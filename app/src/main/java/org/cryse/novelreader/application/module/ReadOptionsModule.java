package org.cryse.novelreader.application.module;

import android.content.SharedPreferences;

import org.cryse.novelreader.application.qualifier.FragmentScope;
import org.cryse.novelreader.application.qualifier.PrefsFontSize;
import org.cryse.novelreader.application.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.ui.ReadOptionsFragment;
import org.cryse.novelreader.util.prefs.StringPreference;

import dagger.Module;
import dagger.Provides;

@Module
public class ReadOptionsModule {
    private ReadOptionsFragment readOptionsFragment;

    public ReadOptionsModule(ReadOptionsFragment readOptionsFragment) {
        this.readOptionsFragment = readOptionsFragment;
    }

    @Provides
    @FragmentScope
    ReadOptionsFragment provideReadOptionsFragment() {
        return readOptionsFragment;
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
}