package org.cryse.novelreader.model;

public interface NovelModel extends NovelReadableModel {
    void setNovelId(String novelId);
    void setTitle(String title);
    void setAuthor(String author);
    void setType(int type);
    void setSource(String source);
    void setCoverImage(String coverImage);
    void setChapterCount(Integer chapterCount);
    void setLastReadChapterTitle(String lastReadChapterTitle);
    void setLatestChapterTitle(String latestChapterTitle);
    void setLatestUpdateChapterCount(Integer latestUpdateChapterCount);
    void setSortKey(Long sortKey);
}
