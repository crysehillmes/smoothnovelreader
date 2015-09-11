package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.ChapterModel;

import java.util.List;

public interface NovelChapterContentView extends ContentView {
    void showChapter(String content, List<CharSequence> splitedContent);
    void showNextChapter(String content, List<CharSequence> splitedContent);
    void showPrevChapter(String content, List<CharSequence> splitedContent, boolean jumpToLast);
    void onBookMarkSaved(int type, boolean isSuccess);
    void onGetOtherSrcFinished(List<NovelChangeSrcModel> otherSrc);
    void onChangeSrc(ChapterModel chapterModel);
}
