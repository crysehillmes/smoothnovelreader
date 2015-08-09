package org.cryse.novelreader.source.baidu.entity.chapterlist;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 2014/3/22.
 * Email: tyk5555@hotmail.com
 */
public class ChapterListDataset {
    private ChapterListItem[] items;
    private int isOptimize;

    public ChapterListItem[] getItems() {
        return items;
    }

    public void setItems(ChapterListItem[] items) {
        this.items = items;
    }

    public int getIsOptimize() {
        return isOptimize;
    }

    public void setIsOptimize(int isOptimize) {
        this.isOptimize = isOptimize;
    }
}
