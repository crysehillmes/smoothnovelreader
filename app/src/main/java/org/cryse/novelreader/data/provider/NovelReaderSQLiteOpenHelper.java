package org.cryse.novelreader.data.provider;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.DefaultDatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import org.cryse.novelreader.BuildConfig;
import org.cryse.novelreader.data.provider.bookmark.BookmarkColumns;
import org.cryse.novelreader.data.provider.chapter.ChapterColumns;
import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentColumns;
import org.cryse.novelreader.data.provider.novel.NovelColumns;

public class NovelReaderSQLiteOpenHelper extends SQLiteOpenHelper {
    private static final String TAG = NovelReaderSQLiteOpenHelper.class.getSimpleName();

    public static final String DATABASE_FILE_NAME = "novelreader.db";
    private static final int DATABASE_VERSION = 2;
    private static NovelReaderSQLiteOpenHelper sInstance;
    private final Context mContext;
    private final NovelReaderSQLiteOpenHelperCallbacks mOpenHelperCallbacks;

    // @formatter:off
    public static final String SQL_CREATE_TABLE_BOOKMARK = "CREATE TABLE IF NOT EXISTS "
            + BookmarkColumns.TABLE_NAME + " ( "
            + BookmarkColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + BookmarkColumns.NOVEL_ID + " TEXT NOT NULL, "
            + BookmarkColumns.CHAPTER_ID + " TEXT NOT NULL, "
            + BookmarkColumns.NOVEL_TITLE + " TEXT, "
            + BookmarkColumns.CHAPTER_TITLE + " TEXT, "
            + BookmarkColumns.CHAPTER_OFFSET + " INTEGER NOT NULL, "
            + BookmarkColumns.MARK_TYPE + " INTEGER NOT NULL, "
            + BookmarkColumns.CREATE_TIME + " INTEGER NOT NULL "
            + " );";

    public static final String SQL_CREATE_INDEX_BOOKMARK_NOVEL_ID = "CREATE INDEX IDX_BOOKMARK_NOVEL_ID "
            + " ON " + BookmarkColumns.TABLE_NAME + " ( " + BookmarkColumns.NOVEL_ID + " );";

    public static final String SQL_CREATE_INDEX_BOOKMARK_CHAPTER_ID = "CREATE INDEX IDX_BOOKMARK_CHAPTER_ID "
            + " ON " + BookmarkColumns.TABLE_NAME + " ( " + BookmarkColumns.CHAPTER_ID + " );";

    public static final String SQL_CREATE_TABLE_CHAPTER = "CREATE TABLE IF NOT EXISTS "
            + ChapterColumns.TABLE_NAME + " ( "
            + ChapterColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ChapterColumns.NOVEL_ID + " TEXT NOT NULL, "
            + ChapterColumns.CHAPTER_ID + " TEXT NOT NULL, "
            + ChapterColumns.SOURCE + " TEXT, "
            + ChapterColumns.TITLE + " TEXT NOT NULL, "
            + ChapterColumns.CHAPTER_INDEX + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_chapter UNIQUE (novel_id, chapter_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_CHAPTER_NOVEL_ID = "CREATE INDEX IDX_CHAPTER_NOVEL_ID "
            + " ON " + ChapterColumns.TABLE_NAME + " ( " + ChapterColumns.NOVEL_ID + " );";

    public static final String SQL_CREATE_INDEX_CHAPTER_CHAPTER_ID = "CREATE INDEX IDX_CHAPTER_CHAPTER_ID "
            + " ON " + ChapterColumns.TABLE_NAME + " ( " + ChapterColumns.CHAPTER_ID + " );";

    public static final String SQL_CREATE_TABLE_CHAPTER_CONTENT = "CREATE TABLE IF NOT EXISTS "
            + ChapterContentColumns.TABLE_NAME + " ( "
            + ChapterContentColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + ChapterContentColumns.NOVEL_ID + " TEXT NOT NULL, "
            + ChapterContentColumns.CHAPTER_ID + " TEXT NOT NULL, "
            + ChapterContentColumns.SOURCE + " TEXT, "
            + ChapterContentColumns.CONTENT + " TEXT "
            + ", CONSTRAINT unique_chapter_content UNIQUE (chapter_id, source) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_CHAPTER_CONTENT_NOVEL_ID = "CREATE INDEX IDX_CHAPTER_CONTENT_NOVEL_ID "
            + " ON " + ChapterContentColumns.TABLE_NAME + " ( " + ChapterContentColumns.NOVEL_ID + " );";

    public static final String SQL_CREATE_INDEX_CHAPTER_CONTENT_CHAPTER_ID = "CREATE INDEX IDX_CHAPTER_CONTENT_CHAPTER_ID "
            + " ON " + ChapterContentColumns.TABLE_NAME + " ( " + ChapterContentColumns.CHAPTER_ID + " );";

    public static final String SQL_CREATE_INDEX_CHAPTER_CONTENT_SOURCE = "CREATE INDEX IDX_CHAPTER_CONTENT_SOURCE "
            + " ON " + ChapterContentColumns.TABLE_NAME + " ( " + ChapterContentColumns.SOURCE + " );";

    public static final String SQL_CREATE_TABLE_NOVEL = "CREATE TABLE IF NOT EXISTS "
            + NovelColumns.TABLE_NAME + " ( "
            + NovelColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + NovelColumns.NOVEL_ID + " TEXT NOT NULL, "
            + NovelColumns.TITLE + " TEXT NOT NULL, "
            + NovelColumns.AUTHOR + " TEXT, "
            + NovelColumns.TYPE + " INTEGER NOT NULL, "
            + NovelColumns.SOURCE + " TEXT NOT NULL, "
            + NovelColumns.COVER_IMAGE + " TEXT, "
            + NovelColumns.CHAPTER_COUNT + " INTEGER NOT NULL, "
            + NovelColumns.LAST_READ_CHAPTER_TITLE + " TEXT, "
            + NovelColumns.LATEST_CHAPTER_TITLE + " TEXT, "
            + NovelColumns.LATEST_UPDATE_CHAPTER_COUNT + " INTEGER NOT NULL, "
            + NovelColumns.SORT_KEY + " INTEGER NOT NULL "
            + ", CONSTRAINT unique_novel_id UNIQUE (novel_id) ON CONFLICT REPLACE"
            + " );";

    public static final String SQL_CREATE_INDEX_NOVEL_NOVEL_ID = "CREATE INDEX IDX_NOVEL_NOVEL_ID "
            + " ON " + NovelColumns.TABLE_NAME + " ( " + NovelColumns.NOVEL_ID + " );";

    public static final String SQL_CREATE_INDEX_NOVEL_TITLE = "CREATE INDEX IDX_NOVEL_TITLE "
            + " ON " + NovelColumns.TABLE_NAME + " ( " + NovelColumns.TITLE + " );";

    public static final String SQL_CREATE_INDEX_NOVEL_AUTHOR = "CREATE INDEX IDX_NOVEL_AUTHOR "
            + " ON " + NovelColumns.TABLE_NAME + " ( " + NovelColumns.AUTHOR + " );";

    public static final String SQL_CREATE_INDEX_NOVEL_TYPE = "CREATE INDEX IDX_NOVEL_TYPE "
            + " ON " + NovelColumns.TABLE_NAME + " ( " + NovelColumns.TYPE + " );";

    public static final String SQL_CREATE_INDEX_NOVEL_SOURCE = "CREATE INDEX IDX_NOVEL_SOURCE "
            + " ON " + NovelColumns.TABLE_NAME + " ( " + NovelColumns.SOURCE + " );";

    // @formatter:on

    public static NovelReaderSQLiteOpenHelper getInstance(Context context) {
        // Use the application context, which will ensure that you
        // don't accidentally leak an Activity's context.
        // See this article for more information: http://bit.ly/6LRzfx
        if (sInstance == null) {
            sInstance = newInstance(context.getApplicationContext());
        }
        return sInstance;
    }

    private static NovelReaderSQLiteOpenHelper newInstance(Context context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            return newInstancePreHoneycomb(context);
        }
        return newInstancePostHoneycomb(context);
    }


    /*
     * Pre Honeycomb.
     */
    private static NovelReaderSQLiteOpenHelper newInstancePreHoneycomb(Context context) {
        return new NovelReaderSQLiteOpenHelper(context);
    }

    private NovelReaderSQLiteOpenHelper(Context context) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION);
        mContext = context;
        mOpenHelperCallbacks = new NovelReaderSQLiteOpenHelperCallbacks();
    }


    /*
     * Post Honeycomb.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private static NovelReaderSQLiteOpenHelper newInstancePostHoneycomb(Context context) {
        return new NovelReaderSQLiteOpenHelper(context, new DefaultDatabaseErrorHandler());
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private NovelReaderSQLiteOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_FILE_NAME, null, DATABASE_VERSION, errorHandler);
        mContext = context;
        mOpenHelperCallbacks = new NovelReaderSQLiteOpenHelperCallbacks();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        if (BuildConfig.DEBUG) Log.d(TAG, "onCreate");
        mOpenHelperCallbacks.onPreCreate(mContext, db);
        db.execSQL(SQL_CREATE_TABLE_BOOKMARK);
        db.execSQL(SQL_CREATE_INDEX_BOOKMARK_NOVEL_ID);
        db.execSQL(SQL_CREATE_INDEX_BOOKMARK_CHAPTER_ID);
        db.execSQL(SQL_CREATE_TABLE_CHAPTER);
        db.execSQL(SQL_CREATE_INDEX_CHAPTER_NOVEL_ID);
        db.execSQL(SQL_CREATE_INDEX_CHAPTER_CHAPTER_ID);
        db.execSQL(SQL_CREATE_TABLE_CHAPTER_CONTENT);
        db.execSQL(SQL_CREATE_INDEX_CHAPTER_CONTENT_NOVEL_ID);
        db.execSQL(SQL_CREATE_INDEX_CHAPTER_CONTENT_CHAPTER_ID);
        db.execSQL(SQL_CREATE_INDEX_CHAPTER_CONTENT_SOURCE);
        db.execSQL(SQL_CREATE_TABLE_NOVEL);
        db.execSQL(SQL_CREATE_INDEX_NOVEL_NOVEL_ID);
        db.execSQL(SQL_CREATE_INDEX_NOVEL_TITLE);
        db.execSQL(SQL_CREATE_INDEX_NOVEL_AUTHOR);
        db.execSQL(SQL_CREATE_INDEX_NOVEL_TYPE);
        db.execSQL(SQL_CREATE_INDEX_NOVEL_SOURCE);
        mOpenHelperCallbacks.onPostCreate(mContext, db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            setForeignKeyConstraintsEnabled(db);
        }
        mOpenHelperCallbacks.onOpen(mContext, db);
    }

    private void setForeignKeyConstraintsEnabled(SQLiteDatabase db) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
            setForeignKeyConstraintsEnabledPreJellyBean(db);
        } else {
            setForeignKeyConstraintsEnabledPostJellyBean(db);
        }
    }

    private void setForeignKeyConstraintsEnabledPreJellyBean(SQLiteDatabase db) {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void setForeignKeyConstraintsEnabledPostJellyBean(SQLiteDatabase db) {
        db.setForeignKeyConstraintsEnabled(true);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        mOpenHelperCallbacks.onUpgrade(mContext, db, oldVersion, newVersion);
    }
}
