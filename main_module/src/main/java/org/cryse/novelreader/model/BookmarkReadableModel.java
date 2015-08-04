package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface BookmarkReadableModel extends BaseModel {

    /**
     * Get the {@code novel_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getNovelId();

    /**
     * Get the {@code chapter_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getChapterId();

    /**
     * Get the {@code novel_title} value.
     * Can be {@code null}.
     */
    @Nullable
    String getNovelTitle();

    /**
     * Get the {@code chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    String getChapterTitle();

    /**
     * Get the {@code chapter_offset} value.
     */
    int getChapterOffset();

    /**
     * Get the {@code mark_type} value.
     */
    int getMarkType();

    /**
     * Get the {@code create_time} value.
     */
    long getCreateTime();
}
