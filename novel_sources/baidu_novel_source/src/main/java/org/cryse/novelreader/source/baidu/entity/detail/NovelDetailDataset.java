package org.cryse.novelreader.source.baidu.entity.detail;

public class NovelDetailDataset {
    private String gid;
    private int hasCache;
    private int download_size;
    private int download_chapter_num;
    private int chapter_num;
    private String cpsrc;
    private String title;
    private String author;
    private String status;
    private DetailLastChapter lastChapter;
    private String coverImage;
    private String summary;
    private String summaryShort;
    private String summaryRest;
    private String cardData;
    private String commentNum;
    private DetailCommentData[] commentData;
    private String downloadurl;
    private String[][] tags;
    private DetailAchieve[] achieve;
    private DetailSubpage subpage;
    private String version;

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public int getHasCache() {
        return hasCache;
    }

    public void setHasCache(int hasCache) {
        this.hasCache = hasCache;
    }

    public int getDownload_size() {
        return download_size;
    }

    public void setDownload_size(int download_size) {
        this.download_size = download_size;
    }

    public int getDownload_chapter_num() {
        return download_chapter_num;
    }

    public void setDownload_chapter_num(int download_chapter_num) {
        this.download_chapter_num = download_chapter_num;
    }

    public int getChapter_num() {
        return chapter_num;
    }

    public void setChapter_num(int chapter_num) {
        this.chapter_num = chapter_num;
    }

    public String getCpsrc() {
        return cpsrc;
    }

    public void setCpsrc(String cpsrc) {
        this.cpsrc = cpsrc;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public DetailLastChapter getLastChapter() {
        return lastChapter;
    }

    public void setLastChapter(DetailLastChapter lastChapter) {
        this.lastChapter = lastChapter;
    }

    public String getCoverImage() {
        return coverImage;
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getSummaryShort() {
        return summaryShort;
    }

    public void setSummaryShort(String summaryShort) {
        this.summaryShort = summaryShort;
    }

    public String getSummaryRest() {
        return summaryRest;
    }

    public void setSummaryRest(String summaryRest) {
        this.summaryRest = summaryRest;
    }

    public String getCardData() {
        return cardData;
    }

    public void setCardData(String cardData) {
        this.cardData = cardData;
    }

    public String getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(String commentNum) {
        this.commentNum = commentNum;
    }

    public DetailCommentData[] getCommentData() {
        return commentData;
    }

    public void setCommentData(DetailCommentData[] commentData) {
        this.commentData = commentData;
    }

    public String getDownloadurl() {
        return downloadurl;
    }

    public void setDownloadurl(String downloadurl) {
        this.downloadurl = downloadurl;
    }

    public String[][] getTags() {
        return tags;
    }

    public void setTags(String[][] tags) {
        this.tags = tags;
    }

    public DetailAchieve[] getAchieve() {
        return achieve;
    }

    public void setAchieve(DetailAchieve[] achieve) {
        this.achieve = achieve;
    }

    public DetailSubpage getSubpage() {
        return subpage;
    }

    public void setSubpage(DetailSubpage subpage) {
        this.subpage = subpage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
