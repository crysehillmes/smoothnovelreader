package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NovelChangeSrcModel implements Parcelable {
    private String id;
    private String title;
    private String bookName;
    private String src;

    public NovelChangeSrcModel(String id, String title, String bookName, String src) {
        this.id = id;
        this.title = title;
        this.bookName = bookName;
        this.src = src;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookname(String bookName) {
        this.bookName = bookName;
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
        dest.writeString(this.title);
        dest.writeString(this.bookName);
        dest.writeString(this.src);
    }

    public NovelChangeSrcModel() {
    }

    private NovelChangeSrcModel(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.bookName = in.readString();
        this.src = in.readString();
    }

    public static final Parcelable.Creator<NovelChangeSrcModel> CREATOR = new Parcelable.Creator<NovelChangeSrcModel>() {
        public NovelChangeSrcModel createFromParcel(Parcel source) {
            return new NovelChangeSrcModel(source);
        }

        public NovelChangeSrcModel[] newArray(int size) {
            return new NovelChangeSrcModel[size];
        }
    };
}
