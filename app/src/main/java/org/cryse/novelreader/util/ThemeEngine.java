package org.cryse.novelreader.util;

import android.content.Context;

import org.cryse.novelreader.R;
import org.cryse.novelreader.qualifier.PrefsNightMode;
import org.cryse.novelreader.qualifier.PrefsThemeColor;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.util.prefs.IntegerPreference;

import javax.inject.Inject;

public class ThemeEngine {
    IntegerPreference mPrefColorIndex;
    BooleanPreference mPrefNightMode;

    int[] mPrimaryColors = new int[]{
            R.color.md_red_500,
            R.color.md_pink_500,
            R.color.md_purple_500,
            R.color.md_deep_purple_500,
            R.color.md_indigo_500,
            R.color.md_blue_500,
            R.color.md_light_blue_500,
            R.color.md_cyan_500,
            R.color.md_teal_500,
            R.color.md_green_500,
            R.color.md_light_green_500,
            R.color.md_lime_500,
            R.color.md_yellow_500,
            R.color.md_amber_500,
            R.color.md_orange_500,
            R.color.md_deep_orange_500,
            R.color.md_brown_500,
            R.color.md_grey_500,
            R.color.md_blue_grey_500
    };

    int[] mPrimaryDarkColors = new int[]{
            R.color.md_red_700,
            R.color.md_pink_700,
            R.color.md_purple_700,
            R.color.md_deep_purple_700,
            R.color.md_indigo_700,
            R.color.md_blue_700,
            R.color.md_light_blue_700,
            R.color.md_cyan_700,
            R.color.md_teal_700,
            R.color.md_green_700,
            R.color.md_light_green_700,
            R.color.md_lime_700,
            R.color.md_yellow_700,
            R.color.md_amber_700,
            R.color.md_orange_700,
            R.color.md_deep_orange_700,
            R.color.md_brown_700,
            R.color.md_grey_700,
            R.color.md_blue_grey_700
    };

    @Inject
    public ThemeEngine(@PrefsNightMode BooleanPreference nightModePrefs, @PrefsThemeColor IntegerPreference themeColorPrefs) {
        mPrefNightMode = nightModePrefs;
        mPrefColorIndex = themeColorPrefs;
    }

    public int getPrimaryColor(Context context) {
        return context.getResources().getColor(mPrimaryColors[mPrefColorIndex.get()]);
    }

    public int getPrimaryDarkColor(Context context) {
        return context.getResources().getColor(mPrimaryDarkColors[mPrefColorIndex.get()]);
    }

    public boolean isNightMode() {
        return mPrefNightMode.get();
    }

    public void setNightMode(boolean isNightMode) {
        mPrefNightMode.set(isNightMode);
    }

    public int getPrimaryColorResId() {
        return mPrimaryColors[mPrefColorIndex.get()];
    }

    public int getPrimaryDarkColorResId() {
        return mPrimaryDarkColors[mPrefColorIndex.get()];
    }
}
