package org.cryse.novelreader.data.provider.bookmark;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractCursor;
import org.cryse.novelreader.model.BookmarkReadableModel;

/**
 * Cursor wrapper for the {@code bookmark} table.
 */
public class BookmarkCursor extends AbstractCursor implements BookmarkReadableModel {
    public BookmarkCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(BookmarkColumns._ID);
        if (res == null)
            throw new NullPointerException("The value of '_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code novel_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getNovelId() {
        String res = getStringOrNull(BookmarkColumns.NOVEL_ID);
        if (res == null)
            throw new NullPointerException("The value of 'novel_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code chapter_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getChapterId() {
        String res = getStringOrNull(BookmarkColumns.CHAPTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'chapter_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code novel_title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getNovelTitle() {
        String res = getStringOrNull(BookmarkColumns.NOVEL_TITLE);
        return res;
    }

    /**
     * Get the {@code chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getChapterTitle() {
        String res = getStringOrNull(BookmarkColumns.CHAPTER_TITLE);
        return res;
    }

    /**
     * Get the {@code chapter_offset} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getChapterOffset() {
        Integer res = getIntegerOrNull(BookmarkColumns.CHAPTER_OFFSET);
        return res;
    }

    /**
     * Get the {@code mark_type} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getMarkType() {
        Integer res = getIntegerOrNull(BookmarkColumns.MARK_TYPE);
        return res;
    }

    /**
     * Get the {@code create_time} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getCreateTime() {
        Long res = getLongOrNull(BookmarkColumns.CREATE_TIME);
        return res;
    }
}
