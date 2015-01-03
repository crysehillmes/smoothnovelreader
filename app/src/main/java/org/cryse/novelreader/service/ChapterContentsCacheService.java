package org.cryse.novelreader.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.data.DaoSession;
import org.cryse.novelreader.data.NovelChapterContentModelDao;
import org.cryse.novelreader.data.NovelChapterModelDao;
import org.cryse.novelreader.data.NovelModelDao;
import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.NovelTextFilter;
import org.cryse.novelreader.util.ToastProxy;
import org.cryse.novelreader.util.ToastType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import de.greenrobot.dao.query.DeleteQuery;
import de.greenrobot.dao.query.QueryBuilder;


public class ChapterContentsCacheService extends Service {
    @Inject
    NovelModelDao novelModelDao;

    @Inject
    NovelChapterModelDao novelChapterModelDao;

    @Inject
    NovelChapterContentModelDao novelChapterContentModelDao;

    @Inject
    NovelSource novelSource;

    @Inject
    NovelTextFilter novelTextFilter;

    NotificationManager mNotifyManager;
    static final int CACHING_NOTIFICATION_ID = 110;
    NotificationCompat.Builder mBuilder;
    boolean isCaching = false;
    boolean stopThreadSignal = false;
    Thread mWorkingThread;
    @Override
    public void onCreate() {
        super.onCreate();
        ((SmoothReaderApplication)getApplication()).inject(this);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new ChapterContentsCacheBinder();
    }

    private Boolean isFavoriteSync(String id) {
        Boolean isExist;
        QueryBuilder<NovelModel> qb = novelModelDao.queryBuilder().where(NovelModelDao.Properties.Id.eq(id));
        isExist = qb.count() != 0;
        return isExist;
    }

    private NovelChapterContentModel loadChapterContentFromWeb(String id, String secondId, String src) {
        return novelSource.getChapterContentSync(id, secondId, src);
    }

    private void updateChapterContentInDB(String secondId, String src, NovelChapterContentModel chapterContentModel) {
        DeleteQuery deleteQuery = novelChapterContentModelDao.queryBuilder().whereOr(NovelChapterContentModelDao.Properties.SecondId.eq(secondId), NovelChapterContentModelDao.Properties.Src.eq(src)).buildDelete();
        deleteQuery.executeDeleteWithoutDetachingEntities();
        clearDaoSession();
        novelChapterContentModelDao.insert(chapterContentModel);
    }

    private void clearDaoSession() {
        DaoSession daoSession = (DaoSession)novelModelDao.getSession();
        daoSession.clear();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d("ChapterContentsCacheService", "onStartCommand");
        if(intent.hasExtra("type")) {
            if("cancel_cache".compareTo(intent.getStringExtra("type")) == 0) {
                Log.d("ChapterContentsCacheService", "cancel_cache");
                stopThreadSignal = true;
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void preloadChapterContents(NovelModel novel, final List<NovelChapterModel> chapterModels) {
        if(isCaching) return;

        int chapterCount = chapterModels.size();
        Intent cancelCacheIntent = new Intent(this, ChapterContentsCacheService.class);
        cancelCacheIntent.putExtra("type", "cancel_cache");
        PendingIntent pendingIntent = PendingIntent.getService(this, 0, cancelCacheIntent, 0);        mBuilder = new NotificationCompat.Builder(ChapterContentsCacheService.this);
        mBuilder.setContentTitle(getResources().getString(R.string.notification_chapter_contents_cache_title, novel.getTitle()))
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher)
                .setOngoing(true);
        mBuilder.addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cache_title), pendingIntent);

        isCaching = true;

        startForeground(CACHING_NOTIFICATION_ID, mBuilder.build());

        mWorkingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                int successCount = 0, failureCount = 0;
                List<NovelChapterModel> items = new ArrayList<NovelChapterModel>(chapterCount);
                items.addAll(chapterModels);
                if (isFavoriteSync(novel.getId())) {
                    for (NovelChapterModel chapterModel : items) {
                        if(stopThreadSignal) {
                            stopThreadSignal = false;
                            break;
                        }
                        try {
                            index++;
                            if (chapterModel.isCached()) {
                                successCount++;
                                continue;
                            }
                            NovelChapterContentModel chapterContentModel = loadChapterContentFromWeb(novel.getId(), chapterModel.getSecondId(), chapterModel.getSrc());
                            if (chapterContentModel != null) {
                                updateChapterContentInDB(chapterModel.getSecondId(), chapterModel.getSrc(), chapterContentModel);
                                successCount++;
                            } else {
                                failureCount++;
                            }
                        } catch (Exception ex) {
                            ToastProxy.showToast(ChapterContentsCacheService.this, getString(R.string.toast_generic_error), ToastType.TOAST_INFO);
                        } finally {
                            mBuilder.setProgress(chapterCount, index, false);
                            mBuilder.setContentText(getResources().getString(R.string.novel_chapter_contents_cache_progress, index, chapterCount));
                            mNotifyManager.notify(CACHING_NOTIFICATION_ID, mBuilder.build());
                        }
                    }

                }
                mNotifyManager.cancel(CACHING_NOTIFICATION_ID);
                stopForeground(true);

                // TODO: 新的结束Notification没有显示出来。
                mBuilder = new NotificationCompat.Builder(ChapterContentsCacheService.this);
                mBuilder.setContentTitle(getResources().getString(R.string.notification_chapter_contents_cache_title_finish, novel.getTitle()))
                        .setContentText(getResources().getString(R.string.notification_chapter_contents_cache_content, successCount, failureCount, chapterCount - successCount - failureCount))
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setAutoCancel(true);
                //startForeground(CACHING_NOTIFICATION_ID, mBuilder.build());
                mNotifyManager.notify(CACHING_NOTIFICATION_ID, mBuilder.build());

                //stopForeground(true);
                isCaching = false;
            }
        });
        mWorkingThread.start();
    }

    public class ChapterContentsCacheBinder extends Binder {
        public boolean isCaching() {
            return isCaching;
        }
        public void chaptersOfflineCache(NovelModel novelModel, List<NovelChapterModel> novelChapterModels) {
            preloadChapterContents(novelModel, novelChapterModels);
        }
    }


}
