package org.cryse.novelreader.lib.novelsource.baidubrowser.converter;

import org.cryse.novelreader.lib.novelsource.baidubrowser.model.NovelUpdateItem;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelUpdate implements Func1<NovelUpdateItem[], List<NovelSyncBookShelfModel>> {
    @Override
    public List<NovelSyncBookShelfModel> call(NovelUpdateItem[] updateShelfItems) {
        List<NovelSyncBookShelfModel> result = new ArrayList<>(updateShelfItems.length);
        for(NovelUpdateItem item : updateShelfItems) {
            result.add(new NovelSyncBookShelfModel(
                    item.getNovelId(),
                    item.getChapterId(),
                    item.getLatestChapterTitle(),
                    item.getChapterNumber()
            ));
        }
        return result;
    }
}