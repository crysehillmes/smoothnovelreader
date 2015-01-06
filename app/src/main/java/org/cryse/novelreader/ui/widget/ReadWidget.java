package org.cryse.novelreader.ui.widget;

import android.view.View;

public interface ReadWidget {
    public int getPageCount();
    public int getCurrentPage();
    public void setCurrentPage(int position, boolean animation);
    public void setAdapter(ReadWidgetAdapter adapter);
    public View getReadDisplayView();
    public void setOnContentRequestListener(OnContentRequestListener listener);
    public void setOnPageChangedListener(OnPageChangedListener listener);
    public void setLoading(boolean isLoading);

    public interface OnContentRequestListener {
        public void onRequestPrevious();
        public void onRequestNext();
    }

    public interface OnPageChangedListener {
        public void onPageChanged(int position);
    }
}
