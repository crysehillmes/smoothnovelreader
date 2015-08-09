package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.model.Chapter;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListDataset;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListItem;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToChapterModel implements Func1<ChapterListDataset, List<ChapterModel>> {
    private String mNovelId = null;
    public ToChapterModel(String novelId) {
        mNovelId = novelId;
    }

    @Override
    public List<ChapterModel> call(ChapterListDataset dataset) {
        ChapterListItem[] chapterArray = dataset.getItems();
        ArrayList<ChapterModel> result = new ArrayList<>(chapterArray.length);
        int index = 0;
        for (ChapterListItem item : chapterArray) {
            result.add(new Chapter(mNovelId,item.getCid(),item.getCtsrc(),item.getTitle(),index));
            index++;
        }
        return result;
    }
}
