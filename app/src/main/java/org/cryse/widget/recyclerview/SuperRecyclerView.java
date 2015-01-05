package org.cryse.widget.recyclerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.widget.FrameLayout;

import org.cryse.novelreader.R;
public class SuperRecyclerView extends FrameLayout {

    protected              int   ITEM_LEFT_TO_LOAD_MORE = 10;

    protected ViewStub    mProgress;
    protected ViewStub    mMoreProgress;
    protected RecyclerView mRecyclerView;
    protected ViewStub    mEmpty;

    protected float   mDividerHeight;
    protected int     mDivider;
    protected boolean mClipToPadding;
    protected int     mPadding;
    protected int     mPaddingTop;
    protected int     mPaddingBottom;
    protected int     mPaddingLeft;
    protected int     mPaddingRight;
    protected int     mScrollbarStyle;
    protected int     mEmptyId;
    protected int     mMoreProgressId;
    protected int     mColumnsCount;
    protected RecyclerView.LayoutManager mLayoutManager;
    protected RecyclerView.OnScrollListener mOnScrollListener;

    protected OnMoreListener mOnMoreListener;
    protected boolean            isLoadingMore;
    protected int                mSelector;
    protected SwipeRefreshLayout mPtrLayout;

    protected int msuperrecyclerviewMainLayout;
    private   int mProgressId;

    public SwipeRefreshLayout getSwipeToRefresh() {
        return mPtrLayout;
    }

    public RecyclerView getList() {
        return mRecyclerView;
    }


    public SuperRecyclerView(Context context) {
        super(context);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initAttrs(attrs);
        initView();
    }

    public SuperRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initAttrs(attrs);
        initView();
    }

    protected void initAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.superrecyclerview);
        try {
            mClipToPadding = a.getBoolean(R.styleable.superrecyclerview_srv_listClipToPadding, false);
            mDivider = a.getColor(R.styleable.superrecyclerview_srv_listDivider, 0);
            mDividerHeight = a.getDimension(R.styleable.superrecyclerview_srv_listDividerHeight, 0.0f);
            mPadding = (int) a.getDimension(R.styleable.superrecyclerview_srv_listPadding, -1.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.superrecyclerview_srv_listPaddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.superrecyclerview_srv_listPaddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.superrecyclerview_srv_listPaddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.superrecyclerview_srv_listPaddingRight, 0.0f);
            mScrollbarStyle = a.getInt(R.styleable.superrecyclerview_srv_scrollbarStyle, -1);
            mEmptyId = a.getResourceId(R.styleable.superrecyclerview_srv_empty, 0);
            mMoreProgressId = a.getResourceId(R.styleable.superrecyclerview_srv_moreProgress, R.layout.widget_superrecyclerview_more_progress);
            mProgressId = a.getResourceId(R.styleable.superrecyclerview_srv_progress, R.layout.widget_superrecyclerview_progress);
            mSelector = a.getResourceId(R.styleable.superrecyclerview_srv_listSelector, 0);
            mColumnsCount = a.getInt(R.styleable.superrecyclerview_srv_columns, 1);
            msuperrecyclerviewMainLayout = a.getResourceId(R.styleable.superrecyclerview_srv_mainLayoutID, R.layout.widget_superrecyclerview);
        } finally {
            a.recycle();
        }
    }

    private void initView() {
        if (isInEditMode()) {
            return;
        }
        View v = LayoutInflater.from(getContext()).inflate(msuperrecyclerviewMainLayout, this);
        mPtrLayout = (SwipeRefreshLayout) v.findViewById(R.id.ptr_layout);
        mPtrLayout.setEnabled(false);

        mProgress = (ViewStub) v.findViewById(android.R.id.progress);

        mProgress.setLayoutResource(mProgressId);
        mProgress.inflate();

        mMoreProgress = (ViewStub) v.findViewById(R.id.more_progress);
        mMoreProgress.setLayoutResource(mMoreProgressId);
        if (mMoreProgressId != 0)
            mMoreProgress.inflate();
        mMoreProgress.setVisibility(View.GONE);

        mEmpty = (ViewStub) v.findViewById(R.id.empty);
        mEmpty.setLayoutResource(mEmptyId);
        if (mEmptyId != 0)
            mEmpty.inflate();
        mEmpty.setVisibility(View.GONE);

        initAbsListView(v);
    }

    /**
     *  Customize the AbsListView
     */
    protected void initAbsListView(View v) {

        View recyclerView = v.findViewById(android.R.id.list);

        if (recyclerView instanceof RecyclerView)
            mRecyclerView = (RecyclerView) recyclerView;
        else
            throw new IllegalArgumentException("SuperRecyclerView works with a RecyclerView!");


        if (mRecyclerView !=null) {
            if(mLayoutManager == null)
                mLayoutManager = getDefaultLayoutManager();
            mRecyclerView.setLayoutManager(mLayoutManager);
            mRecyclerView.setClipToPadding(mClipToPadding);

            //getList().setDivider(mDivider);
            //getList().setDividerHeight((int) mDividerHeight);

            mRecyclerView.setOnScrollListener(InnerOnScrollListener);
            /*if (mSelector != 0)
                mRecyclerView.setSelector(mSelector);*/

            if (mPadding != -1.0f) {
                mRecyclerView.setPadding(mPadding, mPadding, mPadding, mPadding);
            } else {
                mRecyclerView.setPadding(mPaddingLeft, mPaddingTop, mPaddingRight, mPaddingBottom);
            }

            if (mScrollbarStyle != -1)
                mRecyclerView.setScrollBarStyle(mScrollbarStyle);
        }
    }


    /**
     * Set the adapter to the listview
     * Automativally hide the progressbar
     * Set the refresh to false
     * If adapter is empty, then the emptyview is shown
     * @param adapter
     */
    public void setAdapter(RecyclerViewBaseAdapter adapter) {
        mRecyclerView.setAdapter(adapter);
        mProgress.setVisibility(View.GONE);
        /*if (mEmpty != null && mEmptyId != 0)
            mRecyclerView.setEmptyView(mEmpty);*/
        mRecyclerView.setVisibility(View.VISIBLE);
        mPtrLayout.setRefreshing(false);
        adapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            private void checkEmptyState() {
                mProgress.setVisibility(View.GONE);
                isLoadingMore = false;
                mPtrLayout.setRefreshing(false);
                if (mRecyclerView.getAdapter().getItemCount() == 0 && mEmptyId != 0) {
                    mEmpty.setVisibility(View.VISIBLE);
                } else if (mEmptyId != 0) {
                    mEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmptyState();
            }

            @Override
            public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                super.onItemRangeMoved(fromPosition, toPosition, itemCount);
                checkEmptyState();
            }

            @Override
            public void onItemRangeChanged(int positionStart, int itemCount) {
                super.onItemRangeChanged(positionStart, itemCount);
                checkEmptyState();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmptyState();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmptyState();
            }
        });
        if ((adapter == null || adapter.getItemCount() == 0) && mEmptyId != 0) {
            mEmpty.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Remove the adapter from the listview
     */
    public void removeAdapter() {
        getList().setAdapter(null);
    }

    /**
     * Show the progressbar
     */
    public void showProgress() {
        hideList();
        if(mEmptyId != 0 ) mEmpty.setVisibility(View.INVISIBLE);
        mProgress.setVisibility(View.VISIBLE);
    }

    /**
     * Hide the progressbar and show the listview
     */
    public void showList() {
        hideProgress();
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    public void showMoreProgress() {
        mMoreProgress.setVisibility(View.VISIBLE);

    }

    public void hideMoreProgress() {
        mMoreProgress.setVisibility(View.GONE);

    }

    /**
     * Set the listener when refresh is triggered and enable the SwipeRefreshLayout
     * @param listener
     */
    public void setRefreshListener(SwipeRefreshLayout.OnRefreshListener listener) {
        mPtrLayout.setEnabled(true);
        mPtrLayout.setOnRefreshListener(listener);
    }

    /**
     * Set the colors for the SwipeRefreshLayout states
     * @param col1
     * @param col2
     * @param col3
     * @param col4
     */
    public void setRefreshingColor(int col1, int col2, int col3, int col4) {
        mPtrLayout.setColorSchemeColors(col1, col2, col3, col4);
    }

    /**
     * Hide the progressbar
     */
    public void hideProgress() {
        mProgress.setVisibility(View.GONE);
    }

    /**
     * Hide the listview
     */
    public void hideList() {
        mRecyclerView.setVisibility(View.GONE);
    }

    /**
     * Set the scroll listener for the listview
     * @param listener
     */
    public void setOnScrollListener(RecyclerView.OnScrollListener listener) {
        mOnScrollListener = listener;
    }

    /**
     * Set the onItemClickListener for the listview
     * @param listener
     */
    public void setOnItemClickListener(RecyclerViewOnItemClickListener listener) {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if(adapter != null && adapter instanceof RecyclerViewBaseAdapter)
            ((RecyclerViewBaseAdapter) adapter).setOnItemClickListener(listener);
    }

    public void setOnItemLongClickListener(RecyclerViewOnItemLongClickListener listener) {
        RecyclerView.Adapter adapter = mRecyclerView.getAdapter();
        if(adapter != null && adapter instanceof RecyclerViewBaseAdapter)
            ((RecyclerViewBaseAdapter) adapter).setOnItemLongClickListener(listener);
    }

    /**
     *
     * @return the listview adapter
     */
    public RecyclerView.Adapter getAdapter() {
        return mRecyclerView.getAdapter();
    }

    private int getFirstVisiblePosition() {
        RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
        if(layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearLayoutManager = (LinearLayoutManager)layoutManager;
            return linearLayoutManager.findFirstVisibleItemPosition();
        } else if(layoutManager instanceof GridLayoutManager) {
            GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
            return gridLayoutManager.findFirstVisibleItemPosition();
        } else {
            throw new IllegalStateException();
        }
    }

    /**
     * Sets the More listener
     * @param onMoreListener
     * @param max            Number of items before loading more
     */
    public void setupMoreListener(OnMoreListener onMoreListener, int max) {
        mOnMoreListener = onMoreListener;
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public void setOnMoreListener(OnMoreListener onMoreListener) {
        mOnMoreListener = onMoreListener;
    }

    public void setNumberBeforeMoreIsCalled(int max) {
        ITEM_LEFT_TO_LOAD_MORE = max;
    }

    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    /**
     * Enable/Disable the More event
     * @param isLoadingMore
     */
    public void setLoadingMore(boolean isLoadingMore) {
        this.isLoadingMore = isLoadingMore;
    }

    /**
     * Remove the moreListener
     */
    public void removeMoreListener() {
        mOnMoreListener = null;
    }


    public void setOnTouchListener(OnTouchListener listener) {
        mRecyclerView.setOnTouchListener(listener);
    }

    protected RecyclerView.OnScrollListener InnerOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            if(mOnScrollListener != null) mOnScrollListener.onScrollStateChanged(recyclerView, newState);
            //super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            int totalItemCount = getAdapter().getItemCount();
            int firstVisibleItem = getFirstVisiblePosition();
            int lastVisibleItem = 0;
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if(layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager)layoutManager;
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();
                //return linearLayoutManager.findFirstVisibleItemPosition();
            } else if(layoutManager instanceof GridLayoutManager) {
                GridLayoutManager gridLayoutManager = (GridLayoutManager)layoutManager;
                lastVisibleItem = gridLayoutManager.findLastVisibleItemPosition();
                //return gridLayoutManager.findFirstVisibleItemPosition();
            } else {
                throw new IllegalStateException();
            }
            int visibleItemCount = lastVisibleItem - firstVisibleItem + 1;


            if (((totalItemCount - firstVisibleItem - visibleItemCount) == ITEM_LEFT_TO_LOAD_MORE || (totalItemCount - firstVisibleItem - visibleItemCount) == 0 && totalItemCount > visibleItemCount) && !isLoadingMore) {
                isLoadingMore = true;
                if (mOnMoreListener != null) {
                    mMoreProgress.setVisibility(View.VISIBLE);
                    mOnMoreListener.onMoreAsked(mRecyclerView.getAdapter().getItemCount(), ITEM_LEFT_TO_LOAD_MORE, firstVisibleItem);

                }
            }
            if(mOnScrollListener != null) mOnScrollListener.onScrolled(recyclerView, dx, dy);

        }
    };

    protected RecyclerView.LayoutManager getDefaultLayoutManager() {
        if(mColumnsCount == 1) {
            return new LinearLayoutManager(getContext());
        } else {
            return new GridLayoutManager(getContext(), mColumnsCount);
        }
    }

    public void setLayoutManager(RecyclerView.LayoutManager manager) {
        mLayoutManager = manager;
    }

    public interface OnMoreListener {
        /**
         * @param numberOfItems
         * @param numberBeforeMore
         * @param currentItemPos
         */
        public void onMoreAsked(int numberOfItems, int numberBeforeMore, int currentItemPos);
    }
}
