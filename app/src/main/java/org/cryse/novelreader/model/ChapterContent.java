package org.cryse.novelreader.model;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChapterContent implements ChapterContentModel{
    private String novelId;
    private String chapterId;
    private String source;
    private String content;

    public ChapterContent(ChapterContentReadableModel model) {
        this.novelId = model.getNovelId();
        this.chapterId = model.getChapterId();
        this.source = model.getSource();
        this.content = model.getContent();
    }

    public ChapterContent(String novelId, String chapterId, String source, String content) {
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.source = source;
        this.content = content;
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

    @Nullable
    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
