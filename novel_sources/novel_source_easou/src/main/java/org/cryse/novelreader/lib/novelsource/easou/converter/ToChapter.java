package org.cryse.novelreader.lib.novelsource.easou.converter;

import org.cryse.novelreader.lib.novelsource.easou.model.ChapterItem;
import org.cryse.novelreader.model.Chapter;
import org.cryse.novelreader.model.ChapterModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;


public class ToChapter implements Func1<ChapterItem[], List<ChapterModel>> {
    private String mNovelId = null;

    public ToChapter(String novelId) {
        mNovelId = novelId;
    }

    @Override
    public List<ChapterModel> call(ChapterItem[] chapterItems) {
        ArrayList<ChapterModel> result = new ArrayList<>(chapterItems.length);
        int index = 0;
        for (ChapterItem item : chapterItems) {
            String chapterId = Integer.toString(item.getChapterId());
            result.add(new Chapter(mNovelId, chapterId, item.getSource(), item.getTitle(), index));
            index++;
        }
        return result;
    }
}
