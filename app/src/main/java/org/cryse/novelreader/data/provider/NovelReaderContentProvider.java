package org.cryse.novelreader.data.provider;

import java.util.Arrays;

import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import org.cryse.novelreader.data.BuildConfig;
import org.cryse.novelreader.data.provider.base.BaseContentProvider;
import org.cryse.novelreader.data.provider.bookmark.BookmarkColumns;
import org.cryse.novelreader.data.provider.chapter.ChapterColumns;
import org.cryse.novelreader.data.provider.chaptercontent.ChapterContentColumns;
import org.cryse.novelreader.data.provider.novel.NovelColumns;

public class NovelReaderContentProvider extends BaseContentProvider {
    private static final String TAG = NovelReaderContentProvider.class.getSimpleName();

    private static final boolean DEBUG = BuildConfig.DEBUG;

    private static final String TYPE_CURSOR_ITEM = "vnd.android.cursor.item/";
    private static final String TYPE_CURSOR_DIR = "vnd.android.cursor.dir/";

    public static final String AUTHORITY = "org.cryse.novelreader.data.provider";
    public static final String CONTENT_URI_BASE = "content://" + AUTHORITY;

    private static final int URI_TYPE_BOOKMARK = 0;
    private static final int URI_TYPE_BOOKMARK_ID = 1;

    private static final int URI_TYPE_CHAPTER = 2;
    private static final int URI_TYPE_CHAPTER_ID = 3;

    private static final int URI_TYPE_CHAPTER_CONTENT = 4;
    private static final int URI_TYPE_CHAPTER_CONTENT_ID = 5;

    private static final int URI_TYPE_NOVEL = 6;
    private static final int URI_TYPE_NOVEL_ID = 7;



    private static final UriMatcher URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        URI_MATCHER.addURI(AUTHORITY, BookmarkColumns.TABLE_NAME, URI_TYPE_BOOKMARK);
        URI_MATCHER.addURI(AUTHORITY, BookmarkColumns.TABLE_NAME + "/#", URI_TYPE_BOOKMARK_ID);
        URI_MATCHER.addURI(AUTHORITY, ChapterColumns.TABLE_NAME, URI_TYPE_CHAPTER);
        URI_MATCHER.addURI(AUTHORITY, ChapterColumns.TABLE_NAME + "/#", URI_TYPE_CHAPTER_ID);
        URI_MATCHER.addURI(AUTHORITY, ChapterContentColumns.TABLE_NAME, URI_TYPE_CHAPTER_CONTENT);
        URI_MATCHER.addURI(AUTHORITY, ChapterContentColumns.TABLE_NAME + "/#", URI_TYPE_CHAPTER_CONTENT_ID);
        URI_MATCHER.addURI(AUTHORITY, NovelColumns.TABLE_NAME, URI_TYPE_NOVEL);
        URI_MATCHER.addURI(AUTHORITY, NovelColumns.TABLE_NAME + "/#", URI_TYPE_NOVEL_ID);
    }

    @Override
    protected SQLiteOpenHelper createSqLiteOpenHelper() {
        return NovelReaderSQLiteOpenHelper.getInstance(getContext());
    }

    @Override
    protected boolean hasDebug() {
        return DEBUG;
    }

    @Override
    public String getType(Uri uri) {
        int match = URI_MATCHER.match(uri);
        switch (match) {
            case URI_TYPE_BOOKMARK:
                return TYPE_CURSOR_DIR + BookmarkColumns.TABLE_NAME;
            case URI_TYPE_BOOKMARK_ID:
                return TYPE_CURSOR_ITEM + BookmarkColumns.TABLE_NAME;

            case URI_TYPE_CHAPTER:
                return TYPE_CURSOR_DIR + ChapterColumns.TABLE_NAME;
            case URI_TYPE_CHAPTER_ID:
                return TYPE_CURSOR_ITEM + ChapterColumns.TABLE_NAME;

            case URI_TYPE_CHAPTER_CONTENT:
                return TYPE_CURSOR_DIR + ChapterContentColumns.TABLE_NAME;
            case URI_TYPE_CHAPTER_CONTENT_ID:
                return TYPE_CURSOR_ITEM + ChapterContentColumns.TABLE_NAME;

            case URI_TYPE_NOVEL:
                return TYPE_CURSOR_DIR + NovelColumns.TABLE_NAME;
            case URI_TYPE_NOVEL_ID:
                return TYPE_CURSOR_ITEM + NovelColumns.TABLE_NAME;

        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DEBUG) Log.d(TAG, "insert uri=" + uri + " values=" + values);
        return super.insert(uri, values);
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        if (DEBUG) Log.d(TAG, "bulkInsert uri=" + uri + " values.length=" + values.length);
        return super.bulkInsert(uri, values);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "update uri=" + uri + " values=" + values + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.update(uri, values, selection, selectionArgs);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DEBUG) Log.d(TAG, "delete uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs));
        return super.delete(uri, selection, selectionArgs);
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DEBUG)
            Log.d(TAG, "query uri=" + uri + " selection=" + selection + " selectionArgs=" + Arrays.toString(selectionArgs) + " sortOrder=" + sortOrder
                    + " groupBy=" + uri.getQueryParameter(QUERY_GROUP_BY) + " having=" + uri.getQueryParameter(QUERY_HAVING) + " limit=" + uri.getQueryParameter(QUERY_LIMIT));
        return super.query(uri, projection, selection, selectionArgs, sortOrder);
    }

    @Override
    protected QueryParams getQueryParams(Uri uri, String selection, String[] projection) {
        QueryParams res = new QueryParams();
        String id = null;
        int matchedId = URI_MATCHER.match(uri);
        switch (matchedId) {
            case URI_TYPE_BOOKMARK:
            case URI_TYPE_BOOKMARK_ID:
                res.table = BookmarkColumns.TABLE_NAME;
                res.idColumn = BookmarkColumns._ID;
                res.tablesWithJoins = BookmarkColumns.TABLE_NAME;
                res.orderBy = BookmarkColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_CHAPTER:
            case URI_TYPE_CHAPTER_ID:
                res.table = ChapterColumns.TABLE_NAME;
                res.idColumn = ChapterColumns._ID;
                res.tablesWithJoins = ChapterColumns.TABLE_NAME;
                res.orderBy = ChapterColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_CHAPTER_CONTENT:
            case URI_TYPE_CHAPTER_CONTENT_ID:
                res.table = ChapterContentColumns.TABLE_NAME;
                res.idColumn = ChapterContentColumns._ID;
                res.tablesWithJoins = ChapterContentColumns.TABLE_NAME;
                res.orderBy = ChapterContentColumns.DEFAULT_ORDER;
                break;

            case URI_TYPE_NOVEL:
            case URI_TYPE_NOVEL_ID:
                res.table = NovelColumns.TABLE_NAME;
                res.idColumn = NovelColumns._ID;
                res.tablesWithJoins = NovelColumns.TABLE_NAME;
                res.orderBy = NovelColumns.DEFAULT_ORDER;
                break;

            default:
                throw new IllegalArgumentException("The uri '" + uri + "' is not supported by this ContentProvider");
        }

        switch (matchedId) {
            case URI_TYPE_BOOKMARK_ID:
            case URI_TYPE_CHAPTER_ID:
            case URI_TYPE_CHAPTER_CONTENT_ID:
            case URI_TYPE_NOVEL_ID:
                id = uri.getLastPathSegment();
        }
        if (id != null) {
            if (selection != null) {
                res.selection = res.table + "." + res.idColumn + "=" + id + " and (" + selection + ")";
            } else {
                res.selection = res.table + "." + res.idColumn + "=" + id;
            }
        } else {
            res.selection = selection;
        }
        return res;
    }
}
