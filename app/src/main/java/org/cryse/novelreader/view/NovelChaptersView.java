package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChapterModel;

import java.util.List;

public interface NovelChaptersView extends ContentView {
    public void showChapterList(List<NovelChapterModel> chapterList);
    public void canGoToLastRead(NovelBookMarkModel bookMark);
    public void checkFavoriteStatusComplete(Boolean isFavorite);
}
