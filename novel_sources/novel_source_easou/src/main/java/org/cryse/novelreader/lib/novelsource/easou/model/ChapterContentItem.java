package org.cryse.novelreader.lib.novelsource.easou.model;

import com.google.gson.annotations.SerializedName;

public class ChapterContentItem {
    @SerializedName("chapter_name")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("curl")
    private String source;

    @SerializedName("sort")
    private int chapterId;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }
}
