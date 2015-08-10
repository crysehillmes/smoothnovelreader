package org.cryse.novelreader.event;

public class ImportChapterContentEvent extends AbstractEvent {
    private int importedCount;
    public ImportChapterContentEvent(int importedCount) {
        this.importedCount = importedCount;
    }

    public int getImportedCount() {
        return importedCount;
    }
}
