package org.cryse.novelreader.model;

public interface ChapterContentModel extends ChapterContentReadableModel {
    void setNovelId(String novelId);
    void setChapterId(String chapterId);
    void setContent(String content);
    void setSource(String source);
}
