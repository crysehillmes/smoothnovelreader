package org.cryse.novelreader.service;

import android.os.Parcel;
import android.os.Parcelable;

import org.cryse.novelreader.model.NovelModel;

public class NovelCacheTask implements Parcelable {
    private NovelModel novelModel;

    public NovelCacheTask(NovelModel novelModel) {
        this.novelModel = novelModel;
    }

    public NovelModel getNovelModel() {
        return novelModel;
    }

    public String getNovelId() {
        return novelModel.getNovelId();
    }

    public String getNovelTitle() {
        return novelModel.getTitle();
    }

    public NovelCacheTask() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(this.novelModel, 0);
    }

    protected NovelCacheTask(Parcel in) {
        this.novelModel = in.readParcelable(NovelModel.class.getClassLoader());
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
