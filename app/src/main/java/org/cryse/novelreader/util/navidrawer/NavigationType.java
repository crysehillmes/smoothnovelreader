package org.cryse.novelreader.util.navidrawer;

public enum NavigationType{
    NOVEL_CATEGORY_FRAGMENT(0),
    NOVEL_RANK_FRAGMENT(1),
    NOVEL_SETTINGS_ACTIVITY(2),
    NOVEL_BOOKSHELF_FRAGMENT(3);
    int navigationType;

    private NavigationType(int naviType) {
        navigationType = naviType;
    }

    public int getNavigationType() {
        return navigationType;
    }
}
