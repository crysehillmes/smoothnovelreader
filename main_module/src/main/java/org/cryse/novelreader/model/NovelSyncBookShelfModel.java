package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NovelSyncBookShelfModel implements Parcelable {
    private String id;
    private String lastChapterTitle;
    private String lastUpdate;

    public NovelSyncBookShelfModel(String id, String lastChapterTitle, String lastUpdate) {
        this.id = id;
        this.lastChapterTitle = lastChapterTitle;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastChapterTitle() {
        return lastChapterTitle;
    }

    public void setLastChapterTitle(String lastChapterTitle) {
        this.lastChapterTitle = lastChapterTitle;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.lastChapterTitle);
        dest.writeString(this.lastUpdate);
    }

    public NovelSyncBookShelfModel() {
    }

    private NovelSyncBookShelfModel(Parcel in) {
        this.id = in.readString();
        this.lastChapterTitle = in.readString();
        this.lastUpdate = in.readString();
    }

    public static final Parcelable.Creator<NovelSyncBookShelfModel> CREATOR = new Parcelable.Creator<NovelSyncBookShelfModel>() {
        public NovelSyncBookShelfModel createFromParcel(Parcel source) {
            return new NovelSyncBookShelfModel(source);
        }

        public NovelSyncBookShelfModel[] newArray(int size) {
            return new NovelSyncBookShelfModel[size];
        }
    };
}
