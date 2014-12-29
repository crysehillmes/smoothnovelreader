package org.cryse.novelreader.source.baidu.converter;


import org.cryse.novelreader.model.NovelChapterContentModel;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterContentItem;

import rx.functions.Func1;

public class ToNovelChapterContent implements Func1<ChapterContentItem, NovelChapterContentModel> {
    private String mNovelId;
    private String mChapterId;
    public ToNovelChapterContent(String novelId, String chapterId) {
        mNovelId = novelId;
        mChapterId = chapterId;
    }
    @Override
    public NovelChapterContentModel call(ChapterContentItem content) {
        return new NovelChapterContentModel(mNovelId, mChapterId, content.getContent(), content.getCtsrc());
    }
}
