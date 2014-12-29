package org.cryse.novelreader.source.baidu.converter;

import org.cryse.novelreader.model.NovelDetailModel;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import rx.functions.Func1;

/**
 * Created by cryse on 11/14/14.
 */
public class ToNovelDetailModel implements Func1<String, NovelDetailModel> {
    @Override
    public NovelDetailModel call(String htmlString) {
        Document doc = Jsoup.parse(htmlString);
        String title = selectFirstElement(doc, "input#title");
        String author = selectFirstElement(doc, "input#author");
        String coverImage = selectFirstElement(doc, "input#coverImage");
        int chapterNum;
        try{
            chapterNum = Integer.parseInt(selectFirstElement(doc, "input#chapterNum"));
        } catch (NumberFormatException e) {
            chapterNum = 0;
        }
        String cpsrc = selectFirstElement(doc, "input#cpsrc");
        String gid = selectFirstElement(doc, "input#gid");
        String summary = getSummary(doc);

        String lastChapterTitle = getLastChapterTitle(doc);
        NovelDetailModel novelDetailModel = new NovelDetailModel(
                gid,
                cpsrc,
                chapterNum,
                lastChapterTitle,
                summary,
                getTags(doc),
                null
        );
        return novelDetailModel;
    }

    private String selectFirstElement(Document doc, String query) {
        String dataValue = "";
        Elements dataElements = doc.select(query);
        if(!dataElements.isEmpty()) {
            dataValue = dataElements.first().attr("value");
        }
        return dataValue;
    }

    private String getSummary(Document doc) {
        String dataValue = "暂无简介";
        Elements dataElements = doc.select("dd.summary");
        if(!dataElements.isEmpty()) {
            dataValue = dataElements.first().html();
        }
        return dataValue;
    }

    private String[] getTags(Document doc) {
        String[] tags = new String[0];
        Elements dataElements = doc.select("table td[data-subtag]");
        if(!dataElements.isEmpty()) {
            tags = new String[dataElements.size()];
            for(int i = 0; i < dataElements.size(); i++) {
                tags[i] = dataElements.get(i).attr("data-subtag");
            }
        }
        return tags;
    }

    private String getLastChapterTitle(Document doc) {
        String dataValue = "";
        Elements dataElements = doc.select("dd[data-function] > div.chname");
        if(!dataElements.isEmpty()) {
            dataValue = dataElements.first().html();
        }
        return dataValue;
    }
}