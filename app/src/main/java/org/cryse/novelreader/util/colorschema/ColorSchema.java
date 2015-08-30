package org.cryse.novelreader.util.colorschema;

import android.graphics.drawable.Drawable;

public class ColorSchema {

    private int textColor;
    private Drawable backgroundDrawable;
    private Drawable displayDrawable;

    public ColorSchema(int textColor, Drawable backgroundDrawable, Drawable displayDrawable) {
        this.textColor = textColor;
        this.backgroundDrawable = backgroundDrawable;
        this.displayDrawable = displayDrawable;
    }

    public int getTextColor() {
        return textColor;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }

    public Drawable getDisplayDrawable() {
        return displayDrawable;
    }
}
