package org.cryse.novelreader.model;

import android.os.Parcelable;

import java.util.Date;

public interface BookmarkModel extends BookmarkReadableModel, Parcelable {
    public static final int BOOKMARK_TYPE_LASTREAD = 3;
    public static final int BOOKMARK_TYPE_NORMAL = 5;

    void setNovelId(String novelId);

    void setChapterId(String chapterId);

    void setNovelTitle(String novelTitle);

    void setChapterTitle(String chapterTitle);

    void setChapterOffset(Integer chapterOffset);

    void setMarkType(Integer markType);

    void setCreateTime(long createTime);

    void setCreateTime(Date createTime);
}
