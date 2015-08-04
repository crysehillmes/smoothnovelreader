package org.cryse.novelreader.model;

public interface ChapterModel extends ChapterReadableModel {
    void setNovelId(String novelId);
    void setChapterId(String chapterId);
    void setSource(String source);
    void setTitle(String title);
    void setChapterIndex(Integer chapterIndex);
    Boolean isCached();
    void setIsCached(Boolean isCached);
}
