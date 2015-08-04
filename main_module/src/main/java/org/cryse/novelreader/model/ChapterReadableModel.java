package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface ChapterReadableModel extends BaseModel {

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
     * Get the {@code source} value.
     * Can be {@code null}.
     */
    @Nullable
    String getSource();

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * Get the {@code chapter_index} value.
     * Can be {@code null}.
     */
    @Nullable
    Integer getChapterIndex();
}
