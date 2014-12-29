package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cryse on 14-7-6.
 */
public class NovelModel implements Parcelable, Comparable<NovelModel> {
    private String id;
    private String src;
    private String title;
    private String author;
    private String categoryName;
    private Long follow;
    private String status;
    private String summary;
    private String imageUrl;
    private Integer chapterCount;
    private String lastReadChapterTitle;
    private String latestChapterTitle;
    private Integer latestUpdateCount;
    private Long sortWeight;

    public NovelModel(
            String id,
            String src,
            String title,
            String author,
            String categoryName,
            Long follow,
            String status,
            String summary,
            String imageUrl,
            Integer chapterCount,
            String lastReadChapterTitle,
            String latestChapterTitle,
            Integer latestUpdateCount,
            Long sortWeight
    ) {
        this.id = id;
        this.src = src;
        this.title = title;
        this.author = author;
        this.categoryName = categoryName;
        this.follow = follow;
        this.status = status;
        this.summary = summary;
        this.imageUrl = imageUrl;
        this.chapterCount = chapterCount;
        this.lastReadChapterTitle = lastReadChapterTitle;
        this.latestChapterTitle = latestChapterTitle;
        this.latestUpdateCount = latestUpdateCount;
        this.sortWeight = sortWeight;
    }

    public String getId() {
        return id;
    }

    public void setId(String gid) {
        this.id = gid;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public Long getFollow() {
        return follow;
    }

    public void setFollow(Long follow) {
        this.follow = follow;
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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(Integer chapterCount) {
        this.chapterCount = chapterCount;
    }

    public String getLastReadChapterTitle() {
        return lastReadChapterTitle;
    }

    public void setLastReadChapterTitle(String lastReadChapterTitle) {
        this.lastReadChapterTitle = lastReadChapterTitle;
    }

    public String getLatestChapterTitle() {
        return latestChapterTitle;
    }

    public void setLatestChapterTitle(String latestChapterTitle) {
        this.latestChapterTitle = latestChapterTitle;
    }

    public Integer getLatestUpdateCount() {
        return latestUpdateCount;
    }

    public void setLatestUpdateCount(Integer latestUpdateCount) {
        this.latestUpdateCount = latestUpdateCount;
    }

    public Long getSortWeight() {
        return sortWeight;
    }

    public void setSortWeight(Long sortWeight) {
        this.sortWeight = sortWeight;
    }

    @Override
    public int compareTo(NovelModel another) {
        return this.sortWeight.compareTo(another.getSortWeight());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.src);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeString(this.categoryName);
        dest.writeValue(this.follow);
        dest.writeString(this.status);
        dest.writeString(this.summary);
        dest.writeString(this.imageUrl);
        dest.writeValue(this.chapterCount);
        dest.writeString(this.lastReadChapterTitle);
        dest.writeString(this.latestChapterTitle);
        dest.writeValue(this.latestUpdateCount);
        dest.writeValue(this.sortWeight);
    }

    private NovelModel(Parcel in) {
        this.id = in.readString();
        this.src = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.categoryName = in.readString();
        this.follow = (Long) in.readValue(Long.class.getClassLoader());
        this.status = in.readString();
        this.summary = in.readString();
        this.imageUrl = in.readString();
        this.chapterCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.lastReadChapterTitle = in.readString();
        this.latestChapterTitle = in.readString();
        this.latestUpdateCount = (Integer) in.readValue(Integer.class.getClassLoader());
        this.sortWeight = (Long) in.readValue(Long.class.getClassLoader());
    }

    public static final Creator<NovelModel> CREATOR = new Creator<NovelModel>() {
        public NovelModel createFromParcel(Parcel source) {
            return new NovelModel(source);
        }

        public NovelModel[] newArray(int size) {
            return new NovelModel[size];
        }
    };
}
