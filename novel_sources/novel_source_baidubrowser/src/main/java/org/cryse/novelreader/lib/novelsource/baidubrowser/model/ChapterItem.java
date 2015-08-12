package org.cryse.novelreader.lib.novelsource.baidubrowser.model;

import com.google.gson.annotations.SerializedName;

public class ChapterItem {
    @SerializedName("idx")
    private String chapterIndex;

    @SerializedName("id")
    private String chapterId;

    @SerializedName("title")
    private String title;

    @SerializedName("link")
    private String source;

    @SerializedName("cid")
    private String cid;

    public String getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(String chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
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

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
