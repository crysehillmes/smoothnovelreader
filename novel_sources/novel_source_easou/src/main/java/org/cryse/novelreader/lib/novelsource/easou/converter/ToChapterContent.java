package org.cryse.novelreader.lib.novelsource.easou.converter;

import org.cryse.novelreader.lib.novelsource.easou.model.ChapterContentItem;
import org.cryse.novelreader.model.ChapterContent;

import rx.functions.Func1;


public class ToChapterContent implements Func1<ChapterContentItem[], ChapterContent> {
    private String mNovelId;
    private String mChapterId;

    public ToChapterContent(String novelId, String chapterId) {
        mNovelId = novelId;
        mChapterId = chapterId;
    }

    @Override
    public ChapterContent call(ChapterContentItem[] chapterContentItems) {
        ChapterContentItem item = chapterContentItems[0];
        return new ChapterContent(mNovelId, mChapterId, item.getSource(), item.getContent());
    }
}
