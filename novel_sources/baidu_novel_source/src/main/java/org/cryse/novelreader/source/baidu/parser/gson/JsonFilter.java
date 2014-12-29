package org.cryse.novelreader.source.baidu.parser.gson;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 14-2-2.
 * Email: tyk5555@hotmail.com
 */
public interface JsonFilter {
    public boolean filter(String input, StringBuilder outputBuilder);
    public String getFilterName();
}
