package org.cryse.novelreader.application.module;

import android.content.SharedPreferences;

import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.application.qualifier.PrefsFontSize;
import org.cryse.novelreader.application.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.application.qualifier.PrefsReadBackground;
import org.cryse.novelreader.application.qualifier.PrefsReadColorSchema;
import org.cryse.novelreader.application.qualifier.PrefsScrollMode;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.presenter.impl.NovelChapterContentPresenterImpl;
import org.cryse.novelreader.ui.NovelReadViewActivity;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.prefs.IntegerPreference;
import org.cryse.novelreader.util.prefs.StringPreference;

import dagger.Module;
import dagger.Provides;

@Module
public class ReadActivityModule {
    private NovelReadViewActivity readViewActivity;

    public ReadActivityModule(NovelReadViewActivity readViewActivity) {
        this.readViewActivity = readViewActivity;
    }

    @Provides
    @ActivityScope
    NovelReadViewActivity provideNovelReadViewActivity() {
        return readViewActivity;
    }

    @Provides
    NovelChapterContentPresenter provideNovelChapterContentPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display) {
        return new NovelChapterContentPresenterImpl(dataService, display);
    }

    @Provides
    @PrefsFontSize
    StringPreference provideFontSizePreference(SharedPreferences preferences) {
        return new StringPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_FONT_SIZE, PreferenceConstant.SHARED_PREFERENCE_READ_FONT_SIZE_VALUE);
    }

    @Provides
    @PrefsLineSpacing
    StringPreference provideFontLineSpacingPreference(SharedPreferences preferences) {
        return new StringPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_LINE_SPACING, PreferenceConstant.SHARED_PREFERENCE_READ_LINE_SPACING_VALUE);
    }

    @Provides
    @PrefsScrollMode
    StringPreference providePageFlipMode(SharedPreferences preferences) {
        return new StringPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_SCROLL_MODE, PreferenceConstant.SHARED_PREFERENCE_SCROLL_MODE_VALUE);
    }

    @Provides
    @PrefsReadBackground
    IntegerPreference provideReadBackground(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_BACKGROUND, PreferenceConstant.SHARED_PREFERENCE_READ_BACKGROUND_VALUE);
    }

    @Provides
    @PrefsReadColorSchema
    IntegerPreference provideReadColorSchemaPreference(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_READ_COLOR_SCHEMA, PreferenceConstant.SHARED_PREFERENCE_READ_COLOR_SCHEMA_VALUE);
    }
}
