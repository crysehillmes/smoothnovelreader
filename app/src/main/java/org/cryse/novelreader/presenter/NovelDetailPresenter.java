package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BasePresenter;
import org.cryse.novelreader.view.NovelDetailView;

public interface NovelDetailPresenter extends BasePresenter<NovelDetailView> {
    public void loadNovelDetail(NovelModel novelModel);
    public void checkNovelFavoriteStatus(NovelModel novelModel);
    public void addOrRemoveFavorite(NovelModel novelModel, boolean isAdd);
    public void startReading(NovelModel novelModel);
}
