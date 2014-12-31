package org.cryse.novelreader.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

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
    Notification mNotification;
    Thread mWorkingThread;
    @Override
    public void onCreate() {
        super.onCreate();
        ((SmoothReaderApplication)getApplication()).inject(this);
        mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(ChapterContentsCacheService.this);
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

    private void preloadChapterContents(NovelModel novel, final List<NovelChapterModel> chapterModels) {
        if(isCaching) return;
        int chapterCount = chapterModels.size();

        mBuilder.setContentTitle("正在缓存《" + novel.getTitle() + "》章节")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_launcher);
        mBuilder.setProgress(chapterCount, 0, false);
        mNotification = mBuilder.build();
        isCaching = true;
        startForeground(CACHING_NOTIFICATION_ID, mNotification);
        mWorkingThread = new Thread(new Runnable() {
            @Override
            public void run() {
                int index = 0;
                int successCount = 0, failureCount = 0;
                List<NovelChapterModel> items = new ArrayList<NovelChapterModel>(chapterCount);
                items.addAll(chapterModels);
                if (isFavoriteSync(novel.getId())) {
                    for (NovelChapterModel chapterModel : items) {
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
                stopForeground(true);
                ToastProxy.showToast(ChapterContentsCacheService.this, getResources().getString(R.string.toast_chapter_contents_cache, successCount, failureCount), ToastType.TOAST_INFO);
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
