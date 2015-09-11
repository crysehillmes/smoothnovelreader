package org.cryse.novelreader.lib.novelsource.easou.converter;

import org.cryse.novelreader.lib.novelsource.easou.model.NovelUpdateItem;
import org.cryse.novelreader.lib.novelsource.easou.utils.EasouNovelId;
import org.cryse.novelreader.model.NovelSyncBookShelfModel;

import rx.functions.Func1;

public class ToNovelUpdate implements Func1<NovelUpdateItem, NovelSyncBookShelfModel> {
    @Override
    public NovelSyncBookShelfModel call(NovelUpdateItem item) {
        return new NovelSyncBookShelfModel(
                EasouNovelId.toNovelId(item.getGId(), item.getNId()),
                Integer.toString(item.getChapterId()),
                item.getLatestChapterTitle(),
                Integer.toString(item.getChapterNumber())
        );
    }
}