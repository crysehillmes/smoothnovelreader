package org.cryse.novelreader.model;

import android.os.Parcel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ChapterContent implements ChapterContentModel {
    private String novelId;
    private String chapterId;
    private String source;
    private String content;

    public ChapterContent(ChapterContentReadableModel model) {
        this.novelId = model.getNovelId();
        this.chapterId = model.getChapterId();
        this.source = model.getSource();
        this.content = model.getContent();
    }

    public ChapterContent(String novelId, String chapterId, String source, String content) {
        this.novelId = novelId;
        this.chapterId = chapterId;
        this.source = source;
        this.content = content;
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

    @Nullable
    @Override
    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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
        dest.writeString(this.content);
    }

    protected ChapterContent(Parcel in) {
        this.novelId = in.readString();
        this.chapterId = in.readString();
        this.source = in.readString();
        this.content = in.readString();
    }

    public static final Creator<ChapterContent> CREATOR = new Creator<ChapterContent>() {
        public ChapterContent createFromParcel(Parcel source) {
            return new ChapterContent(source);
        }

        public ChapterContent[] newArray(int size) {
            return new ChapterContent[size];
        }
    };
}
