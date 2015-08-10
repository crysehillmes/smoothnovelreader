package org.cryse.novelreader.modules.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.qualifier.PrefsFontLineHeight;
import org.cryse.novelreader.qualifier.PrefsFontSize;
import org.cryse.novelreader.qualifier.PrefsGrayScaleInNight;
import org.cryse.novelreader.qualifier.PrefsHideRedundantChapterTitle;
import org.cryse.novelreader.qualifier.PrefsNightMode;
import org.cryse.novelreader.qualifier.PrefsReadBackground;
import org.cryse.novelreader.qualifier.PrefsScrollMode;
import org.cryse.novelreader.qualifier.PrefsShowCoverImage;
import org.cryse.novelreader.qualifier.PrefsThemeColor;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.util.prefs.IntegerPreference;
import org.cryse.novelreader.util.prefs.PreferenceConstant;
import org.cryse.novelreader.util.prefs.StringPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
            ContextProvider.class
        }
)
public class PreferenceProvider {
    @Provides @Singleton
    public SharedPreferences providePhilmPreferences(@ApplicationContext Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs;
    }

    @Provides
    @Singleton
    @PrefsFontSize
    StringPreference provideFontSizePreference(SharedPreferences preferences) {
        return new StringPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_FONT_SIZE, PreferenceConstant.SHARED_PREFERENCE_READ_FONT_SIZE_VALUE);
    }

    @Provides
    @Singleton
    @PrefsFontLineHeight
    IntegerPreference provideFontLineHeightPreference(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_LINE_HEIGHT, PreferenceConstant.SHARED_PREFERENCE_READ_LINE_HEIGHT_VALUE);
    }

    @Provides
    @Singleton
    @PrefsScrollMode
    StringPreference providePageFlipMode(SharedPreferences preferences) {
        return new StringPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_SCROLL_MODE, PreferenceConstant.SHARED_PREFERENCE_SCROLL_MODE_VALUE);
    }

    @Provides
    @Singleton
    @PrefsNightMode
    BooleanPreference provideIsNightMode(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE);
    }

    @Provides
    @Singleton
    @PrefsShowCoverImage
    BooleanPreference provideShowCoverImage(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_SHOW_COVER_IMAGE, PreferenceConstant.SHARED_PREFERENCE_SHOW_COVER_IMAGE_VALUE);
    }

    @Provides
    @Singleton
    @PrefsGrayScaleInNight
    BooleanPreference provideGrayScaleInNight(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT, PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT_VALUE);
    }

    @Provides
    @Singleton
    @PrefsReadBackground
    IntegerPreference provideReadBackground(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_BACKGROUND, PreferenceConstant.SHARED_PREFERENCE_READ_BACKGROUND_VALUE);
    }

    @Provides
    @PrefsThemeColor
    IntegerPreference provideThemeColorIndex(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);
    }

    @Provides
    @PrefsHideRedundantChapterTitle
    BooleanPreference provideHideRedundantChapterTitle(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_HIDE_REDUNDANT_CHAPTER_TITLE, PreferenceConstant.SHARED_PREFERENCE_HIDE_REDUNDANT_CHAPTER_TITLE_VALUE);
    }
}
