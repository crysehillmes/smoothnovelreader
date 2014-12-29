package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by cryse on 14-7-6.
 */
public class NovelChapterContentModel implements Parcelable {
    private String id;
    private String secondId;
    private String content;
    private String src;

    public NovelChapterContentModel(String id, String secondId, String content, String src) {
        this.id = id;
        this.secondId = secondId;
        this.content = content;
        this.src = src;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.secondId);
        dest.writeString(this.content);
        dest.writeString(this.src);
    }

    private NovelChapterContentModel(Parcel in) {
        this.id = in.readString();
        this.secondId = in.readString();
        this.content = in.readString();
        this.src = in.readString();
    }

    public static final Parcelable.Creator<NovelChapterContentModel> CREATOR = new Parcelable.Creator<NovelChapterContentModel>() {
        public NovelChapterContentModel createFromParcel(Parcel source) {
            return new NovelChapterContentModel(source);
        }

        public NovelChapterContentModel[] newArray(int size) {
            return new NovelChapterContentModel[size];
        }
    };
}
