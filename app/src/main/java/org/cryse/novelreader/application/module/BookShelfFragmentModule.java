package org.cryse.novelreader.application.module;

import org.cryse.novelreader.application.qualifier.FragmentScope;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.presenter.impl.NovelBookShelfPresenterImpl;
import org.cryse.novelreader.ui.NovelBookShelfFragment;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import dagger.Module;
import dagger.Provides;

@Module
public class BookShelfFragmentModule {
    private NovelBookShelfFragment bookShelfFragment;

    public BookShelfFragmentModule(NovelBookShelfFragment bookShelfFragment) {
        this.bookShelfFragment = bookShelfFragment;
    }

    @Provides
    @FragmentScope
    NovelBookShelfFragment provideNovelBookShelfFragment() {
        return bookShelfFragment;
    }

    @Provides
    NovelBookShelfPresenter provideNovelBookShelfPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display) {
        return new NovelBookShelfPresenterImpl(dataService, display);
    }
}
