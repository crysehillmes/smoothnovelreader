package org.cryse.novelreader.model;

import android.os.Parcelable;

public interface ChapterModel extends ChapterReadableModel, Parcelable {
    void setNovelId(String novelId);
    void setChapterId(String chapterId);
    void setSource(String source);
    void setTitle(String title);
    void setChapterIndex(int chapterIndex);
    boolean isCached();
    void setIsCached(boolean isCached);
}
