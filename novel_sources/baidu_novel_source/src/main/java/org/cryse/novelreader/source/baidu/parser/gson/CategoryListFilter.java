package org.cryse.novelreader.source.baidu.parser.gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CategoryListFilter implements JsonFilter {
    private static Pattern pattern = Pattern.compile("(?<=(\\\"dataset\\\"\\s{0,5}:))(.+)(\\})(?=(\\}{3,5}))");
    @Override
    public boolean filter(String input, StringBuilder outputBuilder) {
        if(input.contains("catelist") && input.contains("dataset")) {
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
