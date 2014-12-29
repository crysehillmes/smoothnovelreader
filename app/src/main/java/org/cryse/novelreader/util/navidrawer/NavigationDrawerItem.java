package org.cryse.novelreader.util.navidrawer;

import android.support.annotation.DrawableRes;

public class NavigationDrawerItem {

    private String itemName;

    private int itemIcon;

    private boolean mainItem;

    private boolean selected;

    private NavigationType navigationType;

    private boolean showTitle;

    public NavigationDrawerItem(String itemName, NavigationType navigationType, @DrawableRes int itemIcon, boolean mainItem, boolean showTitle) {
        this.itemName = itemName;
        this.itemIcon = itemIcon;
        this.mainItem = mainItem;
        this.navigationType = navigationType;
        this.showTitle = showTitle;
    }

    public NavigationDrawerItem(String itemName, NavigationType navigationType, boolean mainItem, boolean showTitle) {
        this(itemName, navigationType, 0, mainItem, showTitle);
    }


    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public int getItemIcon() {
        return itemIcon;
    }

    public void setItemIcon(int itemIcon) {
        this.itemIcon = itemIcon;
    }

    public boolean isMainItem() {
        return mainItem;
    }

    public void setMainItem(boolean mainItem) {
        this.mainItem = mainItem;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public NavigationType getNavigationType() {
        return navigationType;
    }

    public void setNavigationType(NavigationType navigationType) {
        this.navigationType = navigationType;
    }

    public boolean isShowTitle() {
        return showTitle;
    }

    public void setShowTitle(boolean showTitle) {
        this.showTitle = showTitle;
    }
}
