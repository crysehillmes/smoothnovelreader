package org.cryse.novelreader.model;

import android.os.Parcelable;

public interface NovelModel extends NovelReadableModel, Parcelable {
    int TYPE_BAIDU_SOURCE = 12;
    int TYPE_BAIDU_BROWSER_SOURCE = 14;
    int TYPE_EASOU_SOURCE = 16;
    int TYPE_LOCAL_FILE = 22;
    void setNovelId(String novelId);
    void setTitle(String title);
    void setAuthor(String author);
    void setType(int type);
    void setSource(String source);
    void setCoverImage(String coverImage);
    void setChapterCount(int chapterCount);
    void setLastReadChapterTitle(String lastReadChapterTitle);
    void setLatestChapterTitle(String latestChapterTitle);
    void setLatestChapterId(String latestChapterId);
    void setLatestUpdateChapterCount(int latestUpdateChapterCount);
    void setSortKey(long sortKey);
}
