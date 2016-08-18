package org.cryse.novelreader.ui.adapter.item;

public class NovelDetailItem {
    public static final int TYPE_ITEM = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_DIVIDER = 2;

    private CharSequence text;
    private int type = 0;
    private int textColor;

    public NovelDetailItem(CharSequence text) {
        this(text, TYPE_ITEM);
    }

    public NovelDetailItem(CharSequence text, int type) {
        this(text, type, 0);
    }

    public NovelDetailItem(CharSequence text, int type, int textColor) {
        this.text = text;
        this.type = type;
        this.textColor = textColor;
    }

    public CharSequence getText() {
        return text;
    }

    public void setText(CharSequence text) {
        this.text = text;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }
}
