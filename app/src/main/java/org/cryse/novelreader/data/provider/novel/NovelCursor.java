package org.cryse.novelreader.data.provider.novel;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractCursor;
import org.cryse.novelreader.model.NovelModel;

/**
 * Cursor wrapper for the {@code novel} table.
 */
public class NovelCursor extends AbstractCursor implements NovelModel {
    public NovelCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(NovelColumns._ID);
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
        String res = getStringOrNull(NovelColumns.NOVEL_ID);
        if (res == null)
            throw new NullPointerException("The value of 'novel_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(NovelColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code type} value.
     */
    public int getType() {
        Integer res = getIntegerOrNull(NovelColumns.TYPE);
        if (res == null)
            throw new NullPointerException("The value of 'type' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code source} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getSource() {
        String res = getStringOrNull(NovelColumns.SOURCE);
        return res;
    }

    /**
     * Get the {@code cover_image} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getCoverImage() {
        String res = getStringOrNull(NovelColumns.COVER_IMAGE);
        return res;
    }

    /**
     * Get the {@code chapter_count} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getChapterCount() {
        Integer res = getIntegerOrNull(NovelColumns.CHAPTER_COUNT);
        return res;
    }

    /**
     * Get the {@code last_read_chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getLastReadChapterTitle() {
        String res = getStringOrNull(NovelColumns.LAST_READ_CHAPTER_TITLE);
        return res;
    }

    /**
     * Get the {@code latest_chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getLatestChapterTitle() {
        String res = getStringOrNull(NovelColumns.LATEST_CHAPTER_TITLE);
        return res;
    }

    /**
     * Get the {@code latest_update_chapter_count} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getLatestUpdateChapterCount() {
        Integer res = getIntegerOrNull(NovelColumns.LATEST_UPDATE_CHAPTER_COUNT);
        return res;
    }

    /**
     * Get the {@code sort_key} value.
     * Can be {@code null}.
     */
    @Nullable
    public Long getSortKey() {
        Long res = getLongOrNull(NovelColumns.SORT_KEY);
        return res;
    }
}
