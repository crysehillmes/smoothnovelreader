package org.cryse.novelreader.data.provider.chapter;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code chapter} table.
 */
public class ChapterContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return ChapterColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable ChapterSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable ChapterSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public ChapterContentValues putNovelId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("novelId must not be null");
        mContentValues.put(ChapterColumns.NOVEL_ID, value);
        return this;
    }


    public ChapterContentValues putChapterId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("chapterId must not be null");
        mContentValues.put(ChapterColumns.CHAPTER_ID, value);
        return this;
    }


    public ChapterContentValues putSource(@Nullable String value) {
        mContentValues.put(ChapterColumns.SOURCE, value);
        return this;
    }

    public ChapterContentValues putSourceNull() {
        mContentValues.putNull(ChapterColumns.SOURCE);
        return this;
    }

    public ChapterContentValues putTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("title must not be null");
        mContentValues.put(ChapterColumns.TITLE, value);
        return this;
    }


    public ChapterContentValues putChapterIndex(int value) {
        mContentValues.put(ChapterColumns.CHAPTER_INDEX, value);
        return this;
    }

}
