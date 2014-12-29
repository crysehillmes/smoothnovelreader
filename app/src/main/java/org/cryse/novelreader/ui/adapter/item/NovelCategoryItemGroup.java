package org.cryse.novelreader.ui.adapter.item;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

public class NovelCategoryItemGroup implements Parcelable {
    public static final int TYPE_TRADITIONAL_CATEGORY = 0;
    public static final int TYPE_HEADER = 1;
    public static final int TYPE_TAG_CATEGORY = 11;
    private int groupType;
    private String groupName;
    private ArrayList<NovelCategoryItem> items;

    public NovelCategoryItemGroup(int groupType, String groupName, ArrayList<NovelCategoryItem> items) {
        this.groupType = groupType;
        this.groupName = groupName;
        this.items = items;
    }

    public int getGroupType() {
        return groupType;
    }

    public void setGroupType(int groupType) {
        this.groupType = groupType;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public ArrayList<NovelCategoryItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<NovelCategoryItem> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.groupType);
        dest.writeString(this.groupName);
        NovelCategoryItem[] itemsArray = new NovelCategoryItem[this.items.size()];
        itemsArray = items.toArray(itemsArray);
        dest.writeParcelableArray(itemsArray, flags);
    }

    public NovelCategoryItemGroup() {
    }

    private NovelCategoryItemGroup(Parcel in) {
        this.groupType = in.readInt();
        this.groupName = in.readString();
        //this.items = (ArrayList<NovelCategoryItem>) in.readSerializable();
        Parcelable[] parcelableArray =
                in.readParcelableArray(NovelCategoryItem.class.getClassLoader());
        NovelCategoryItem[] resultArray = null;
        if (parcelableArray != null) {
            resultArray = Arrays.copyOf(parcelableArray, parcelableArray.length, NovelCategoryItem[].class);
        }
        this.items.addAll(Arrays.asList(resultArray));
    }

    public static final Parcelable.Creator<NovelCategoryItemGroup> CREATOR = new Parcelable.Creator<NovelCategoryItemGroup>() {
        public NovelCategoryItemGroup createFromParcel(Parcel source) {
            return new NovelCategoryItemGroup(source);
        }

        public NovelCategoryItemGroup[] newArray(int size) {
            return new NovelCategoryItemGroup[size];
        }
    };
}