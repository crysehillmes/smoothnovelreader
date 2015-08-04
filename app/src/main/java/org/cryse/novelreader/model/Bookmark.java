package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class Bookmark implements BookmarkModel {
    private String novelId;
    private String chapterId;
    private String novelTitle;
    private String chapterTitle;
    private Integer chapterOffset;
    private Integer markType;
    private Date createTime;

    public Bookmark(String novelId,
                    String chapterId,
                    String novelTitle,
                    String chapterTitle,
                    Integer chapterOffset,
                    Integer markType,
                    Date createTime
    ) {
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.novelTitle = novelTitle;
        this.chapterTitle = chapterTitle;
        this.chapterOffset = chapterOffset;
        this.markType = markType;
        this.createTime = createTime;
    }

    public Bookmark(BookmarkReadableModel model) {
        this.novelId = model.getNovelId();
        this.chapterId = model.getChapterId();
        this.novelTitle = model.getNovelTitle();
        this.chapterTitle = model.getChapterTitle();
        this.chapterOffset = model.getChapterOffset();
        this.markType = model.getMarkType();
        this.createTime = model.getCreateTime() == null ? new Date() : new Date(model.getCreateTime());
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
    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    @Nullable
    @Override
    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    @Nullable
    @Override
    public Integer getChapterOffset() {
        return chapterOffset;
    }

    public void setChapterOffset(Integer chapterOffset) {
        this.chapterOffset = chapterOffset;
    }

    @Nullable
    @Override
    public Integer getMarkType() {
        return markType;
    }

    public void setMarkType(Integer markType) {
        this.markType = markType;
    }

    public Date getCreateDateline() {
        return createTime;
    }

    @Nullable
    @Override
    public Long getCreateTime() {
        return createTime.getTime();
    }

    public void setCreateTime(long createTime) {
        this.createTime = new Date(createTime);
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
