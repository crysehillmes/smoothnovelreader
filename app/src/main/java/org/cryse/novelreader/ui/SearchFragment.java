package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.qualifier.PrefsGrayScaleInNight;
import org.cryse.novelreader.qualifier.PrefsShowCoverImage;
import org.cryse.novelreader.ui.adapter.NovelModelListAdapter;
import org.cryse.novelreader.ui.adapter.NovelOnlineListAdapter;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.SnackbarTextDelegate;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.view.NovelOnlineListView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchFragment extends AbstractFragment implements NovelOnlineListView {
    private static final String LOG_TAG = SearchFragment.class.getName();
    @Inject
    NovelListPresenter mPresenter;

    @Inject
    @PrefsShowCoverImage
    BooleanPreference mIsShowCoverImage;

    @Inject
    @PrefsGrayScaleInNight
    BooleanPreference mGrayScaleInNight;

    @Bind(R.id.novel_searchlistview)
    SuperRecyclerView mCollectionView;

    private NovelModelListAdapter mSearchListAdapter;
    private List<NovelModel> mSearchNovelList;
    private int currentListPageNumber = 0;
    private boolean isNoMore = false;
    private boolean isLoadingMore = false;
    private boolean isLoading = false;
    private boolean showCoverImage;
    private boolean grayScaleInNight;
    private String mSearchString;
    private OnSearchResultListener mOnSearchResultListener;

    public static SearchFragment newInstance(String searchString, OnSearchResultListener onSearchResultListener) {
        SearchFragment searchFragment = new SearchFragment();
        Bundle args = new Bundle();
        args.putString(DataContract.SEARCH_STRING, searchString);
        searchFragment.setArguments(args);
        searchFragment.mOnSearchResultListener = onSearchResultListener;
        return searchFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);

        // UIUtils.setInsets(this, mCollectionView, false, false, true, Build.VERSION.SDK_INT < 21);
        Bundle args = getArguments();
        if (args != null) {
            if (args.containsKey(DataContract.SEARCH_STRING)) {
                mSearchString = args.getString(DataContract.SEARCH_STRING);
            }
        }
        /*if(TextUtils.isEmpty(mSearchString)) {
            throw new IllegalArgumentException("Need SEARCH_STRING param.");
        }*/
        showCoverImage = mIsShowCoverImage.get();
        grayScaleInNight = mGrayScaleInNight.get();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_search, null);
        ButterKnife.bind(this, contentView);
        initListView();
        return contentView;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        search();
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mIsShowCoverImage.get() != showCoverImage || mGrayScaleInNight.get() != grayScaleInNight) {
            mSearchListAdapter = new NovelOnlineListAdapter(getContext(), mSearchNovelList, mIsShowCoverImage.get(), isNightMode(), mGrayScaleInNight.get());
            mCollectionView.setAdapter(mSearchListAdapter);
            mSearchListAdapter.notifyDataSetChanged();
            showCoverImage = mIsShowCoverImage.get();
            grayScaleInNight = mGrayScaleInNight.get();
        }
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(getContext()).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    public NovelListPresenter getPresenter() {
        return mPresenter;
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        //mCollectionView.getList().setItemAnimator(new ScaleInOutItemAnimator(mCollectionView.getList()));
        mSearchNovelList = new ArrayList<NovelModel>();
        int columnCount = getResources().getInteger(R.integer.search_column_count);
        RecyclerView.LayoutManager layoutManager;
        if (columnCount == 1) {
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            layoutManager = new GridLayoutManager(getContext(), columnCount);
        }
        mCollectionView.setLayoutManager(layoutManager);
        mSearchListAdapter = new NovelOnlineListAdapter(getContext(), mSearchNovelList, mIsShowCoverImage.get(), isNightMode(), mGrayScaleInNight.get());
        mCollectionView.setAdapter(mSearchListAdapter);

        mCollectionView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && currentListPageNumber < 4) { // load more would load +1, so current must less than 4
                getPresenter().searchNovel(mSearchString, currentListPageNumber + 1, true);
            } else {
                mCollectionView.setLoadingMore(false);
                mCollectionView.hideMoreProgress();
            }
        });
        mSearchListAdapter.setOnItemClickListener((view, position, id) -> {
            NovelModel novelModel = mSearchListAdapter.getItem(position);
            // getPresenter().showNovelIntroduction(novelModel);
            if (mOnSearchResultListener != null) {
                mOnSearchResultListener.onItemClick(novelModel, position);
            }
        });
    }

    private void search() {
        if (!TextUtils.isEmpty(mSearchString)) {
            getPresenter().searchNovel(mSearchString, 0, false);
        }
    }

    public void search(String query) {
        if (TextUtils.isEmpty(query)) {
            throw new IllegalArgumentException("Param query should not be empty.");
        } else {
            this.mSearchString = query;
            search();
        }
    }

    public void clearSearch() {
        mSearchNovelList.clear();
        mSearchListAdapter.notifyDataSetChanged();
    }

    @Override
    public void getNovelListSuccess(List<NovelModel> novels, boolean append) {
        if (append) {
            if (novels.size() == 0) isNoMore = true;
            if (novels.size() != 0) currentListPageNumber++;
            mSearchNovelList.addAll(novels);
            mSearchListAdapter.notifyDataSetChanged();
        } else {
            isNoMore = false;
            currentListPageNumber = 0;
            mSearchNovelList.clear();
            mSearchNovelList.addAll(novels);
            mSearchListAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void getNovelListFailure(Throwable e, Object... extras) {
        SnackbarUtils snackbarUtils = new SnackbarUtils(new SnackbarTextDelegate(getContext()));
        snackbarUtils.showExceptionToast(this, e);
    }

    @Override
    public void setLoading(Boolean value) {
        isLoading = value;
        if (isLoading) {
            mCollectionView.showProgress();
        } else {
            mCollectionView.hideProgress();
            mCollectionView.showRecycler();
        }
    }

    @Override
    public Boolean isLoading() {
        return isLoading;
    }

    @Override
    public boolean isLoadingMore() {
        return isLoadingMore;
    }

    @Override
    public void setLoadingMore(boolean value) {
        isLoadingMore = value;
        mCollectionView.setLoadingMore(value);
        if (value)
            mCollectionView.showMoreProgress();
        else
            mCollectionView.hideMoreProgress();
    }

    public interface OnSearchResultListener {
        void onItemClick(NovelModel novelModel, int position);
    }
}
