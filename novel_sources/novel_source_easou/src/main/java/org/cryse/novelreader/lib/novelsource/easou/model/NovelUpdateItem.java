package org.cryse.novelreader.lib.novelsource.easou.model;

import com.google.gson.annotations.SerializedName;

public class NovelUpdateItem {
    @SerializedName("gid")
    private long gId;

    @SerializedName("nid")
    private long nId;

    @SerializedName("name")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("last_sort")
    private int chapterId;

    @SerializedName("last_chapter_name")
    private String latestChapterTitle;

    public int getChapterNumber() {
        return chapterId;
    }

    public long getGId() {
        return gId;
    }

    public void setGId(long gId) {
        this.gId = gId;
    }

    public long getNId() {
        return nId;
    }

    public void setNId(long nId) {
        this.nId = nId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getChapterId() {
        return chapterId;
    }

    public void setChapterId(int chapterId) {
        this.chapterId = chapterId;
    }

    public String getLatestChapterTitle() {
        return latestChapterTitle;
    }

    public void setLatestChapterTitle(String latestChapterTitle) {
        this.latestChapterTitle = latestChapterTitle;
    }
}
