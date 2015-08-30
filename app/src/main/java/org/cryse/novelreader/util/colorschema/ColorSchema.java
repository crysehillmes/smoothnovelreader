package org.cryse.novelreader.util.colorschema;

import android.graphics.drawable.Drawable;

public class ColorSchema {

    private int textColor;
    private Drawable backgroundDrawable;

    public ColorSchema(int textColor, Drawable backgroundDrawable) {
        this.textColor = textColor;
        this.backgroundDrawable = backgroundDrawable;
    }

    public int getTextColor() {
        return textColor;
    }

    public Drawable getBackgroundDrawable() {
        return backgroundDrawable;
    }
}
