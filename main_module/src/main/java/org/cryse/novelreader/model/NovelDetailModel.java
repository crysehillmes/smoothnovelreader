package org.cryse.novelreader.model;

import android.os.Parcel;
import android.os.Parcelable;

public class NovelDetailModel implements Parcelable {
    private String id;
    private String src;
    private int chapterNumber;
    private String latestChapter;
    private String summary;
    private String[] tags;
    private NovelAchieveModel[] achieves;

    public NovelDetailModel(String id, String src, int chapterNumber, String latestChapter, String summary, String[] tags, NovelAchieveModel[] achieves) {
        this.id = id;
        this.src = src;
        this.chapterNumber = chapterNumber;
        this.latestChapter = latestChapter;
        this.summary = summary;
        this.tags = tags;
        this.achieves = achieves;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public int getChapterNumber() {
        return chapterNumber;
    }

    public void setChapterNumber(int chapterNumber) {
        this.chapterNumber = chapterNumber;
    }

    public String getLatestChapter() {
        return latestChapter;
    }

    public void setLatestChapter(String latestChapter) {
        this.latestChapter = latestChapter;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String[] getTags() {
        return tags;
    }

    public void setTags(String[] tags) {
        this.tags = tags;
    }

    public NovelAchieveModel[] getAchieves() {
        return achieves;
    }

    public void setAchieves(NovelAchieveModel[] achieves) {
        this.achieves = achieves;
    }

    public static class NovelAchieveModel implements Parcelable {
        private String name;
        private String rank;
        private String year;
        private String month;

        public NovelAchieveModel(String name, String rank, String year, String month) {
            this.name = name;
            this.rank = rank;
            this.year = year;
            this.month = month;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getRank() {
            return rank;
        }

        public void setRank(String rank) {
            this.rank = rank;
        }

        public String getYear() {
            return year;
        }

        public void setYear(String year) {
            this.year = year;
        }

        public String getMonth() {
            return month;
        }

        public void setMonth(String month) {
            this.month = month;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.name);
            dest.writeString(this.rank);
            dest.writeString(this.year);
            dest.writeString(this.month);
        }

        public NovelAchieveModel() {
        }

        private NovelAchieveModel(Parcel in) {
            this.name = in.readString();
            this.rank = in.readString();
            this.year = in.readString();
            this.month = in.readString();
        }

        public static final Creator<NovelAchieveModel> CREATOR = new Creator<NovelAchieveModel>() {
            public NovelAchieveModel createFromParcel(Parcel source) {
                return new NovelAchieveModel(source);
            }

            public NovelAchieveModel[] newArray(int size) {
                return new NovelAchieveModel[size];
            }
        };
    }

    public NovelDetailModel() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.src);
        dest.writeInt(this.chapterNumber);
        dest.writeString(this.latestChapter);
        dest.writeString(this.summary);
        dest.writeStringArray(this.tags);
        dest.writeParcelableArray(this.achieves, flags);
    }

    private NovelDetailModel(Parcel in) {
        this.id = in.readString();
        this.src = in.readString();
        this.chapterNumber = in.readInt();
        this.latestChapter = in.readString();
        this.summary = in.readString();
        this.tags = in.createStringArray();
        this.achieves = (NovelAchieveModel[]) in.readParcelableArray(NovelAchieveModel.class.getClassLoader());
    }

    public static final Creator<NovelDetailModel> CREATOR = new Creator<NovelDetailModel>() {
        public NovelDetailModel createFromParcel(Parcel source) {
            return new NovelDetailModel(source);
        }

        public NovelDetailModel[] newArray(int size) {
            return new NovelDetailModel[size];
        }
    };
}
