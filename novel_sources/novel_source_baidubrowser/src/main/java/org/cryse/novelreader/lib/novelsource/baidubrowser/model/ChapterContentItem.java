package org.cryse.novelreader.lib.novelsource.baidubrowser.model;

import com.google.gson.annotations.SerializedName;

public class ChapterContentItem {
    @SerializedName("title")
    private int title;

    @SerializedName("text")
    private String content;

    @SerializedName("link")
    private String source;

    @SerializedName("cid")
    private String cid;

    public int getTitle() {
        return title;
    }

    public void setTitle(int title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }
}
