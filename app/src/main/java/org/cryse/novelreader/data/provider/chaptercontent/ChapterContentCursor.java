package org.cryse.novelreader.data.provider.chaptercontent;

import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.cryse.novelreader.data.provider.base.AbstractCursor;
import org.cryse.novelreader.model.ChapterContentModel;

/**
 * Cursor wrapper for the {@code chapter_content} table.
 */
public class ChapterContentCursor extends AbstractCursor implements ChapterContentModel {
    public ChapterContentCursor(Cursor cursor) {
        super(cursor);
    }

    /**
     * Primary key.
     */
    public long getId() {
        Long res = getLongOrNull(ChapterContentColumns._ID);
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
        String res = getStringOrNull(ChapterContentColumns.NOVEL_ID);
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
        String res = getStringOrNull(ChapterContentColumns.CHAPTER_ID);
        if (res == null)
            throw new NullPointerException("The value of 'chapter_id' in the database was null, which is not allowed according to the model definition");
        return res;
    }

    /**
     * Get the {@code content} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getContent() {
        String res = getStringOrNull(ChapterContentColumns.CONTENT);
        return res;
    }

    /**
     * Get the {@code source} value.
     * Can be {@code null}.
     */
    @Nullable
    public String getSource() {
        String res = getStringOrNull(ChapterContentColumns.SOURCE);
        return res;
    }
}
