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
import org.cryse.novelreader.event.ImportChapterContentEvent;
import org.cryse.novelreader.event.LoadLocalFileDoneEvent;
import org.cryse.novelreader.event.LoadLocalFileStartEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.model.Chapter;
import org.cryse.novelreader.model.ChapterContent;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.NovelSource;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.HashUtils;
import org.cryse.novelreader.util.NovelTextFilter;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.inject.Inject;

public class LoadLocalTextService extends Service {
    private static final String LOG_TAG = LoadLocalTextService.class.getName();
    public static final String LOCAL_FILE_PREFIX = DataContract.LOCAL_FILE_PREFIX;
    public static final int MAX_CHAPTER_CHAR_COUNT_LIMIT = 20000;
    @Inject
    NovelDatabaseAccessLayer mNovelDatabase;

    @Inject
    NovelSource novelSource;

    @Inject
    NovelTextFilter novelTextFilter;

    @Inject
    RxEventBus mEventBus;

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

    public void readChapters(ReadLocalTextTask task) {
        String filePath = task.getTextFilePath();
        File file = new File(filePath);
        String fileName = file.getName();
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
        progressNotificationBuilder
                .setContentTitle(
                        getString(
                                R.string.notification_read_local_file_title,
                                customTitle == null ? fileName : customTitle
                        )
                )
                .setContentText(
                        mTaskQueue.size() > 0 ?
                        getString(
                                R.string.notification_read_local_file_content,
                                mTaskQueue.size()
                        ) : ""
                )
                .setSmallIcon(R.drawable.ic_notification_open_local)
                .setOngoing(true)
                .setProgress(100, 0, false);
                //.addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_current), cancelCurrentPendingIntent)
                //.addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_all), cancelAllPendingIntent);

        startForeground(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());




        LocalTextReader localTextReader = null;
        try {
            File textFile = new File(filePath);
            localTextReader = new LocalTextReader(filePath);
            localTextReader.open();
            String novelId = LOCAL_FILE_PREFIX + ":" + HashUtils.md5(filePath);
            mNovelDatabase.addToFavorite(new Novel(
                    novelId,
                    TextUtils.isEmpty(customTitle) ? textFile.getName() : customTitle,
                    "",
                    NovelModel.TYPE_LOCAL_FILE,
                    LOCAL_FILE_PREFIX + ":" + filePath,
                    ""
            ));
            mEventBus.sendEvent(new LoadLocalFileStartEvent());
            localTextReader.setOnReadProgressListener(percent -> {
                progressNotificationBuilder
                        .setProgress(100, percent, false)
                        .setContentText(
                                getResources().getString(
                                        R.string.notification_read_local_file_content_progress,
                                        percent,
                                        mTaskQueue.size()
                                )
                        );
                startForeground(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());
            });
            int chapterCount = localTextReader.readChapters(new LocalTextReader.OnChapterReadCallback() {

                int chapterIndex = 0;
                int importBulkCount = 0;
                @Override
                public void onChapterRead(TextChapter chapter, String content) {

                    if(LocalTextReader.trimNoSymbolEqual(chapter.getChapterName(),content))
                        return;
                    if(content.length() > MAX_CHAPTER_CHAR_COUNT_LIMIT) {
                        StringBuilder stringBuilder = new StringBuilder(content);
                        int subChapterId = 1;
                        while(stringBuilder.length() > MAX_CHAPTER_CHAR_COUNT_LIMIT) {
                            for(int charIndex = MAX_CHAPTER_CHAR_COUNT_LIMIT; charIndex >= 0; charIndex--) {
                                if(stringBuilder.charAt(charIndex) == '\n') {
                                    addChapterToDatabase(
                                            novelId,
                                            chapterIndex,
                                            chapter.getChapterName() + " [" + Integer.toString(subChapterId) + "]",
                                            stringBuilder.substring(0, charIndex)
                                    );
                                    subChapterId++;
                                    chapterIndex++;
                                    importBulkCount++;
                                    stringBuilder.delete(0, charIndex);
                                    break;
                                }
                            }
                        }
                        addChapterToDatabase(
                                novelId,
                                chapterIndex,
                                chapter.getChapterName() + "[" + Integer.toString(subChapterId) + "]",
                                stringBuilder.toString()
                        );
                        chapterIndex++;
                        importBulkCount++;
                    } else {
                        addChapterToDatabase(novelId, chapterIndex, chapter.getChapterName(), content);
                        chapterIndex++;
                        importBulkCount++;
                    }
                    if(importBulkCount >= DataContract.NOVEL_IMPORT_BULK_COUNT) {
                        mEventBus.sendEvent(new ImportChapterContentEvent(DataContract.NOVEL_IMPORT_BULK_COUNT));
                        importBulkCount = 0;
                    }
                }
            });
            Log.d("CHAPTERS", String.format("Chapter count: %d", chapterCount));
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(localTextReader != null) {
                localTextReader.close();
            }
            mEventBus.sendEvent(new LoadLocalFileDoneEvent());
        }

        // TODO: If this is not the last task, do not cancel the progress notification, otherwise cancel it and stopForeground.
        mNotifyManager.cancel(CACHING_NOTIFICATION_ID);
        stopForeground(true);

        // TODO: Show a notification here to let user know one book is cached.
        showTaskResultNotification(
                filePath,
                fileName,
                customTitle
        );
    }

    private void addChapterToDatabase(String novelId, int chapterIndex, String chapterTitle, String chapterContent) {
        String chapterHash = HashUtils.md5(chapterTitle + String.format("%06d", chapterIndex));
        mNovelDatabase.insertChapter(novelId, new Chapter(
                novelId,
                chapterHash,
                LOCAL_FILE_PREFIX,
                chapterTitle,
                chapterIndex
        ));
        mNovelDatabase.updateChapterContent(new ChapterContent(
                novelId,
                chapterHash,
                LOCAL_FILE_PREFIX + ":" + chapterHash,
                novelTextFilter.filter(chapterContent)
        ));
    }

    private void showTaskResultNotification(String textFilePath, String fileName, String customTitle) {
        notification_count = notification_count + 1;
        NotificationCompat.Builder mResultBuilder = new NotificationCompat.Builder(this);
        Bundle extras = new Bundle();
        extras.putString("text_file_path", textFilePath);
        extras.putString("custom_title", customTitle);
        mResultBuilder
                .setContentTitle(
                        getString(
                                R.string.notification_read_local_file_finish_title,
                                customTitle == null ? fileName : customTitle
                        )
                )
                .setContentText(getString(R.string.notification_read_local_file_finish_content))
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
            updateQueueCountInNotification();
            // preloadChapterContents(novelModel, novelChapterModels);
        }

        public boolean removeFromQueueIfExist(String textFilePath) {
            for(ReadLocalTextTask task : mTaskQueue) {
                if(task.getTextFilePath().equalsIgnoreCase(textFilePath)) {
                    mTaskQueue.remove(task);
                    updateQueueCountInNotification();
                    return true;
                }
            }
            return false;
        }
    }

    protected void updateQueueCountInNotification() {
        if(mCurrentTask == null)
            return;
        String filePath = mCurrentTask.getTextFilePath();
        File file = new File(filePath);
        String fileName = file.getName();
        String customTitle = mCurrentTask.getCustomTitle();
        NotificationCompat.Builder progressNotificationBuilder = new NotificationCompat.Builder(LoadLocalTextService.this);
        progressNotificationBuilder
                .setContentTitle(
                        getString(
                                R.string.notification_read_local_file_title,
                                customTitle == null ? fileName : customTitle
                        )
                )
                .setContentText(
                        mTaskQueue.size() > 0 ?
                                getString(
                                        R.string.notification_read_local_file_content,
                                        mTaskQueue.size()
                                ) : ""
                )
                .setSmallIcon(R.drawable.ic_notification_open_local)
                .setOngoing(true)
                .setProgress(0, 0, true);
        //.addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_current), cancelCurrentPendingIntent)
        //.addAction(R.drawable.ic_action_close, getString(R.string.notification_action_chapter_contents_cancel_all), cancelAllPendingIntent);

        startForeground(CACHING_NOTIFICATION_ID, progressNotificationBuilder.build());
    }
}
