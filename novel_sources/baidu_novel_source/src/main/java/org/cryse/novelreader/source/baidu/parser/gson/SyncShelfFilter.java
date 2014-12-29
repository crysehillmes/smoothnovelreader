package org.cryse.novelreader.source.baidu.parser.gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 14-2-2.
 * Email: tyk5555@hotmail.com
 */
public class SyncShelfFilter implements JsonFilter {
    private static Pattern pattern = Pattern.compile("(?<=(\\\"syncshelf\\\"\\s{0,5}:))(.+)(\\})(?=(\\}{2,5}))");
    @Override
    public boolean filter(String input, StringBuilder outputBuilder) {
        if(input.contains("syncshelf") && input.contains("dataset")) {
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