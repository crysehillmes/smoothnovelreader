package org.cryse.novelreader.data.provider.bookmark;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code bookmark} table.
 */
public class BookmarkContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return BookmarkColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable BookmarkSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable BookmarkSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public BookmarkContentValues putNovelId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("novelId must not be null");
        mContentValues.put(BookmarkColumns.NOVEL_ID, value);
        return this;
    }


    public BookmarkContentValues putChapterId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("chapterId must not be null");
        mContentValues.put(BookmarkColumns.CHAPTER_ID, value);
        return this;
    }


    public BookmarkContentValues putNovelTitle(@Nullable String value) {
        mContentValues.put(BookmarkColumns.NOVEL_TITLE, value);
        return this;
    }

    public BookmarkContentValues putNovelTitleNull() {
        mContentValues.putNull(BookmarkColumns.NOVEL_TITLE);
        return this;
    }

    public BookmarkContentValues putChapterTitle(@Nullable String value) {
        mContentValues.put(BookmarkColumns.CHAPTER_TITLE, value);
        return this;
    }

    public BookmarkContentValues putChapterTitleNull() {
        mContentValues.putNull(BookmarkColumns.CHAPTER_TITLE);
        return this;
    }

    public BookmarkContentValues putChapterOffset(int value) {
        mContentValues.put(BookmarkColumns.CHAPTER_OFFSET, value);
        return this;
    }


    public BookmarkContentValues putMarkType(int value) {
        mContentValues.put(BookmarkColumns.MARK_TYPE, value);
        return this;
    }


    public BookmarkContentValues putCreateTime(long value) {
        mContentValues.put(BookmarkColumns.CREATE_TIME, value);
        return this;
    }

}
