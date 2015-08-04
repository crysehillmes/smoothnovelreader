package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * A human being which is part of a team.
 */
public interface ChapterContentModel extends BaseModel {

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
     * Get the {@code content} value.
     * Can be {@code null}.
     */
    @Nullable
    String getContent();

    /**
     * Get the {@code source} value.
     * Can be {@code null}.
     */
    @Nullable
    String getSource();
}
