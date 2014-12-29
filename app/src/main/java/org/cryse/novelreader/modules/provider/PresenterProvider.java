package org.cryse.novelreader.modules.provider;

import android.content.Context;

import org.cryse.novelreader.logic.NovelDataService;
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
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.ToastTextGenerator;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

import dagger.Module;
import dagger.Provides;

@Module(
        library = true,
        includes = {
                NovelSourceProvider.class,
                ContextProvider.class
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
    NovelChaptersPresenter provideNovelChaptersPresenter(NovelDataService dataService, AndroidDisplay display, ToastUtil toastUtil) {
        return new NovelChaptersPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelListPresenter provideNovelListPresenter(NovelDataService dataService, AndroidDisplay display, ToastUtil toastUtil) {
        return new NovelListPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelChapterContentPresenter provideNovelChapterContentPresenter(NovelDataService dataService, AndroidDisplay display, ToastUtil toastUtil) {
        return new NovelChapterContentPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelBookShelfPresenter provideNovelBookShelfPresenter(NovelDataService dataService, AndroidDisplay display, ToastUtil toastUtil) {
        return new NovelBookShelfPresenterImpl(dataService, display, toastUtil);
    }

    @Provides
    NovelDetailPresenter provideNovelDetailPresenter(NovelDataService dataService, AndroidDisplay display, ToastUtil toastUtil) {
        return new NovelDetailPresenterImpl(dataService, display, toastUtil);
    }
}
