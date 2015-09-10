package org.cryse.novelreader.lib.novelsource.easou.converter;

import android.text.TextUtils;

import org.cryse.novelreader.lib.novelsource.easou.model.DetailItem;
import org.cryse.novelreader.lib.novelsource.easou.utils.EasouNovelId;
import org.cryse.novelreader.model.NovelDetailModel;

import rx.functions.Func1;

public class ToNovelDetail implements Func1<DetailItem, NovelDetailModel> {
    @Override
    public NovelDetailModel call(DetailItem detailItem) {
        return new NovelDetailModel(
                EasouNovelId.toNovelId(detailItem.getGId(), detailItem.getNId()),
                detailItem.getSource(),
                TextUtils.isDigitsOnly(detailItem.getChapterNumber()) ? Integer.valueOf(detailItem.getChapterNumber()) : 0,
                detailItem.getLastChapterTitle(),
                detailItem.getSummary(),
                null,
                null
        );
    }
}
