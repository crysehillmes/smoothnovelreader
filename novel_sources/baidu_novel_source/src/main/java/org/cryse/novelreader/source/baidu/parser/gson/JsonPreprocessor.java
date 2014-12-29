package org.cryse.novelreader.source.baidu.parser.gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Cryse Hillmess(YINKUN TAO) on 14-2-2.
 * Email: tyk5555@hotmail.com
 */
public class JsonPreprocessor {
    protected static List<JsonFilter> jsonFilters = new ArrayList<JsonFilter>(Arrays.asList(new JsonFilter[]{
            new CategoryListFilter(),
            new SyncShelfFilter(),
            new RankListFilter(),
            new ChapterListFilter(),
            new ChapterContentFilter(),
            new SearchFilter(),
            new ChangeSrcFilter()
    }));

    public String process(String input) throws IOException {
        boolean isProcessed = false;
        String filterName = "";
        String output = input;
        for (JsonFilter filter : jsonFilters) {
            StringBuilder builder = new StringBuilder();
            boolean ret = filter.filter(input, builder);
            if (ret && isProcessed) {
                throw new IOException(filterName + "||||" + filter.getFilterName() );
            }
            isProcessed = ret;
            if(ret) {
                filterName = filter.getFilterName();
                output = builder.toString();
            }
        }
        return output;
    }
}
