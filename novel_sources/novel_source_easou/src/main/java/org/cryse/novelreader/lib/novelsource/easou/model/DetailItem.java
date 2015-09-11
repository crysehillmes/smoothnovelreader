package org.cryse.novelreader.lib.novelsource.easou.model;

import com.google.gson.annotations.SerializedName;

public class DetailItem {
    @SerializedName("gid")
    private long gId;

    @SerializedName("nid")
    private long nId;

    @SerializedName("name")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("category")
    private String category;

    @SerializedName("status")
    private int status;

    @SerializedName("desc")
    private String summary;

    @SerializedName("img_url")
    private String coverImage;

    @SerializedName("last_sort")
    private String chapterNumber;

    @SerializedName("curl")
    private String source;

    @SerializedName("last_chapter_name")
    private String lastChapterTitle;

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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }
}

