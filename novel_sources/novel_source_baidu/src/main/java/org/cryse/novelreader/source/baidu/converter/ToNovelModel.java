package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.source.baidu.entity.categorylist.CategoryDataset;
import org.cryse.novelreader.source.baidu.entity.categorylist.CategoryNovelItem;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;


public final class ToNovelModel implements Func1<CategoryDataset, List<NovelModel>> {
    @Override
    public List<NovelModel> call(CategoryDataset dataset) {
        CategoryNovelItem[] novelArray = dataset.getCatelist();
        ArrayList<NovelModel> result = new ArrayList<>(novelArray.length);
        for(CategoryNovelItem item : novelArray) {
            result.add(new Novel(
                    item.getGid(),
                    item.getTitle(),
                    item.getAuthor(),
                    NovelModel.TYPE_BAIDU_SOURCE,
                    item.getSrc(),
                    item.getCoverImage()
            ));
        }
        return result;
    }
}
