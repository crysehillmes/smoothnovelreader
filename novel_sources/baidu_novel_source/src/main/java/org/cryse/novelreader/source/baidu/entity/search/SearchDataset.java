package org.cryse.novelreader.source.baidu.entity.search;

public class SearchDataset {
    private SearchItem[] items;
    private String[] tagList;
    public SearchItem[] getItems() {
        return items;
    }

    public void setItems(SearchItem[] items) {
        this.items = items;
    }

    public String[] getTagList() {
        return tagList;
    }

    public void setTagList(String[] tagList) {
        this.tagList = tagList;
    }
}
