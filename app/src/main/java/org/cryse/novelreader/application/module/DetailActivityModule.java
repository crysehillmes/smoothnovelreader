package org.cryse.novelreader.application.module;

import org.cryse.novelreader.application.qualifier.ActivityScope;
import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.presenter.impl.NovelDetailPresenterImpl;
import org.cryse.novelreader.ui.NovelDetailActivity;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import dagger.Module;
import dagger.Provides;

@Module
public class DetailActivityModule {
    private NovelDetailActivity detailActivity;

    public DetailActivityModule(NovelDetailActivity detailActivity) {
        this.detailActivity = detailActivity;
    }

    @Provides
    @ActivityScope
    NovelDetailActivity provideNovelDetailActivity() {
        return detailActivity;
    }

    @Provides
    NovelDetailPresenter provideNovelDetailPresenter(NovelBusinessLogicLayer dataService, AndroidNavigation display) {
        return new NovelDetailPresenterImpl(dataService, display);
    }
}
