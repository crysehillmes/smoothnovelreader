package org.cryse.novelreader.application.module;

import android.content.SharedPreferences;

import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.application.qualifier.PrefsHideRedundantChapterTitle;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.presenter.impl.NovelChaptersPresenterImpl;
import org.cryse.novelreader.ui.NovelChapterListActivity;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.prefs.BooleanPreference;

import dagger.Module;
import dagger.Provides;

@Module
public class ChaptersActivityModule {
    private NovelChapterListActivity chapterListActivity;

    public ChaptersActivityModule(NovelChapterListActivity chapterListActivity) {
        this.chapterListActivity = chapterListActivity;
    }

    @Provides
    @ActivityScope
    NovelChapterListActivity provideNovelChapterListActivity() {
        return chapterListActivity;
    }

    @Provides
    NovelChaptersPresenter provideNovelChaptersPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display) {
        return new NovelChaptersPresenterImpl(dataService, display);
    }


    @Provides
    @PrefsHideRedundantChapterTitle
    BooleanPreference provideHideRedundantChapterTitle(SharedPreferences preferences) {
        return new BooleanPreference(preferences, PreferenceConstant.SHARED_PREFERENCE_HIDE_REDUNDANT_CHAPTER_TITLE, PreferenceConstant.SHARED_PREFERENCE_HIDE_REDUNDANT_CHAPTER_TITLE_VALUE);
    }
}
