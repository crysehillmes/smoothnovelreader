package org.cryse.novelreader.util.drawable;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.annotation.DrawableRes;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;

public class CompatTintUtils {
    public static Drawable getTintedDrawable(Resources resources, @DrawableRes int drawableRes, int tintColor) {
        Drawable darkModeDrawable = ResourcesCompat.getDrawable(resources, drawableRes, null);
        Drawable wrappedDarkModeDrawable = DrawableCompat.wrap(darkModeDrawable);
        DrawableCompat.setTint(wrappedDarkModeDrawable, tintColor);
        return wrappedDarkModeDrawable;
    }

    public static Drawable getTintedDrawable(Drawable drawable, int tintColor) {
        Drawable wrappedDarkModeDrawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(wrappedDarkModeDrawable, tintColor);
        return wrappedDarkModeDrawable;
    }
}
