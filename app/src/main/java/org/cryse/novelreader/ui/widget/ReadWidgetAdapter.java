package org.cryse.novelreader.ui.widget;

import org.cryse.novelreader.util.colorschema.ColorSchema;

import java.util.ArrayList;
import java.util.List;

public interface ReadWidgetAdapter {
    int CURRENT = 0;
    int PREVIOUS = -1;
    int NEXT = 1;

    void replaceContent(List<CharSequence> newContents);

    void setDisplaySchema(ColorSchema displaySchema);

    void setFontSize(float fontSize);

    void setLineSpacing(float lineSpacingMultiplier);

    int getPageFromStringOffset(int offset);

    int getStringOffsetFromPage(int page);

    ArrayList<CharSequence> getContent();

    int getCount();

    void notifyDataSetChanged();
}
