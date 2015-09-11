package org.cryse.novelreader.model;

import android.os.Parcelable;

public interface ChapterContentModel extends ChapterContentReadableModel, Parcelable {
    void setNovelId(String novelId);
    void setChapterId(String chapterId);
    void setContent(String content);
    void setSource(String source);
}
