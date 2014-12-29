package org.cryse.novelreader.presenter;

import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.common.BasePresenter;
import org.cryse.novelreader.view.NovelChaptersView;

import java.util.List;

public interface NovelChaptersPresenter extends BasePresenter<NovelChaptersView> {
    public void loadChapters(NovelModel novelModel);
    public void loadChapters(NovelModel novelModel, boolean forceUpdate);
    public void checkLastReadState(final String novelId);
    public void readChapter(NovelModel novelModel, int chapterIndex, List<NovelChapterModel> chapterList);
    public void readLastPosition(NovelModel novelModel, List<NovelChapterModel> chapterList);
    public void showNovelIntroduction(NovelModel novelModel);
}
