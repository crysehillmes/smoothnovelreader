package org.cryse.novelreader.ui.widget;

import java.util.ArrayList;
import java.util.List;

public interface ReadWidgetAdapter {
    public static final int CURRENT = 0;
    public static final int PREVIOUS = -1;
    public static final int NEXT = 1;
    public void replaceContent(List<CharSequence> newContents);
    public void setBackgroundColor(int backgroundColor);
    public void setFontSize(float fontSize);
    public int getPageFromStringOffset(int offset);
    public int getStringOffsetFromPage(int page);
    public ArrayList<CharSequence> getContent();
    public int getCount();
    public void notifyDataSetChanged();
}
