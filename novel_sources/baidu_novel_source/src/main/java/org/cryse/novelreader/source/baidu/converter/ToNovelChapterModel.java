package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListDataset;
import org.cryse.novelreader.source.baidu.entity.chapterlist.ChapterListItem;
import org.cryse.novelreader.model.NovelChapterModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelChapterModel implements Func1<ChapterListDataset, List<NovelChapterModel>> {
    private String mNovelId = null;
    public ToNovelChapterModel(String novelId) {
        mNovelId = novelId;
    }

    @Override
    public List<NovelChapterModel> call(ChapterListDataset dataset) {
        ChapterListItem[] chapterArray = dataset.getItems();
        ArrayList<NovelChapterModel> result = new ArrayList<>(chapterArray.length);
        int index = 0;
        for (ChapterListItem item : chapterArray) {
            result.add(new NovelChapterModel(mNovelId,item.getCid(),item.getCtsrc(),item.getTitle(),index));
            index++;
        }
        return result;
    }
}
