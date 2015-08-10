package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BasePresenter;
import org.cryse.novelreader.view.NovelChaptersView;

import java.util.List;

import rx.Observable;

public interface NovelChaptersPresenter extends BasePresenter<NovelChaptersView> {
    void loadChapters(NovelModel novelModel, boolean hideRedundantTitle);
    void loadChapters(NovelModel novelModel, boolean forceUpdate, boolean hideRedundantTitle);
    void checkNovelFavoriteStatus(NovelModel novelModel);
    void checkLastReadState(NovelModel novelModel);
    void readChapter(NovelModel novelModel, String chapterId, List<ChapterModel> chapterList);
    void readLastPosition(NovelModel novelModel, List<ChapterModel> chapterList);
    void showNovelIntroduction(NovelModel novelModel);
    Observable<Boolean> preloadChapterContents(NovelModel novel, List<ChapterModel> chapterModels);
}
