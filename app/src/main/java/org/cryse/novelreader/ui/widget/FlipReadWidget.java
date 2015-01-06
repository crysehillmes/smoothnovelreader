package org.cryse.novelreader.ui.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.BaseAdapter;

import com.aphidmobile.flip.FlipViewController;

public class FlipReadWidget extends FlipViewController implements ReadWidget {
    private OnContentRequestListener mContentRequestListener;
    private OnPageChangedListener mPageChangedListener;

    public FlipReadWidget(Context context) {
        super(context);
    }

    public FlipReadWidget(Context context, int flipOrientation) {
        super(context, flipOrientation);
    }

    public FlipReadWidget(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @TargetApi(21)
    public FlipReadWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getPageCount() {
        return this.getAdapter() == null ? 0 : this.getAdapter().getCount();
    }

    @Override
    public int getCurrentPage() {
        return getSelectedItemPosition();
    }

    @Override
    public void setCurrentPage(int position, boolean animation) {
        setSelection(position);
    }

    @Override
    public void setAdapter(ReadWidgetAdapter adapter) {
        if(!(adapter instanceof BaseAdapter))
            throw new IllegalArgumentException("FlipReadWidget only work with BaseAdapter.");
        setAdapter((BaseAdapter)adapter);
    }

    @Override
    public View getReadDisplayView() {
        return this;
    }

    @Override
    public void setOnContentRequestListener(OnContentRequestListener listener) {
        mContentRequestListener = listener;
        setOnOverFlipListener((isLastPage, isFullyOverFlip, flipOffset) -> {
            if (isFullyOverFlip) {
                if (isLastPage) {
                    mContentRequestListener.onRequestNext();
                } else {
                    mContentRequestListener.onRequestPrevious();
                }
            }
        });
    }

    @Override
    public void setOnPageChangedListener(OnPageChangedListener listener) {
        mPageChangedListener = listener;
        setOnViewFlipListener(new ViewFlipListener() {
            @Override
            public void onViewFlipped(View view, int position) {
                mPageChangedListener.onPageChanged(position);
            }
        });
    }

    @Override
    public void setLoading(boolean isLoading) {

    }
}
