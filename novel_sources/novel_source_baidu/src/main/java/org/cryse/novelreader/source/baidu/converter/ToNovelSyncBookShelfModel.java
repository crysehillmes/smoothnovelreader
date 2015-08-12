package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.model.NovelSyncBookShelfModel;
import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfItem;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelSyncBookShelfModel implements Func1<SyncShelfItem[], List<NovelSyncBookShelfModel>> {
    @Override
    public List<NovelSyncBookShelfModel> call(SyncShelfItem[] dataset) {
        List<NovelSyncBookShelfModel> result = new ArrayList<NovelSyncBookShelfModel>(dataset.length);
        for(SyncShelfItem item : dataset) {
            result.add(new NovelSyncBookShelfModel(
                    item.getGid(),
                    "",
                    item.getLastchaptitle(),
                    item.getLastupdate()
            ));
        }
        return result;
    }
}
