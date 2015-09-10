package org.cryse.novelreader.lib.novelsource.easou.converter;

import org.cryse.novelreader.lib.novelsource.easou.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.easou.utils.EasouNovelId;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;

import java.util.ArrayList;
import java.util.List;

import rx.functions.Func1;

public class ToNovelUpdate implements Func1<NovelUpdateItem[], List<NovelSyncBookShelfModel>> {
    @Override
    public List<NovelSyncBookShelfModel> call(NovelUpdateItem[] updateShelfItems) {
        List<NovelSyncBookShelfModel> result = new ArrayList<>(updateShelfItems.length);
        for (NovelUpdateItem item : updateShelfItems) {
            result.add(new NovelSyncBookShelfModel(
                    EasouNovelId.toNovelId(item.getGId(), item.getNId()),
                    Integer.toString(item.getChapterId()),
                    item.getLatestChapterTitle(),
                    item.getChapterNumber()
            ));
        }
        return result;
    }
}