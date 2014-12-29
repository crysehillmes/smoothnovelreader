package org.cryse.novelreader.presenter;

import android.text.TextPaint;

import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.presenter.common.BaseFragmentPresenter;
import org.cryse.novelreader.view.NovelChapterContentView;

import java.util.List;

public interface NovelChapterContentPresenter extends BaseFragmentPresenter<NovelChapterContentView> {
    public void loadChapter(NovelChapterModel novelChapterModel, boolean forceUpdate);
    public void loadNextChapter(NovelChapterModel novelChapterModel);
    public void loadPrevChapter(NovelChapterModel novelChapterModel, boolean jumpToLast);
    public void splitChapterAndDisplay(String title, String content);
    public void addBookMark(NovelBookMarkModel bookMarkModel);
    public void saveLastReadBookMark(NovelBookMarkModel bookMarkModel);
    public List<NovelChapterModel> getChaptersState();
    public void removeChaptersState();
    public void saveChaptersState(List<NovelChapterModel> chapters);
    public void setSplitParams(int width, int height, float lineSpacingMultiplier, float lineSpacingExtra, TextPaint textPaint);
    public TextPaint getSplitTextPainter();
    public void getOtherSrc(NovelChapterModel novelChapterModel);
    public void changeSrc(NovelChapterModel novelChapterModel, NovelChangeSrcModel changeSrcModel);
}
