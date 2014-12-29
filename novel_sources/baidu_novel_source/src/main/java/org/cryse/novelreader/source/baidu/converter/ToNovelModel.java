package org.cryse.novelreader.source.baidu.converter;

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
            Long follower;
            try {
                follower = Long.parseLong(item.getFollow());
            } catch (NumberFormatException e) {
                follower = 0l;
            }
            result.add(new NovelModel(
                    item.getGid(),
                    item.getSrc(),
                    item.getTitle(),
                    item.getAuthor(),
                    item.getReason(),
                    follower,
                    item.getStatus(),
                    "",
                    item.getCoverImage(),
                    0,
                    "",
                    "",
                    0,
                    0l
            ));
        }
        return result;
    }
}
