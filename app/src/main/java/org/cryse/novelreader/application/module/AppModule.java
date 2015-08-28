package org.cryse.novelreader.application.module;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.cryse.novelreader.application.qualifier.ApplicationContext;
import org.cryse.novelreader.application.qualifier.PrefsNightMode;
import org.cryse.novelreader.application.qualifier.PrefsThemeColor;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.util.prefs.IntegerPreference;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {
    private Application mApplication;
    private AndroidNavigation mNavigation;

    public AppModule(Application application, AndroidNavigation navigation) {
        this.mApplication = application;
        this.mNavigation = navigation;
    }

    @Provides
    @Singleton
    public Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    @Singleton
    public Context provideApplicationContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    public SharedPreferences provideDefaultSharedreferences(@ApplicationContext Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }

    @Provides
    @Singleton
    AndroidNavigation provideAndroidNavigation() {
        return mNavigation;
    }

    @Provides
    @Singleton
    @PrefsNightMode
    BooleanPreference provideIsNightMode(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE);
    }

    @Provides
    @Singleton
    @PrefsThemeColor
    IntegerPreference provideThemeColorIndex(SharedPreferences preferences) {
        return new IntegerPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);
    }
}