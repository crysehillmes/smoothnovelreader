package org.cryse.widget.spinner;

public class ActionBarSpinnerItem {
    private String title;
    private String value;
    private boolean isHeader;
    private int color;

    public ActionBarSpinnerItem(String title, String value, boolean isHeader, int color) {
        this.title = title;
        this.value = value;
        this.isHeader = isHeader;
        this.color = color;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isHeader() {
        return isHeader;
    }

    public void setHeader(boolean isHeader) {
        this.isHeader = isHeader;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }
}
