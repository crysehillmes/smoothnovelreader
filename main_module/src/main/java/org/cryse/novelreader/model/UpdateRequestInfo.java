package org.cryse.novelreader.model;

public class UpdateRequestInfo {
    private int novelType;
    private String novelId;
    private String chapterId;

    public UpdateRequestInfo(int novelType, String novelId, String chapterId) {
        this.novelType = novelType;
        this.novelId = novelId;
        this.chapterId = chapterId;
    }

    public int getNovelType() {
        return novelType;
    }

    public void setNovelType(int novelType) {
        this.novelType = novelType;
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
