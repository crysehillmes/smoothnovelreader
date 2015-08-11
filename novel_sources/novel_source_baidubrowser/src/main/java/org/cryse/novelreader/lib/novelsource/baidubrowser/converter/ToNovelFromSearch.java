package org.cryse.novelreader.lib.novelsource.baidubrowser.converter;

import org.cryse.novelreader.lib.novelsource.baidubrowser.model.SearchNovelItem;
import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelFromSearch implements Func1<SearchNovelItem[], List<NovelModel>> {

    @Override
    public List<NovelModel> call(SearchNovelItem[] searchNovelItems) {
        List<NovelModel> result = new ArrayList<>(searchNovelItems.length);
        for(SearchNovelItem item : searchNovelItems) {
            result.add(new Novel(
                    item.getNovelId(),
                    item.getTitle(),
                    item.getAuthor(),
                    NovelModel.TYPE_BAIDU_SOURCE,
                    item.getSource(),
                    item.getCoverImage()
            ));
        }
        return result;
    }
}
