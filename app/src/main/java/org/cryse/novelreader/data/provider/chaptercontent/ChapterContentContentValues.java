package org.cryse.novelreader.data.provider.chaptercontent;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code chapter_content} table.
 */
public class ChapterContentContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return ChapterContentColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable ChapterContentSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable ChapterContentSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public ChapterContentContentValues putNovelId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("novelId must not be null");
        mContentValues.put(ChapterContentColumns.NOVEL_ID, value);
        return this;
    }


    public ChapterContentContentValues putChapterId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("chapterId must not be null");
        mContentValues.put(ChapterContentColumns.CHAPTER_ID, value);
        return this;
    }


    public ChapterContentContentValues putContent(@Nullable String value) {
        mContentValues.put(ChapterContentColumns.CONTENT, value);
        return this;
    }

    public ChapterContentContentValues putContentNull() {
        mContentValues.putNull(ChapterContentColumns.CONTENT);
        return this;
    }

    public ChapterContentContentValues putSource(@Nullable String value) {
        mContentValues.put(ChapterContentColumns.SOURCE, value);
        return this;
    }

    public ChapterContentContentValues putSourceNull() {
        mContentValues.putNull(ChapterContentColumns.SOURCE);
        return this;
    }
}
