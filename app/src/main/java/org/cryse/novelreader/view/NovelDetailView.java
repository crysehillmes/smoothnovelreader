package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelDetailModel;

public interface NovelDetailView extends ContentView {
    public void showNovelDetail(NovelDetailModel novelDetail);
    public void setFavoriteButtonStatus(boolean isFavorited);
}
