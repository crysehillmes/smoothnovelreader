package org.cryse.novelreader.lib.novelsource.baidubrowser.model;

import com.google.gson.annotations.SerializedName;

public class SearchNovelItem {
    @SerializedName("id")
    private String novelId;

    @SerializedName("name")
    private String title;

    @SerializedName("author")
    private String author;

    @SerializedName("type")
    private String category;

    @SerializedName("status")
    private String status;

    @SerializedName("summary")
    private String summary;

    @SerializedName("cover")
    private String coverImage;

    @SerializedName("link")
    private String source;

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
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
