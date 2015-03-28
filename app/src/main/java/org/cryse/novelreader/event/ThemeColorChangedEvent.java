package org.cryse.novelreader.event;

public class ThemeColorChangedEvent extends AbstractEvent {
    private int newPrimaryColor;
    private int newPrimaryDarkColor;
    private int newPrimaryColorResId;
    private int newPrimaryDarkColorResId;

    public ThemeColorChangedEvent(int newPrimaryColor, int newPrimaryDarkColor, int newPrimaryColorResId, int newPrimaryDarkColorResId) {
        this.newPrimaryColor = newPrimaryColor;
        this.newPrimaryDarkColor = newPrimaryDarkColor;
        this.newPrimaryColorResId = newPrimaryColorResId;
        this.newPrimaryDarkColorResId = newPrimaryDarkColorResId;
    }
    public int getNewPrimaryColor() {
        return newPrimaryColor;
    }

    public void setNewPrimaryColor(int newPrimaryColor) {
        this.newPrimaryColor = newPrimaryColor;
    }

    public int getNewPrimaryDarkColor() {
        return newPrimaryDarkColor;
    }

    public void setNewPrimaryDarkColor(int newPrimaryDarkColor) {
        this.newPrimaryDarkColor = newPrimaryDarkColor;
    }

    public int getNewPrimaryColorResId() {
        return newPrimaryColorResId;
    }

    public int getNewPrimaryDarkColorResId() {
        return newPrimaryDarkColorResId;
    }
}
