package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cryse on 14-7-6.
 */
public class NovelChapterModel implements Parcelable, Comparable<NovelChapterModel> {
    private String id;
    private String secondId;
    private String src;
    private String title;
    private int chapterIndex;
    private boolean cached = false;

    public NovelChapterModel() {

    }

    public NovelChapterModel(String id, String secondId, String src, String title, int chapterIndex) {
        this.id = id;
        this.secondId = secondId;
        this.src = src;
        this.title = title;
        this.chapterIndex = chapterIndex;
    }

    public int getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(int chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSecondId() {
        return secondId;
    }

    public void setSecondId(String secondId) {
        this.secondId = secondId;
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

    public boolean isCached() {
        return cached;
    }

    public void setCached(boolean cached) {
        this.cached = cached;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.secondId);
        dest.writeString(this.src);
        dest.writeString(this.title);
        dest.writeInt(this.chapterIndex);
        dest.writeByte((byte) (cached ? 1 : 0));
    }

    private NovelChapterModel(Parcel in) {
        this.id = in.readString();
        this.secondId = in.readString();
        this.src = in.readString();
        this.title = in.readString();
        this.chapterIndex = in.readInt();
        this.cached = in.readByte() != 0;
    }

    public static final Parcelable.Creator<NovelChapterModel> CREATOR = new Parcelable.Creator<NovelChapterModel>() {
        public NovelChapterModel createFromParcel(Parcel source) {
            return new NovelChapterModel(source);
        }

        public NovelChapterModel[] newArray(int size) {
            return new NovelChapterModel[size];
        }
    };

    @Override
    public int compareTo(NovelChapterModel o) {
        return ((Integer)this.chapterIndex).compareTo(o.getChapterIndex());
    }
}
