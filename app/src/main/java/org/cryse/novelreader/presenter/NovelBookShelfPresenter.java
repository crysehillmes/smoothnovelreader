package org.cryse.novelreader.presenter;

import android.support.v4.util.Pair;
import android.view.View;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelBookShelfView;

public interface NovelBookShelfPresenter extends BaseFragmentPresenter<NovelBookShelfView> {
    void loadFavoriteNovels();
    void getNovelUpdates();
    void removeFromFavorite(String... novelIds);
    void showNovelChapterList(NovelModel novelModel);

    void goSearch(String queryString, Pair<View, String>... transitionPairs);

    void showNovelDetail(NovelModel novelModel);
}
