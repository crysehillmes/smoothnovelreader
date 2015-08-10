package org.cryse.novelreader.util;

import org.cryse.novelreader.model.Chapter;
import org.cryse.novelreader.model.ChapterModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChapterTitleUtils {
    static final Pattern chapterNumberPattern = Pattern.compile("(第[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部篇]?\\s+)|([第]?[0-9〇一二三四五六七八九十百千万壹贰叁肆伍陆柒捌玖零拾佰仟 ]+[章回节卷集幕计部篇]\\s+)");
    public static String shrinkTitle(String title) {
        Matcher matcher = chapterNumberPattern.matcher(title);
        int count = 0;
        int lastMatchStart = 0;
        while (matcher.find()) {
            lastMatchStart = matcher.start();
            count++;
        }
        String result;
        if(count >= 2 && lastMatchStart >=0 && lastMatchStart < title.length())
            result = title.substring(lastMatchStart);
        else
            result = title;
        return result;
    }

    public static List<ChapterModel> shrinkTitles(List<ChapterModel> models) {

        for(ChapterModel chapterModel : models) {
            Chapter chapter = (Chapter) chapterModel;
            chapter.setTitle(shrinkTitle(chapter.getTitle()));
        }
        return models;
    }
}
