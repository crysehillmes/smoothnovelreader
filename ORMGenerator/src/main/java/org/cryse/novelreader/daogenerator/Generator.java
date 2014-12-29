package org.cryse.novelreader.daogenerator;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Schema;

public class Generator {
    public static void main(String[] args) {
        Schema schema = new Schema(1, "org.cryse.novelreader.data");

        Entity novelChapter= schema.addEntity("NovelChapterModel");
        novelChapter.addStringProperty("id").index();
        novelChapter.addStringProperty("secondId");
        novelChapter.addStringProperty("src");
        novelChapter.addStringProperty("title");
        novelChapter.addIntProperty("chapterIndex").index();

        Entity novelModel = schema.addEntity("NovelModel");
        novelModel.addStringProperty("id").primaryKey().getProperty();
        novelModel.addStringProperty("src");
        novelModel.addStringProperty("title");
        novelModel.addStringProperty("author");
        novelModel.addStringProperty("categoryName");
        novelModel.addLongProperty("follow");
        novelModel.addStringProperty("status");
        novelModel.addStringProperty("summary");
        novelModel.addStringProperty("imageUrl");
        novelModel.addIntProperty("chapterCount");
        novelModel.addStringProperty("lastReadChapterTitle");
        novelModel.addStringProperty("latestChapterTitle");
        novelModel.addIntProperty("latestUpdateCount");
        novelModel.addLongProperty("sortWeight");

        Entity bookMark = schema.addEntity("NovelBookMarkModel");
        bookMark.addStringProperty("id");
        bookMark.addStringProperty("chapterTitle");
        bookMark.addStringProperty("novelTitle");
        bookMark.addIntProperty("chapterIndex");
        bookMark.addIntProperty("chapterOffset");
        bookMark.addIntProperty("bookMarkType");
        bookMark.addDateProperty("createTime");

        Entity novelChapterContent = schema.addEntity("NovelChapterContentModel");
        novelChapterContent.addStringProperty("id").index();
        novelChapterContent.addStringProperty("secondId").primaryKey();
        novelChapterContent.addStringProperty("content");
        novelChapterContent.addStringProperty("src");

        try {
            new DaoGenerator().generateAll(schema, args[0]);
        } catch (Exception e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
