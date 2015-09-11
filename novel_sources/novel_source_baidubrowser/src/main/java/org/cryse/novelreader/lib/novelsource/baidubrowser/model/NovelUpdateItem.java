package org.cryse.novelreader.lib.novelsource.baidubrowser.model;

import com.google.gson.annotations.SerializedName;

public class NovelUpdateItem {
    @SerializedName("id")
    private String novelId;

    @SerializedName("idx")
    private String chapterIndex;

    @SerializedName("chp_id")
    private String chapterId;

    @SerializedName("title")
    private String latestChapterTitle;

    @SerializedName("chap_num")
    private String chapterNumber;

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public String getChapterIndex() {
        return chapterIndex;
    }

    public void setChapterIndex(String chapterIndex) {
        this.chapterIndex = chapterIndex;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }

    public String getLatestChapterTitle() {
        return latestChapterTitle;
    }

    public void setLatestChapterTitle(String latestChapterTitle) {
        this.latestChapterTitle = latestChapterTitle;
    }

    public String getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(String chapterNumber) {
        this.chapterNumber = chapterNumber;
    }
}
