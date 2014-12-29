package org.cryse.novelreader.ui.widget;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.View;

import com.handmark.pulltorefresh.extras.viewpager.PullToRefreshViewPager;
import com.handmark.pulltorefresh.library.PullToRefreshBase;

public class ViewPagerReadWidget extends PullToRefreshViewPager implements ReadWidget {
    private OnContentRequestListener mContentRequestListener;
    private OnPageChangedListener mPageChangedListener;

    public ViewPagerReadWidget(Context context) {
        super(context);
    }

    public ViewPagerReadWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public int getCurrentPage() {
        return getRefreshableView().getCurrentItem();
    }

    @Override
    public void setCurrentPage(int position, boolean animation) {
        getRefreshableView().setCurrentItem(position, animation);
    }

    @Override
    public void setAdapter(ReadWidgetAdapter adapter) {
        if(!(adapter instanceof PagerAdapter))
            throw new IllegalArgumentException("ViewPagerReadWidget only work with PagerAdapter.");
        getRefreshableView().setAdapter((PagerAdapter) adapter);

    }

    @Override
    public View getReadDisplayView() {
        return getRefreshableView();
    }

    @Override
    public void setOnContentRequestListener(OnContentRequestListener listener) {
        mContentRequestListener = listener;
        setOnRefreshListener(new OnRefreshListener2<ViewPager>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ViewPager> viewPagerPullToRefreshBase) {
                if(mContentRequestListener != null) {
                    mContentRequestListener.onRequestPrevious();
                }
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ViewPager> viewPagerPullToRefreshBase) {
                if(mContentRequestListener != null) {
                    mContentRequestListener.onRequestNext();
                }
            }
        });
    }

    @Override
    public void setOnPageChangedListener(OnPageChangedListener listener) {
        mPageChangedListener = listener;
        getRefreshableView().setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {
                if(mPageChangedListener != null)
                    mPageChangedListener.onPageChanged(i);
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
    }

    @Override
    public void setLoading(boolean isLoading) {
        if(!isLoading)
            stopRefreshing();
    }
}
