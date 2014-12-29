package org.cryse.novelreader.source.baidu.entity.categorylist;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 14-2-1.
 * Email: tyk5555@hotmail.com
 */
public class CategoryNovelItem {
    private String gid = null;
    private String author = null;
    private String name = null;
    private String title = null;
    private String reason = null;
    private String follow = null;
    private String status = null;
    private String logo = null;
    private String coverImage = null;
    private String src = null;

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

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getName() {
        return chooseNotNull(name,title);
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return chooseNotNull(reason, follow);
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLogo() {
        return chooseNotNull(logo, coverImage);
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getSrc() {
        return src;
    }

    public void setSrc(String src) {
        this.src = src;
    }

    public String getTitle() {
        return chooseNotNull(title,name);
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFollow() {
        return chooseNotNull(follow, reason);
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public String getCoverImage() {
        return chooseNotNull(coverImage, logo);
    }

    public void setCoverImage(String coverImage) {
        this.coverImage = coverImage;
    }
}
