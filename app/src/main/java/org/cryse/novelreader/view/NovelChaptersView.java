package org.cryse.novelreader.view;

import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterModel;

import java.util.List;

public interface NovelChaptersView extends ContentView {
    void showChapterList(List<ChapterModel> chapterList, boolean scrollToLastRead);
    void canGoToLastRead(BookmarkModel bookMark);
    void checkFavoriteStatusComplete(Boolean isFavorite, Boolean isLocal);
}
