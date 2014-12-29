package org.cryse.novelreader.util.parser;

import android.text.TextUtils;

import org.cryse.novelreader.util.EmptyContentException;
import org.cryse.novelreader.util.NovelTextFilter;

public class NovelTextSimplifyFilter implements NovelTextFilter {

    @Override
    public String filter(String source) {
        return processBody(source);
    }

    private String processBody(String body) {
        if(body == null || (body != null && TextUtils.isEmpty(body)))
            throw new EmptyContentException("NovelTextSimplifyFilter::processBody: body is null or empty.");
        String[] lines = body.split("\n");
        StringBuilder builder = new StringBuilder();
        for(String line : lines) {
            builder.append(singleLineToParagraph(line));
        }
        return builder.toString();
    }

    public static String trimAllSpace(String str) {
        return str == null ? str : str.replaceAll("^[\\s\\u3000]*|[\\s\\u3000]*$", "");
    }

    private String singleLineToParagraph(String line) {
        StringBuilder builder = new StringBuilder();
        String trimmedLine = trimAllSpace(line);
        if(trimmedLine.length() == 0)
            return "";
        builder.append(getIndentString()).append(trimmedLine).append("\n");
        return builder.toString();
    }

    public String getIndentString() {
        return  "\u3000\u3000";
    }
}
