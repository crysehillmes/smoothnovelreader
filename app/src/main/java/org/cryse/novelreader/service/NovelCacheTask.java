package org.cryse.novelreader.service;

import android.os.Parcel;
import android.os.Parcelable;

public class NovelCacheTask implements Parcelable {
    private String novelId;
    private String novelTitle;

    public NovelCacheTask(String novelId, String novelTitle) {
        this.novelId = novelId;
        this.novelTitle = novelTitle;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public String getNovelTitle() {
        return novelTitle;
    }

    public void setNovelTitle(String novelTitle) {
        this.novelTitle = novelTitle;
    }

    public NovelCacheTask() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.novelId);
        dest.writeString(this.novelTitle);
    }

    private NovelCacheTask(Parcel in) {
        this.novelId = in.readString();
        this.novelTitle = in.readString();
    }

    public static final Creator<NovelCacheTask> CREATOR = new Creator<NovelCacheTask>() {
        public NovelCacheTask createFromParcel(Parcel source) {
            return new NovelCacheTask(source);
        }

        public NovelCacheTask[] newArray(int size) {
            return new NovelCacheTask[size];
        }
    };
}
