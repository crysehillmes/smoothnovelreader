package org.cryse.novelreader.source.baidu.converter;


import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.source.baidu.entity.search.SearchDataset;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.baidu.entity.search.SearchItem;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelModelFromSearch implements Func1<SearchDataset, List<NovelModel>> {
    @Override
    public List<NovelModel> call(SearchDataset dataset) {
        SearchItem[] novelArray = dataset.getItems();
        ArrayList<NovelModel> result = new ArrayList<>(novelArray.length);
        for(SearchItem item : novelArray) {
            result.add(new Novel(
                    item.getGid(),
                    item.getTitle(),
                    item.getAuthor(),
                    NovelModel.TYPE_BAIDU_SOURCE,
                    item.getListurl(),
                    item.getCoverImage()
            ));
        }
        return result;
    }
}
