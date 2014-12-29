package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

public class NovelBookMarkModel implements Parcelable, Comparable<NovelBookMarkModel> {
    public static final int BOOKMARK_TYPE_LASTREAD = 3;
    public static final int BOOKMARK_TYPE_NORMAL = 5;

    private String id;
    private String chapterTitle;
    private String novelTitle;
    private int chapterIndex;
    private int chapterOffset;
    private int bookMarkType;
    private Date createTime;

    public NovelBookMarkModel(String id, String chapterTitle, String novelTitle, int chapterIndex, int chapterOffset, int bookMarkType, Date createTime) {
        this.id = id;
        this.chapterTitle = chapterTitle;
        this.novelTitle = novelTitle;
        this.chapterIndex = chapterIndex;
        this.chapterOffset = chapterOffset;
        this.bookMarkType = bookMarkType;
        this.createTime = createTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getChapterTitle() {
        return chapterTitle;
    }

    public void setChapterTitle(String chapterTitle) {
        this.chapterTitle = chapterTitle;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public int getChapterOffset() {
        return chapterOffset;
    }

    public void setChapterOffset(int chapterOffset) {
        this.chapterOffset = chapterOffset;
    }

    public int getBookMarkType() {
        return bookMarkType;
    }

    public void setBookMarkType(int bookMarkType) {
        this.bookMarkType = bookMarkType;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public NovelBookMarkModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.chapterTitle);
        dest.writeString(this.novelTitle);
        dest.writeInt(this.chapterIndex);
        dest.writeInt(this.chapterOffset);
        dest.writeInt(this.bookMarkType);
        dest.writeLong(createTime != null ? createTime.getTime() : -1);
    }

    private NovelBookMarkModel(Parcel in) {
        this.id = in.readString();
        this.chapterTitle = in.readString();
        this.novelTitle = in.readString();
        this.chapterIndex = in.readInt();
        this.chapterOffset = in.readInt();
        this.bookMarkType = in.readInt();
        long tmpCreateTime = in.readLong();
        this.createTime = tmpCreateTime == -1 ? null : new Date(tmpCreateTime);
    }

    public static final Creator<NovelBookMarkModel> CREATOR = new Creator<NovelBookMarkModel>() {
        public NovelBookMarkModel createFromParcel(Parcel source) {
            return new NovelBookMarkModel(source);
        }

        public NovelBookMarkModel[] newArray(int size) {
            return new NovelBookMarkModel[size];
        }
    };

    @Override
    public int compareTo(NovelBookMarkModel another) {
        return this.createTime.compareTo(another.getCreateTime());
    }
}
