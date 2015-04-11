package org.cryse.novelreader.modules.provider;

import android.content.Context;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.impl.NovelListPresenterImpl;
import org.cryse.novelreader.qualifier.ApplicationContext;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.presenter.impl.NovelBookShelfPresenterImpl;
import org.cryse.novelreader.presenter.impl.NovelChapterContentPresenterImpl;
import org.cryse.novelreader.presenter.impl.NovelChaptersPresenterImpl;
import org.cryse.novelreader.presenter.impl.NovelDetailPresenterImpl;
import org.cryse.novelreader.qualifier.PrefsNightMode;
import org.cryse.novelreader.qualifier.PrefsThemeColor;
import org.cryse.novelreader.util.ThemeEngine;
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.ToastTextGenerator;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.util.prefs.IntegerPreference;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
                NovelSourceProvider.class,
                ContextProvider.class,
                PreferenceProvider.class
        }
)
public class PresenterProvider {
    @Provides
    ToastTextGenerator provideToastTextGenerator(@ApplicationContext Context context) {
        return new ToastTextGenerator(context);
    }

    ToastUtil provideExceptionToastHelper(ToastTextGenerator generator) {
        return new ToastUtil(generator);
    }

    @Provides
    NovelChaptersPresenter provideNovelChaptersPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display, ToastUtil toastUtil) {
        return new NovelChaptersPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelListPresenter provideNovelListPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display, ToastUtil toastUtil) {
        return new NovelListPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelChapterContentPresenter provideNovelChapterContentPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display, ToastUtil toastUtil) {
        return new NovelChapterContentPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelBookShelfPresenter provideNovelBookShelfPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display, ToastUtil toastUtil) {
        return new NovelBookShelfPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelDetailPresenter provideNovelDetailPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display, ToastUtil toastUtil) {
        return new NovelDetailPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    public ThemeEngine provideThemeEngine(@PrefsNightMode BooleanPreference nightModePrefs, @PrefsThemeColor IntegerPreference themeColorPrefs) {
        return new ThemeEngine(nightModePrefs, themeColorPrefs);
    }
}
