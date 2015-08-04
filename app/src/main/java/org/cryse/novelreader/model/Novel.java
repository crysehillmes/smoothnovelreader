package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Novel implements NovelModel {
    private String novelId;
    private String title;
    private String author;
    private int type;
    private String source;
    private String coverImage;
    private Integer chapterCount;
    private String lastReadChapterTitle;
    private String latestChapterTitle;
    private Integer latestUpdateChapterCount;
    private Long sortKey;

    public Novel(String novelId, String title, int type, String source, String coverImage) {
        this.novelId = novelId;
        this.title = title;
        this.type = type;
        this.source = source;
        this.coverImage = coverImage;
    }

    public Novel(NovelReadableModel model) {
        this.novelId = model.getNovelId();
        this.title = model.getTitle();
        this.author = model.getAuthor();
        this.type = model.getType();
        this.source = model.getSource();
        this.coverImage = model.getCoverImage();
        this.chapterCount = model.getChapterCount();
        this.lastReadChapterTitle = model.getLastReadChapterTitle();
        this.latestChapterTitle = model.getLatestChapterTitle();
        this.latestUpdateChapterCount = model.getLatestUpdateChapterCount();
        this.sortKey = model.getSortKey();
    }

    @NonNull
    @Override
    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Nullable
    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Nullable
    @Override
    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @Nullable
    @Override
    public Integer getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    @Nullable
    @Override
    public String getLastReadChapterTitle() {
        return lastReadChapterTitle;
    }

    public void setLastReadChapterTitle(String lastReadChapterTitle) {
        this.lastReadChapterTitle = lastReadChapterTitle;
    }

    @Nullable
    @Override
    public String getLatestChapterTitle() {
        return latestChapterTitle;
    }

    public void setLatestChapterTitle(String latestChapterTitle) {
        this.latestChapterTitle = latestChapterTitle;
    }


    @Override
    public Integer getLatestUpdateChapterCount() {
        return latestUpdateChapterCount;
    }

    public void setLatestUpdateChapterCount(Integer latestUpdateChapterCount) {
        this.latestUpdateChapterCount = latestUpdateChapterCount;
    }

    @Nullable
    @Override
    public Long getSortKey() {
        return sortKey;
    }

    public void setSortKey(Long sortKey) {
        this.sortKey = sortKey;
    }
}
