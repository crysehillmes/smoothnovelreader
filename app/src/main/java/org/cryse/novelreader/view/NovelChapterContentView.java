package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterModel;

import java.util.List;

public interface NovelChapterContentView extends ContentView {
    public void showChapter(String content, List<CharSequence> splitedContent);
    public void showNextChapter(String content, List<CharSequence> splitedContent);
    public void showPrevChapter(String content, List<CharSequence> splitedContent, boolean jumpToLast);
    public void onBookMarkSaved(int type, boolean isSuccess);
    public void onGetOtherSrcFinished(List<NovelChangeSrcModel> otherSrc);
    public void onChangeSrc(NovelChapterModel chapterModel);
}
