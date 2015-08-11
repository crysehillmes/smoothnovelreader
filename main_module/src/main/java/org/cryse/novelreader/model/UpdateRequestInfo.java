package org.cryse.novelreader.model;

public class UpdateRequestInfo {
    private String novelId;
    private String chapterId;

    public UpdateRequestInfo(String novelId, String chapterId) {
        this.novelId = novelId;
        this.chapterId = chapterId;
    }

    public String getNovelId() {
        return novelId;
    }

    public void setNovelId(String novelId) {
        this.novelId = novelId;
    }

    public String getChapterId() {
        return chapterId;
    }

    public void setChapterId(String chapterId) {
        this.chapterId = chapterId;
    }
}
