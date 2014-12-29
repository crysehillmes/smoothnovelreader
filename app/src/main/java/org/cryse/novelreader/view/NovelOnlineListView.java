package org.cryse.novelreader.view;

import org.cryse.novelreader.model.NovelModel;

import java.util.List;

public interface NovelOnlineListView extends ContentViewEx {
    public void getNovelListSuccess(List<NovelModel> novels, boolean append);
    public void getNovelListFailure(Throwable e, Object... extras);
}
