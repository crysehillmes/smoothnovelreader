package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfDataset;
import org.cryse.novelreader.source.baidu.entity.syncshelf.SyncShelfItem;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import rx.functions.Func1;

public class ToNovelSyncBookShelfModel implements Func1<SyncShelfDataset, List<NovelSyncBookShelfModel>> {
    @Override
    public List<NovelSyncBookShelfModel> call(SyncShelfDataset dataset) {
        List<SyncShelfItem> items = Arrays.asList(dataset.getDataset());
        List<NovelSyncBookShelfModel> result = new ArrayList<NovelSyncBookShelfModel>(items.size());
        for(SyncShelfItem item : items) {
            result.add(new NovelSyncBookShelfModel(
                    item.getGid(),
                    item.getLastchaptitle(),
                    item.getLastupdate()
            ));
        }
        return result;
    }
}
