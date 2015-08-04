package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Chapter implements ChapterModel {
    private String novelId;
    private String chapterId;
    private String source;
    private String title;
    private Integer chapterIndex;

    public Chapter(ChapterReadableModel chapterModel) {
        this.novelId = chapterModel.getNovelId();
        this.chapterId = chapterModel.getChapterId();
        this.source = chapterModel.getSource();
        this.title = chapterModel.getTitle();
        this.chapterIndex = chapterModel.getChapterIndex();
    }

    public Chapter(String novelId, String chapterId, String source, String title, Integer chapterIndex) {
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.source = source;
        this.title = title;
        this.chapterIndex = chapterIndex;
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
    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    @Nullable
    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }
}
