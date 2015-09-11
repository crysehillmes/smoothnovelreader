package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NovelSyncBookShelfModel implements Parcelable {
    private String id;
    private String lastChapterId;
    private String lastChapterTitle;
    private String lastUpdate;

    public NovelSyncBookShelfModel(String id, String lastChapterId, String lastChapterTitle, String lastUpdate) {
        this.id = id;
        this.lastChapterId = lastChapterId;
        this.lastChapterTitle = lastChapterTitle;
        this.lastUpdate = lastUpdate;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLastChapterId() {
        return lastChapterId;
    }

    public void setLastChapterId(String lastChapterId) {
        this.lastChapterId = lastChapterId;
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

    public NovelSyncBookShelfModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.lastChapterId);
        dest.writeString(this.lastChapterTitle);
        dest.writeString(this.lastUpdate);
    }

    protected NovelSyncBookShelfModel(Parcel in) {
        this.id = in.readString();
        this.lastChapterId = in.readString();
        this.lastChapterTitle = in.readString();
        this.lastUpdate = in.readString();
    }

    public static final Creator<NovelSyncBookShelfModel> CREATOR = new Creator<NovelSyncBookShelfModel>() {
        public NovelSyncBookShelfModel createFromParcel(Parcel source) {
            return new NovelSyncBookShelfModel(source);
        }

        public NovelSyncBookShelfModel[] newArray(int size) {
            return new NovelSyncBookShelfModel[size];
        }
    };
}
