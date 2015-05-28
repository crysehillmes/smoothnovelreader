package org.cryse.novelreader.service;

public class ReadLocalTextTask {
    String textFilePath;
    String customTitle;

    public ReadLocalTextTask(String textFilePath, String customTitle) {
        this.textFilePath = textFilePath;
        this.customTitle = customTitle;
    }

    public String getTextFilePath() {
        return textFilePath;
    }

    public void setTextFilePath(String textFilePath) {
        this.textFilePath = textFilePath;
    }

    public String getCustomTitle() {
        return customTitle;
    }

    public void setCustomTitle(String customTitle) {
        this.customTitle = customTitle;
    }
}
