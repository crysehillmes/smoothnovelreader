package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelBookShelfView;

public interface NovelBookShelfPresenter extends BaseFragmentPresenter<NovelBookShelfView> {
    public void loadFavoriteNovels();
    public void getNovelUpdates();
    public void removeFromFavorite(String... novelIds);
    public void showNovelChapterList(NovelModel novelModel);
    public void goSearch();
}
