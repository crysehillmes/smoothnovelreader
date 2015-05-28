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
import android.text.TextUtils;
import android.util.Log;

import org.cryse.chaptersplitter.LocalTextReader;
import org.cryse.chaptersplitter.TextChapter;
import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.data.NovelDatabaseAccessLayer;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.HashUtils;
import org.cryse.novelreader.util.NovelTextFilter;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

public class LoadLocalTextService extends Service {
    private static final String LOG_TAG = LoadLocalTextService.class.getName();
    public static final String LOCAL_FILE_PREFIX = DataContract.LOCAL_FILE_PREFIX;
    @Inject
    NovelDatabaseAccessLayer mNovelDatabase;

    @Inject
    NovelSource novelSource;

    @Inject
    NovelTextFilter novelTextFilter;

    BlockingQueue<ReadLocalTextTask> mTaskQueue = new LinkedBlockingQueue<ReadLocalTextTask>();
    ReadLocalTextTask mCurrentTask = null;
    public static final int NOTIFICATION_START_ID = 1024;
    public int notification_count = 0;

    NotificationManager mNotifyManager;
    static final int CACHING_NOTIFICATION_ID = 450;

    boolean stopCurrentTask = false;
    Thread mCachingThread;
    boolean mIsStopingService;

    @Override
    public void onCreate() {
        super.onCreate();
        ((SmoothReaderApplication)getApplication()).inject(this);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mCachingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (!mIsStopingService) {
                        mCurrentTask = mTaskQueue.take();
                        readChapters(mCurrentTask);
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
        return new ReadLocalTextFileBinder();
    }

    private NovelChapterContentModel loadChapterContentFromWeb(String id, String secondId, String src) {
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

    public void readChapters(ReadLocalTextTask task) {
        String filePath = task.getTextFilePath();
        String customTitle = task.getCustomTitle();

        Log.d(LOG_TAG, "downloadChapters");
        NotificationCompat.Builder progressNotificationBuilder;

        Intent cancelCurrentIntent = new Intent(this, LoadLocalTextService.class);
        cancelCurrentIntent.putExtra("type", "cancel_current");
        PendingIntent cancelCurrentPendingIntent = PendingIntent.getService(this, 0, cancelCurrentIntent, 0);
        Intent cancelAllIntent = new Intent(this, LoadLocalTextService.class);
        cancelAllIntent.putExtra("type", "cancel_all");
        PendingIntent cancelAllPendingIntent = PendingIntent.getService(this, 1, cancelAllIntent, 0);


        progressNotificationBuilder = new NotificationCompat.Builder(LoadLocalTextService.this);
        progressNotificationBuilder.setContentTitle(getResources().getString(R.string.notification_chapter_contents_cache_title, task.getTextFilePath()))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_action_chapter_cache)
                .setOngoing(true)
                .setProgress(0, 0, true)
                .addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_current), cancelCurrentPendingIntent)
                .addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_all), cancelAllPendingIntent);

        startForeground(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());




        LocalTextReader localTextReader = null;
        try {
            File textFile = new File(filePath);
            localTextReader = new LocalTextReader(filePath);
            localTextReader.open();
            String novelId = HashUtils.md5(filePath);
            mNovelDatabase.addToFavorite(new NovelModel(
                    novelId,
                    LOCAL_FILE_PREFIX + ":" + filePath,
                    TextUtils.isEmpty(customTitle) ? textFile.getName() : customTitle,
                    "",
                    "",
                    0l,
                    "",
                    "",
                    "",
                    0,
                    "",
                    "",
                    0,
                    new Date().getTime()
            ));
            int chapterCount = localTextReader.readChapters(new LocalTextReader.OnChapterReadCallback() {

                int chapterIndex = 0;
                @Override
                public void onChapterRead(TextChapter chapter, String content) {
                    String chapterHash = HashUtils.md5(chapter.getChapterName() + String.format("%06d", chapterIndex));

                    mNovelDatabase.insertChapter(novelId, new NovelChapterModel(
                            novelId,
                            chapterHash,
                            LOCAL_FILE_PREFIX,
                            chapter.getChapterName(),
                            chapterIndex
                    ));

                    chapterIndex++;
                    mNovelDatabase.updateChapterContent(new NovelChapterContentModel(
                            novelId,
                            chapterHash,
                            novelTextFilter.filter(content),
                            LOCAL_FILE_PREFIX + ":" + chapterHash
                    ));
                }
            });
            Log.d("CHAPTERS", String.format("Chapter count: %d", chapterCount));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(localTextReader != null) {
                localTextReader.close();
            }
        }

        // TODO: If this is not the last task, do not cancel the progress notification, otherwise cancel it and stopForeground.
        mNotifyManager.cancel(CACHING_NOTIFICATION_ID);
        stopForeground(true);

        // TODO: Show a notification here to let user know one book is cached.
        showTaskResultNotification(
                task.getTextFilePath(),
                task.getCustomTitle()
        );
    }

    private void showTaskResultNotification(String textFilePath, String customTitle) {
        notification_count = notification_count + 1;
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(this);
        Bundle extras = new Bundle();
        extras.putString("text_file_path", textFilePath);
        extras.putString("custom_title", customTitle);
        mResultBuilder.setContentTitle("Finish")
                .setContentText("Finish")
                .setSmallIcon(R.drawable.ic_notification_done)
                .setExtras(extras)
                .setAutoCancel(true);
        mNotifyManager.notify(NOTIFICATION_START_ID + notification_count, mResultBuilder.build());
    }

    public class ReadLocalTextFileBinder extends Binder {
        public boolean isCaching() {
            return mCurrentTask != null;
        }

        public String getCurrentTextFilePath() {
            return mCurrentTask == null ? null : mCurrentTask.getTextFilePath();
        }

        public void addToCacheQueue(String textFilePath, String customTitle) {
            ReadLocalTextTask newTask = new ReadLocalTextTask(textFilePath, customTitle);
            for(ReadLocalTextTask task : mTaskQueue) {
                if(task.getTextFilePath().equalsIgnoreCase(textFilePath)) {
                    return;
                }
            }
            mTaskQueue.add(newTask);
            // preloadChapterContents(novelModel, novelChapterModels);
        }

        public boolean removeFromQueueIfExist(String textFilePath) {
            for(ReadLocalTextTask task : mTaskQueue) {
                if(task.getTextFilePath().equalsIgnoreCase(textFilePath)) {
                    mTaskQueue.remove(task);
                    return true;
                }
            }
            return false;
        }
    }


}
