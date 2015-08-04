package org.cryse.novelreader.data.provider.chapter;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractCursor;
import org.cryse.novelreader.model.ChapterModel;

/**
 * Cursor wrapper for the {@code chapter} table.
 */
public class ChapterCursor extends AbstractCursor implements ChapterModel {
    public ChapterCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(ChapterColumns._ID);
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
        String res = getStringOrNull(ChapterColumns.NOVEL_ID);
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
        String res = getStringOrNull(ChapterColumns.CHAPTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'chapter_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code source} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getSource() {
        String res = getStringOrNull(ChapterColumns.SOURCE);
        return res;
    }

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    @NonNull
    public String getTitle() {
        String res = getStringOrNull(ChapterColumns.TITLE);
        if (res == null)
            throw new NullPointerException("The value of 'title' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code chapter_index} value.
     * Can be {@code null}.
     */
    @Nullable
    public Integer getChapterIndex() {
        Integer res = getIntegerOrNull(ChapterColumns.CHAPTER_INDEX);
        return res;
    }
}
