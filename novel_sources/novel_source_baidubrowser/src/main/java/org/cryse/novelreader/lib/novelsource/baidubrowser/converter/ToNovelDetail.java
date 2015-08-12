package org.cryse.novelreader.lib.novelsource.baidubrowser.converter;

import android.text.TextUtils;

import org.cryse.novelreader.lib.novelsource.baidubrowser.model.DetailItem;
import org.cryse.novelreader.model.NovelDetailModel;

import rx.functions.Func1;

public class ToNovelDetail implements Func1<DetailItem, NovelDetailModel> {
    @Override
    public NovelDetailModel call(DetailItem detailItem) {
        return new NovelDetailModel(
                detailItem.getNovelId(),
                detailItem.getSource(),
                TextUtils.isDigitsOnly(detailItem.getChapterNumber()) ? Integer.valueOf(detailItem.getChapterNumber()) : 0,
                detailItem.getLatestChapterTitle(),
                detailItem.getSummary(),
                null,
                null
        );
    }
}
