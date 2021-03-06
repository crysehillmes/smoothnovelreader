package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelBookShelfView;

public interface NovelBookShelfPresenter extends BaseFragmentPresenter<NovelBookShelfView> {
    void loadFavoriteNovels();
    void getNovelUpdates();
    void removeFromFavorite(String... novelIds);
    void showNovelChapterList(NovelModel novelModel);
    void showNovelDetail(NovelModel novelModel);
}
