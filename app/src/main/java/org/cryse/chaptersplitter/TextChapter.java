package org.cryse.chaptersplitter;

public class TextChapter {
    private String chapterName;
    private int startLineNumber;
    private int endLineNumber;
    private int chapterSize;

    public TextChapter(String chapterName, int startLineNumber, int endLineNumber) {
        this.chapterName = chapterName;
        this.startLineNumber = startLineNumber;
        this.endLineNumber = endLineNumber;
    }

    public String getChapterName() {
        return chapterName;
    }

    public void setChapterName(String chapterName) {
        this.chapterName = chapterName;
    }

    public int getStartLineNumber() {
        return startLineNumber;
    }

    public void setStartLineNumber(int startLineNumber) {
        this.startLineNumber = startLineNumber;
    }

    public int getEndLineNumber() {
        return endLineNumber;
    }

    public void setEndLineNumber(int endLineNumber) {
        this.endLineNumber = endLineNumber;
    }

    public int getChapterSize() {
        return chapterSize;
    }

    public void setChapterSize(int chapterSize) {
        this.chapterSize = chapterSize;
    }
}