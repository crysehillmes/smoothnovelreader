package org.cryse.novelreader.presenter;

import android.text.TextPaint;

import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelChapterContentView;

import java.util.List;

public interface NovelChapterContentPresenter extends BaseFragmentPresenter<NovelChapterContentView> {
    void loadChapter(ChapterModel novelChapterModel, boolean forceUpdate);
    void loadNextChapter(ChapterModel novelChapterModel);
    void loadPrevChapter(ChapterModel novelChapterModel, boolean jumpToLast);
    void splitChapterAndDisplay(String title, String content);
    void addBookMark(BookmarkModel bookMarkModel);
    void saveLastReadBookMark(BookmarkModel bookMarkModel);
    List<ChapterModel> getChaptersState();
    void removeChaptersState();
    void saveChaptersState(List<ChapterModel> chapters);
    void setSplitParams(int width, int height, float lineSpacingMultiplier, float lineSpacingExtra, TextPaint textPaint);
    TextPaint getSplitTextPainter();
    void getOtherSrc(ChapterModel novelChapterModel);
    void changeSrc(ChapterModel novelChapterModel, NovelChangeSrcModel changeSrcModel);
}
