package org.cryse.novelreader.lib.novelsource.easou.model;

import com.google.gson.annotations.SerializedName;

public class ChapterItem {
    @SerializedName("sort")
    private int chapterIndex;

    @SerializedName("sort")
    private int chapterId;

    @SerializedName("chapter_name")
    private String title;

    @SerializedName("curl")
    private String source;

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
