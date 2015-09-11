package org.cryse.novelreader.data.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import org.cryse.novelreader.BuildConfig;
import org.cryse.novelreader.constant.DatabaseConstants;

/**
 * Implement your custom database creation or upgrade code here.
 *
 * This file will not be overwritten if you re-run the content provider generator.
 */
public class NovelReaderSQLiteOpenHelperCallbacks {
    private static final boolean DEBUG = BuildConfig.DEBUG && DatabaseConstants.DEBUG_DATABASE;
    private static final String TAG = NovelReaderSQLiteOpenHelperCallbacks.class.getSimpleName();

    public void onOpen(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Log.d(TAG, "onOpen");
        // Insert your db open code here.
    }

    public void onPreCreate(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Log.d(TAG, "onPreCreate");
        // Insert your db creation code here. This is called before your tables are created.
    }

    public void onPostCreate(final Context context, final SQLiteDatabase db) {
        if (DEBUG) Log.d(TAG, "onPostCreate");
        // Insert your db creation code here. This is called after your tables are created.
    }

    public void onUpgrade(final Context context, final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        if (DEBUG) Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion);
        // Insert your upgrading code here.
        if(oldVersion < 2) {
            db.execSQL("DROP TABLE IF EXISTS " + "NOVEL_BOOK_MARK_MODEL;");
            db.execSQL("DROP TABLE IF EXISTS " + "NOVEL_CHAPTER_CONTENT_MODEL;");
            db.execSQL("DROP TABLE IF EXISTS " + "NOVEL_CHAPTER_MODEL;");
            db.execSQL("DROP TABLE IF EXISTS " + "NOVEL_MODEL;");
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_TABLE_BOOKMARK);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_BOOKMARK_NOVEL_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_BOOKMARK_CHAPTER_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_TABLE_CHAPTER);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_CHAPTER_NOVEL_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_CHAPTER_CHAPTER_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_TABLE_CHAPTER_CONTENT);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_CHAPTER_CONTENT_NOVEL_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_CHAPTER_CONTENT_CHAPTER_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_CHAPTER_CONTENT_SOURCE);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_TABLE_NOVEL);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_NOVEL_NOVEL_ID);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_NOVEL_TITLE);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_NOVEL_AUTHOR);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_NOVEL_TYPE);
            db.execSQL(NovelReaderSQLiteOpenHelper.SQL_CREATE_INDEX_NOVEL_SOURCE);
        }
    }
}
