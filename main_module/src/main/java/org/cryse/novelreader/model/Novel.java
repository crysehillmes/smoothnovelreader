package org.cryse.novelreader.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.Date;

public class Novel implements NovelModel {
    public static final Creator<Novel> CREATOR = new Creator<Novel>() {
        public Novel createFromParcel(Parcel source) {
            return new Novel(source);
        }

        public Novel[] newArray(int size) {
            return new Novel[size];
        }
    };
    private String novelId;
    private String title;
    private String author;
    private int type;
    private String source;
    private String coverImage;
    private int chapterCount;
    private String lastReadChapterTitle;
    private String latestChapterTitle;
    private String latestChapterId;
    private int latestUpdateChapterCount;
    private long sortKey;
    // Optional field below
    private String status;
    private String summary;
    private String category;

    public Novel(String novelId, String title, String author, int type, String source, String coverImage) {
        this.novelId = novelId;
        this.title = title;
        this.author = author;
        this.type = type;
        this.source = source;
        this.coverImage = coverImage;
        this.chapterCount = 0;
        this.latestUpdateChapterCount = 0;
        this.sortKey = new Date().getTime();
    }

    public Novel(NovelReadableModel model) {
        this.novelId = model.getNovelId();
        this.title = model.getTitle();
        this.author = model.getAuthor();
        this.type = model.getType();
        this.source = model.getSource();
        this.coverImage = model.getCoverImage();
        this.chapterCount = model.getChapterCount();
        this.lastReadChapterTitle = model.getLastReadChapterTitle();
        this.latestChapterTitle = model.getLatestChapterTitle();
        this.latestUpdateChapterCount = model.getLatestUpdateChapterCount();
        this.sortKey = model.getSortKey();
    }

    protected Novel(Parcel in) {
        this.novelId = in.readString();
        this.title = in.readString();
        this.author = in.readString();
        this.type = in.readInt();
        this.source = in.readString();
        this.coverImage = in.readString();
        this.chapterCount = in.readInt();
        this.lastReadChapterTitle = in.readString();
        this.latestChapterTitle = in.readString();
        this.latestChapterId = in.readString();
        this.latestUpdateChapterCount = in.readInt();
        this.sortKey = in.readLong();
        this.status = in.readString();
        this.summary = in.readString();
        this.category = in.readString();
    }

    @NonNull
    @Override
    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    @NonNull
    @Override
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    @Override
    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    @Override
    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @NonNull
    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    @Nullable
    @Override
    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    @Override
    public int getChapterCount() {
        return chapterCount;
    }

    public void setChapterCount(int chapterCount) {
        this.chapterCount = chapterCount;
    }

    @Nullable
    @Override
    public String getLastReadChapterTitle() {
        return lastReadChapterTitle;
    }

    public void setLastReadChapterTitle(String lastReadChapterTitle) {
        this.lastReadChapterTitle = lastReadChapterTitle;
    }

    @Nullable
    @Override
    public String getLatestChapterTitle() {
        return latestChapterTitle;
    }

    public void setLatestChapterTitle(String latestChapterTitle) {
        this.latestChapterTitle = latestChapterTitle;
    }

    @Nullable
    @Override
    public String getLatestChapterId() {
        return latestChapterId;
    }

    @Override
    public void setLatestChapterId(String latestChapterId) {
        this.latestChapterId = latestChapterId;
    }

    @Override
    public int getLatestUpdateChapterCount() {
        return latestUpdateChapterCount;
    }

    public void setLatestUpdateChapterCount(int latestUpdateChapterCount) {
        this.latestUpdateChapterCount = latestUpdateChapterCount;
    }

    @Override
    public long getSortKey() {
        return sortKey;
    }

    public void setSortKey(long sortKey) {
        this.sortKey = sortKey;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.novelId);
        dest.writeString(this.title);
        dest.writeString(this.author);
        dest.writeInt(this.type);
        dest.writeString(this.source);
        dest.writeString(this.coverImage);
        dest.writeInt(this.chapterCount);
        dest.writeString(this.lastReadChapterTitle);
        dest.writeString(this.latestChapterTitle);
        dest.writeString(this.latestChapterId);
        dest.writeInt(this.latestUpdateChapterCount);
        dest.writeLong(this.sortKey);
        dest.writeString(this.status);
        dest.writeString(this.summary);
        dest.writeString(this.category);
    }
}
