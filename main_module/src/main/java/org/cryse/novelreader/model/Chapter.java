package org.cryse.novelreader.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Chapter implements ChapterModel {
    private String novelId;
    private String chapterId;
    private String source;
    private String title;
    private Integer chapterIndex;
    private Boolean isCached;

    public Chapter(ChapterReadableModel chapterModel) {
        this.novelId = chapterModel.getNovelId();
        this.chapterId = chapterModel.getChapterId();
        this.source = chapterModel.getSource();
        this.title = chapterModel.getTitle();
        this.chapterIndex = chapterModel.getChapterIndex();
    }

    public Chapter(String novelId, String chapterId, String source, String title, Integer chapterIndex) {
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.source = source;
        this.title = title;
        this.chapterIndex = chapterIndex;
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
    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    @Nullable
    @Override
    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
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
    public Integer getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(Integer chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    @Override
    public Boolean isCached() {
        return isCached;
    }

    @Override
    public void setIsCached(Boolean isCached) {
        this.isCached = isCached;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.novelId);
        dest.writeString(this.chapterId);
        dest.writeString(this.source);
        dest.writeString(this.title);
        dest.writeValue(this.chapterIndex);
        dest.writeValue(this.isCached);
    }

    protected Chapter(Parcel in) {
        this.novelId = in.readString();
        this.chapterId = in.readString();
        this.source = in.readString();
        this.title = in.readString();
        this.chapterIndex = (Integer) in.readValue(Integer.class.getClassLoader());
        this.isCached = (Boolean) in.readValue(Boolean.class.getClassLoader());
    }

    public static final Creator<Chapter> CREATOR = new Creator<Chapter>() {
        public Chapter createFromParcel(Parcel source) {
            return new Chapter(source);
        }

        public Chapter[] newArray(int size) {
            return new Chapter[size];
        }
    };
}
