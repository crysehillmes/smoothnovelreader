package org.cryse.novelreader.source.baidu.parser.gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 2014/3/22.
 * Email: tyk5555@hotmail.com
 */
public class ChapterContentFilter implements JsonFilter {
    private static Pattern pattern = Pattern.compile("(?<=(\\\"dataset\\\"\\s{0,5}:))(.+)(\\})(?=(\\}{3,5}))");
    @Override
    public boolean filter(String input, StringBuilder outputBuilder) {
        if(input.contains("content") && input.contains("dataset") && !input.contains("items")) {
            Matcher matcher = pattern.matcher(input);
            if(matcher.find()) {
                outputBuilder.append(matcher.group(0));
                return true;
            }
        }
        return false;
    }

    @Override
    public String getFilterName() {
        return getClass().getName();
    }
}
