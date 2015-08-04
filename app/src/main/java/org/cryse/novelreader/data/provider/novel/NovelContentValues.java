package org.cryse.novelreader.data.provider.novel;

import java.util.Date;

import android.content.Context;
import android.content.ContentResolver;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractContentValues;

/**
 * Content values wrapper for the {@code novel} table.
 */
public class NovelContentValues extends AbstractContentValues {
    @Override
    public Uri uri() {
        return NovelColumns.CONTENT_URI;
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(ContentResolver contentResolver, @Nullable NovelSelection where) {
        return contentResolver.update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    /**
     * Update row(s) using the values stored by this object and the given selection.
     *
     * @param contentResolver The content resolver to use.
     * @param where The selection to use (can be {@code null}).
     */
    public int update(Context context, @Nullable NovelSelection where) {
        return context.getContentResolver().update(uri(), values(), where == null ? null : where.sel(), where == null ? null : where.args());
    }

    public NovelContentValues putNovelId(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("novelId must not be null");
        mContentValues.put(NovelColumns.NOVEL_ID, value);
        return this;
    }


    public NovelContentValues putTitle(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("title must not be null");
        mContentValues.put(NovelColumns.TITLE, value);
        return this;
    }


    public NovelContentValues putAuthor(@Nullable String value) {
        mContentValues.put(NovelColumns.AUTHOR, value);
        return this;
    }

    public NovelContentValues putAuthorNull() {
        mContentValues.putNull(NovelColumns.AUTHOR);
        return this;
    }

    public NovelContentValues putType(int value) {
        mContentValues.put(NovelColumns.TYPE, value);
        return this;
    }


    public NovelContentValues putSource(@NonNull String value) {
        if (value == null) throw new IllegalArgumentException("source must not be null");
        mContentValues.put(NovelColumns.SOURCE, value);
        return this;
    }


    public NovelContentValues putCoverImage(@Nullable String value) {
        mContentValues.put(NovelColumns.COVER_IMAGE, value);
        return this;
    }

    public NovelContentValues putCoverImageNull() {
        mContentValues.putNull(NovelColumns.COVER_IMAGE);
        return this;
    }

    public NovelContentValues putChapterCount(int value) {
        mContentValues.put(NovelColumns.CHAPTER_COUNT, value);
        return this;
    }


    public NovelContentValues putLastReadChapterTitle(@Nullable String value) {
        mContentValues.put(NovelColumns.LAST_READ_CHAPTER_TITLE, value);
        return this;
    }

    public NovelContentValues putLastReadChapterTitleNull() {
        mContentValues.putNull(NovelColumns.LAST_READ_CHAPTER_TITLE);
        return this;
    }

    public NovelContentValues putLatestChapterTitle(@Nullable String value) {
        mContentValues.put(NovelColumns.LATEST_CHAPTER_TITLE, value);
        return this;
    }

    public NovelContentValues putLatestChapterTitleNull() {
        mContentValues.putNull(NovelColumns.LATEST_CHAPTER_TITLE);
        return this;
    }

    public NovelContentValues putLatestUpdateChapterCount(int value) {
        mContentValues.put(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT, value);
        return this;
    }


    public NovelContentValues putSortKey(long value) {
        mContentValues.put(NovelColumns.SORT_KEY, value);
        return this;
    }

}
