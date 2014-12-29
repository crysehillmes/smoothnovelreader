package org.cryse.novelreader.ui.adapter.item;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.DrawableRes;

public class NovelCategoryItem implements Parcelable {
    private String title;
    private String value;
    private int icon;

    public NovelCategoryItem(String title, String value) {
        this(title, value, 0);
    }

    public NovelCategoryItem(String title, String value, @DrawableRes int icon) {
        this.title = title;
        this.value = value;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(@DrawableRes int icon) {
        this.icon = icon;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeString(this.value);
        dest.writeInt(this.icon);
    }

    private NovelCategoryItem(Parcel in) {
        this.title = in.readString();
        this.value = in.readString();
        this.icon = in.readInt();
    }

    public static final Parcelable.Creator<NovelCategoryItem> CREATOR = new Parcelable.Creator<NovelCategoryItem>() {
        public NovelCategoryItem createFromParcel(Parcel source) {
            return new NovelCategoryItem(source);
        }

        public NovelCategoryItem[] newArray(int size) {
            return new NovelCategoryItem[size];
        }
    };
}