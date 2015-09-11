package org.cryse.novelreader.lib.novelsource.easou.converter;

import org.cryse.novelreader.lib.novelsource.easou.Consts;
import org.cryse.novelreader.lib.novelsource.easou.model.SearchNovelItem;
import org.cryse.novelreader.lib.novelsource.easou.utils.EasouNovelId;
import org.cryse.novelreader.model.Novel;
import org.cryse.novelreader.model.NovelModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelFromSearch implements Func1<SearchNovelItem[], List<NovelModel>> {

    @Override
    public List<NovelModel> call(SearchNovelItem[] searchNovelItems) {
        List<NovelModel> result = new ArrayList<>(searchNovelItems.length);
        for (SearchNovelItem item : searchNovelItems) {
            Novel novel = new Novel(
                    EasouNovelId.toNovelId(item.getGId(), item.getNId()),
                    item.getTitle(),
                    item.getAuthor(),
                    Consts.TYPE_EASOU_SOURCE,
                    item.getSource(),
                    item.getCoverImage()
            );
            novel.setStatus(item.getStatus());
            novel.setSummary(item.getSummary());
            novel.setCategory(item.getCategory());
            result.add(novel);
        }
        return result;
    }
}
