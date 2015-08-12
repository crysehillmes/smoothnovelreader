package org.cryse.novelreader.event;

public class ImportChapterContentEvent extends AbstractEvent {
    private String mNovelId;
    private int mImportedCount;
    public ImportChapterContentEvent(String novelId, int importedCount) {
        this.mNovelId = novelId;
        this.mImportedCount = importedCount;
    }

    public String getNovelId() {
        return mNovelId;
    }

    public int getImportedCount() {
        return mImportedCount;
    }
}
