package org.cryse.novelreader.source.baidu.entity.categorylist;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 14-2-1.
 * Email: tyk5555@hotmail.com
 */
public class CategoryDataset {
    private CategoryNovelItem[] catelist = null;
    private CategoryNovelItem[] items = null;

    private <M> M chooseNotNull(M arg1, M arg2) {
        M ret = null;
        if(arg1 == null && arg2 != null)
            ret = arg2;
        else if(arg1 != null && arg2 == null)
            ret = arg1;
        else
            ret = arg1;
        return ret;
    }

    public CategoryNovelItem[] getCatelist() {
        return chooseNotNull(catelist, items);
    }

    public void setCatelist(CategoryNovelItem[] catelist) {
        this.catelist = catelist;
    }

    public CategoryNovelItem[] getItems() {
        return chooseNotNull(items, catelist);
    }

    public void setItems(CategoryNovelItem[] items) {
        this.items = items;
    }
}
