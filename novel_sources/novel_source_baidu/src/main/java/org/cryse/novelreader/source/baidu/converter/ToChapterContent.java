package org.cryse.novelreader.source.baidu.converter;


import org.cryse.novelreader.model.ChapterContent;
import org.cryse.novelreader.model.ChapterContentModel;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterContentItem;

import rx.functions.Func1;

public class ToChapterContent implements Func1<ChapterContentItem, ChapterContentModel> {
    private String mNovelId;
    private String mChapterId;
    public ToChapterContent(String novelId, String chapterId) {
        mNovelId = novelId;
        mChapterId = chapterId;
    }
    @Override
    public ChapterContentModel call(ChapterContentItem content) {
        return new ChapterContent(mNovelId, mChapterId, content.getCtsrc(), content.getContent());
    }
}
