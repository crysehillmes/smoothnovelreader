package org.cryse.novelreader.application.module;

import android.content.SharedPreferences;

import org.cryse.novelreader.application.qualifier.FragmentScope;
import org.cryse.novelreader.application.qualifier.PrefsGrayScaleInNight;
import org.cryse.novelreader.application.qualifier.PrefsShowCoverImage;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.presenter.impl.NovelListPresenterImpl;
import org.cryse.novelreader.ui.SearchFragment;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.prefs.BooleanPreference;

import dagger.Module;
import dagger.Provides;

@Module
public class SearchModule {
    private SearchFragment searchFragment;

    public SearchModule(SearchFragment searchFragment) {
        this.searchFragment = searchFragment;
    }

    @Provides
    @FragmentScope
    SearchFragment provideSearchFragment() {
        return searchFragment;
    }


    @Provides
    NovelListPresenter provideNovelListPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display) {
        return new NovelListPresenterImpl(dataService, display);
    }

    @Provides
    @PrefsShowCoverImage
    BooleanPreference provideShowCoverImage(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_SHOW_COVER_IMAGE, PreferenceConstant.SHARED_PREFERENCE_SHOW_COVER_IMAGE_VALUE);
    }

    @Provides
    @PrefsGrayScaleInNight
    BooleanPreference provideGrayScaleInNight(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT, PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT_VALUE);
    }
}
