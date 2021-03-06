package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelOnlineListView;

public interface NovelListPresenter extends BaseFragmentPresenter<NovelOnlineListView> {
    void loadNovelCategoryList(String category, String subCategory, int page, int status, boolean isByTag, boolean append);

    void loadNovelRankList(String rank, int page, boolean append);

    void searchNovel(String query, int page, boolean append);

    void showNovelIntroduction(
            NovelModel novelModel);
}