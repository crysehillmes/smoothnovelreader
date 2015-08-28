package org.cryse.novelreader.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.application.module.ChapterCacheModule;
import org.cryse.novelreader.constant.CacheConstants;
import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.event.ImportChapterContentEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.ui.NovelChapterListActivity;
import org.cryse.novelreader.util.NovelTextFilter;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

public class ChapterContentsCacheService extends Service {
    public static final int NOTIFICATION_START_ID = 150;
    static final int CACHING_NOTIFICATION_ID = 110;
    private static final String LOG_TAG = ChapterContentsCacheService.class.getName();
    public int notification_count = 0;
    @Inject
    NovelDatabaseAccessLayer mNovelDatabase;
    @Inject
    NovelSource novelSource;
    @Inject
    NovelTextFilter novelTextFilter;
    RxEventBus mEventBus = RxEventBus.getInstance();
    BlockingQueue<NovelCacheTask> mTaskQueue = new LinkedBlockingQueue<NovelCacheTask>();
    NovelCacheTask mCurrentTask = null;
    NotificationManager mNotifyManager;
    boolean stopCurrentTask = false;
    Thread mCachingThread;
    boolean mIsStopingService;

    @Override
    public void onCreate() {
        super.onCreate();
        SmoothReaderApplication.get(this)
                .getAppComponent()
                .plus(new ChapterCacheModule(this))
                .inject(this);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mCachingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!mIsStopingService) {
                        mCurrentTask = mTaskQueue.take();
                        downloadChapters(mCurrentTask);
                        mCurrentTask = null;
                    }
                } catch (InterruptedException ex) {
                    Log.e(LOG_TAG, "Caching thread exception.", ex);
                }
            }
        });
        mCachingThread.start();
    }

    @Override
    public void onDestroy() {
        mIsStopingService = true;
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ChapterContentsCacheBinder();
    }

    private ChapterContentModel loadChapterContentFromWeb(String id, String secondId, String src) {
        return novelSource.getChapterContentSync(id, secondId, src);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(LOG_TAG, "onStartCommand");
        if(intent != null && intent.hasExtra("type")) {

            Log.d(LOG_TAG, String.format("onStartCommand: %s", intent.getStringExtra("type")));
            if("cancel_current".compareTo(intent.getStringExtra("type")) == 0) {
                Log.d(LOG_TAG, "onStartCommand, type: cancel_current");
                stopCurrentTask = true;
            } else if("cancel_all".compareTo(intent.getStringExtra("type")) == 0) {
                Log.d(LOG_TAG, "onStartCommand, type: cancel_all");
                mTaskQueue.clear();
                stopCurrentTask = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void downloadChapters(NovelCacheTask cacheTask) {
        Log.d(LOG_TAG, "downloadChapters");
        NotificationCompat.Builder progressNotificationBuilder;

        Intent cancelCurrentIntent = new Intent(this, ChapterContentsCacheService.class);
        cancelCurrentIntent.putExtra("type", "cancel_current");
        PendingIntent cancelCurrentPendingIntent = PendingIntent.getService(this, 0, cancelCurrentIntent, 0);
        Intent cancelAllIntent = new Intent(this, ChapterContentsCacheService.class);
        cancelAllIntent.putExtra("type", "cancel_all");
        PendingIntent cancelAllPendingIntent = PendingIntent.getService(this, 1, cancelAllIntent, 0);


        progressNotificationBuilder = new NotificationCompat.Builder(ChapterContentsCacheService.this);
        progressNotificationBuilder.setContentTitle(getResources().getString(R.string.notification_chapter_contents_cache_title, cacheTask.getNovelTitle()))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_notification_download)
                .setOngoing(true)
                .addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_current), cancelCurrentPendingIntent)
                .addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_all), cancelAllPendingIntent);

        startForeground(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());


        int index = 0;
        int successCount = 0, failureCount = 0;
        List<ChapterModel> items = mNovelDatabase.loadChapters(cacheTask.getNovelId());
        int chapterCount = items.size();
        int importBulkCount = 0;
        int percent = 0;
        for (ChapterModel chapterModel : items) {
            if (stopCurrentTask) {
                stopCurrentTask = false;
                break;
            }
            try {
                index++;
                importBulkCount++;
                if (chapterModel.isCached()) {
                    successCount++;
                    continue;
                }
                ChapterContentModel chapterContentModel = loadChapterContentFromWeb(cacheTask.getNovelId(), chapterModel.getChapterId(), chapterModel.getSource());
                if (chapterContentModel != null) {
                    chapterContentModel.setContent(novelTextFilter.filter(chapterContentModel.getContent()));
                    mNovelDatabase.updateChapterContent(chapterContentModel);
                    successCount++;
                } else {
                    failureCount++;
                }
            } catch (Exception ex) {
                failureCount++;
            } finally {
                if(importBulkCount >= CacheConstants.CONST_BULK_INSERT_COUNT) {
                    mEventBus.sendEvent(new ImportChapterContentEvent(cacheTask.getNovelId(), importBulkCount));
                    importBulkCount = 0;
                }

                int newPercent = (int)Math.floor((((float)index)/((float)chapterCount)) * 100);
                if(newPercent > percent) {
                    percent = newPercent;
                    progressNotificationBuilder
                            .setProgress(chapterCount, index, false)
                            .setContentText(buildProgressContentString(percent, index, chapterCount));
                    mNotifyManager.notify(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());
                }

            }
        }

        // TODO: If this is not the last task, do not cancel the progress notification, otherwise cancel it and stopForeground.
        mNotifyManager.cancel(CACHING_NOTIFICATION_ID);
        stopForeground(true);

        // TODO: Show a notification here to let user know one book is cached.
        showTaskResultNotification(
                cacheTask,
                successCount,
                failureCount,
                chapterCount
        );
    }

    private void showTaskResultNotification(NovelCacheTask cacheTask, int successCount, int failureCount, int chapterCount) {
        notification_count = notification_count + 1;
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(this);
        Intent openChaptersActivityIntent = new Intent(this, NovelChapterListActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(DataContract.NOVEL_OBJECT_NAME, cacheTask.getNovelModel());
        openChaptersActivityIntent.putExtras(mBundle);
        PendingIntent chaptersListIntent =
                PendingIntent.getActivity(this, 0, openChaptersActivityIntent, PendingIntent.FLAG_ONE_SHOT);

        Bundle extras = new Bundle();
        extras.putString("novel_id", cacheTask.getNovelId());
        extras.putString("novel_title", cacheTask.getNovelTitle());
        mResultBuilder.setContentTitle(getResources().getString(R.string.notification_chapter_contents_cache_title_finish, cacheTask.getNovelTitle()))
                .setContentText(getResources().getString(R.string.notification_chapter_contents_cache_content, successCount, failureCount, chapterCount - successCount - failureCount))
                .setSmallIcon(R.drawable.ic_notification_done)
                .setExtras(extras)
                .setContentIntent(chaptersListIntent)
                .setAutoCancel(true);
        mNotifyManager.notify(NOTIFICATION_START_ID + notification_count, mResultBuilder.build());
    }

    private String buildProgressContentString(int percent, int index, int chapterCount) {
        String result;
        if(mTaskQueue.size() == 0) {
            result = getResources().getString(R.string.novel_chapter_contents_cache_progress_0, percent);
        } else if(mTaskQueue.size() == 1) {
            Iterator<NovelCacheTask> iterator = mTaskQueue.iterator();
            if(iterator.hasNext()) {
                NovelCacheTask task = iterator.next();
                result = getResources().getString(R.string.novel_chapter_contents_cache_progress_1, percent, task.getNovelTitle());
            } else {
                result = getResources().getString(R.string.novel_chapter_contents_cache_progress_3, percent, mTaskQueue.size());
            }
        } else {
            Iterator<NovelCacheTask> iterator = mTaskQueue.iterator();
            if(iterator.hasNext()) {
                NovelCacheTask task = iterator.next();
                result = getResources().getString(R.string.novel_chapter_contents_cache_progress_2, percent, task.getNovelTitle(), mTaskQueue.size());
            } else {
                result = getResources().getString(R.string.novel_chapter_contents_cache_progress_3, percent, mTaskQueue.size());
            }
        }
        return result;
    }

    public class ChapterContentsCacheBinder extends Binder {
        public boolean isCaching() {
            return mCurrentTask != null;
        }

        public String getCurrentCachingNovelId() {
            return mCurrentTask == null ? null : mCurrentTask.getNovelId();
        }

        public void addToCacheQueue(NovelModel novelModel) {
            NovelCacheTask novelCacheTask = new NovelCacheTask(novelModel);
            for(NovelCacheTask task : mTaskQueue) {
                if(task.getNovelId().equalsIgnoreCase(novelModel.getNovelId())) {
                    return;
                }
            }
            mTaskQueue.add(novelCacheTask);
            // preloadChapterContents(novelModel, novelChapterModels);
        }

        public boolean removeFromQueueIfExist(String novelId) {
            for(NovelCacheTask task : mTaskQueue) {
                if(task.getNovelId().equalsIgnoreCase(novelId)) {
                    mTaskQueue.remove(task);
                    return true;
                }
            }
            return false;
        }
    }


}
