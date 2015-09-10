package org.cryse.novelreader.lib.novelsource.easou.model;

import com.google.gson.annotations.SerializedName;

public class SearchNovelItem {
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
    private String status;

    @SerializedName("desc")
    private String summary;

    @SerializedName("imgUrl")
    private String coverImage;

    @SerializedName("site")
    private String source;

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
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

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }
}
