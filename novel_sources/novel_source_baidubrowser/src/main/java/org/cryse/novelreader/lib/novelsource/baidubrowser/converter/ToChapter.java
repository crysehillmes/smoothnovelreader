package org.cryse.novelreader.lib.novelsource.baidubrowser.converter;

import org.cryse.novelreader.lib.novelsource.baidubrowser.model.ChapterItem;
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
            if(index == 0) {
                index++;
                continue;
            }
            result.add(new Chapter(mNovelId, item.getChapterId(),item.getSource(),item.getTitle(), index - 1));
            index++;
        }
        return result;
    }
}
