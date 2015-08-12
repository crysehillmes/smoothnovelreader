package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public interface NovelReadableModel extends BaseModel {

    /**
     * Get the {@code novel_id} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getNovelId();

    /**
     * Get the {@code title} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getTitle();

    /**
     * Get the {@code author} value.
     * Can be {@code null}.
     */
    @Nullable
    String getAuthor();

    /**
     * Get the {@code type} value.
     */
    int getType();

    /**
     * Get the {@code source} value.
     * Cannot be {@code null}.
     */
    @NonNull
    String getSource();

    /**
     * Get the {@code cover_image} value.
     * Can be {@code null}.
     */
    @Nullable
    String getCoverImage();

    /**
     * Get the {@code chapter_count} value.
     */
    int getChapterCount();

    /**
     * Get the {@code last_read_chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLastReadChapterTitle();

    /**
     * Get the {@code latest_chapter_title} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLatestChapterTitle();

    /**
     * Get the {@code latest_chapter_id} value.
     * Can be {@code null}.
     */
    @Nullable
    String getLatestChapterId();

    /**
     * Get the {@code latest_update_chapter_count} value.
     */
    int getLatestUpdateChapterCount();

    /**
     * Get the {@code sort_key} value.
     */
    long getSortKey();
}
