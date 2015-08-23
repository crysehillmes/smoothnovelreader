package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.qualifier.PrefsGrayScaleInNight;
import org.cryse.novelreader.qualifier.PrefsShowCoverImage;
import org.cryse.novelreader.ui.adapter.NovelModelListAdapter;
import org.cryse.novelreader.ui.adapter.NovelOnlineListAdapter;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.RecyclerViewUtils;
import org.cryse.novelreader.util.SnackbarTextDelegate;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.view.NovelOnlineListView;
import org.cryse.widget.recyclerview.animator.ScaleInOutItemAnimator;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NovelListFragment extends AbstractFragment implements NovelOnlineListView {
    public static final int QUERY_TYPE_SEARCH = 11;
    public static final int QUERY_TYPE_CATEGORY = 13;
    public static final int QUERY_TYPE_RANK = 19;
    private static final String LOG_TAG = NovelListFragment.class.getName();
    private static final String CONTRACT_LIST_PAGE = "list_page";
    private static final String CONTRACT_LIST_CONTENT = "list_content";
    private static final String CONTRACT_QUERY_TYPE = "query_type";
    private static final String CONTRACT_FRAGMENT_TITLE = "fragment_title";
    private static final String CONTRACT_QUERY_STRING = "query_string";
    private static final String CONTRACT_SUB_QUERY_STRING = "sub_query_string";
    private static final String CONTRACT_IS_QUERY_BY_TAG = "is_query_by_tag";
    protected ArrayList<NovelModel> mNovelList;
    protected NovelModelListAdapter mNovelListAdapter;
    protected int mCurrentListPageNumber = 0;
    protected boolean isLoadingMore = false;
    protected boolean isLoading = false;
    protected boolean isNoMore = false;
    protected boolean showCoverImage;
    protected boolean grayScaleInNight;
    protected int mQueryType;
    protected String mFragmentTitle;
    protected String mQueryString;
    protected String mSubQueryString;
    protected boolean mIsQueryByTag;
    @Inject
    NovelListPresenter mPresenter;
    @Inject
    @PrefsShowCoverImage
    BooleanPreference mIsShowCoverImage;
    @Inject
    @PrefsGrayScaleInNight
    BooleanPreference mGrayScaleInNight;
    @Bind(R.id.novel_listview)
    SuperRecyclerView mListView;
    @Bind(R.id.empty_view_text_prompt)
    TextView mEmptyViewText;
    private Handler mHandler;
    private View mContentView;

    public static NovelListFragment newInstance(
            int queryType,
            String fragmentTitle,
            String queryString,
            String subQueryString,
            boolean isQueryByTag
    ) {
        NovelListFragment myFragment = new NovelListFragment();

        Bundle args = new Bundle();
        args.putInt(CONTRACT_QUERY_TYPE, queryType);
        args.putString(CONTRACT_FRAGMENT_TITLE, fragmentTitle);
        args.putString(CONTRACT_QUERY_STRING, queryString);
        args.putString(CONTRACT_SUB_QUERY_STRING, subQueryString);
        args.putBoolean(CONTRACT_IS_QUERY_BY_TAG, isQueryByTag);

        myFragment.setArguments(args);

        return myFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mHandler = new Handler();

        Bundle bundle = getArguments();
        if(bundle != null && bundle.containsKey(CONTRACT_QUERY_TYPE)) {
            mQueryType = bundle.getInt(CONTRACT_QUERY_TYPE);
            mFragmentTitle = bundle.getString(CONTRACT_FRAGMENT_TITLE);
            mQueryString = bundle.getString(CONTRACT_QUERY_STRING);
            mSubQueryString = bundle.getString(CONTRACT_SUB_QUERY_STRING);
            mIsQueryByTag = bundle.getBoolean(CONTRACT_IS_QUERY_BY_TAG);
        } else
            throw new IllegalArgumentException("Must set category_keyword.");
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(getActivity()).inject(this);
    }

    protected NovelListPresenter getPresenter() {
        return mPresenter;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_novel_list,null);
        ButterKnife.bind(this, mContentView);
        showCoverImage = mIsShowCoverImage.get();
        grayScaleInNight = mGrayScaleInNight.get();
        mEmptyViewText.setText(getActivity().getString(R.string.empty_view_prompt));
        initListView();
        mListView.setClipToPadding(false);
        UIUtils.setInsets(getActivity(), mListView, false, false, true, Build.VERSION.SDK_INT < 21);
        return mContentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        mListView.getRecyclerView().setItemAnimator(
                new ScaleInOutItemAnimator(
                        0.1f,
                        350,
                        350,
                        1.5f
                )
        );
        mNovelList = new ArrayList<NovelModel>();
        mNovelListAdapter = createAdapter();
        mListView.setAdapter(mNovelListAdapter);
        mListView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && mCurrentListPageNumber < 4) { // load more would load +1, so current must less than 4
                //isLoadingMore = true;
                loadMore(mCurrentListPageNumber);
            } else {
                mListView.setLoadingMore(false);
                mListView.hideMoreProgress();
            }
        });
        mNovelListAdapter.setOnItemClickListener((view, position, id) -> {
            NovelModel novelModel = mNovelListAdapter.getItem(position);
            getPresenter().showNovelIntroduction(novelModel);
        });
    }

    protected void loadData() {
        switch (mQueryType) {
            case QUERY_TYPE_SEARCH:
                getPresenter().searchNovel(mQueryString, 0, false);
                break;
            case QUERY_TYPE_CATEGORY:
                getPresenter().loadNovelCategoryList(mQueryString, mSubQueryString, 0, 0, mIsQueryByTag, false);
                break;
            case QUERY_TYPE_RANK:
                getPresenter().loadNovelRankList(mQueryString, 0, false);
                break;
            default:
                throw new IllegalStateException("Wrong query_type.");
        }
    }
    protected void loadMore(int currentPage) {
        switch (mQueryType) {
            case QUERY_TYPE_SEARCH:
                getPresenter().searchNovel(mQueryString, currentPage + 1, true);
                break;
            case QUERY_TYPE_CATEGORY:
                getPresenter().loadNovelCategoryList(mQueryString, mSubQueryString, currentPage + 1, 0, mIsQueryByTag, true);
                break;
            case QUERY_TYPE_RANK:
                getPresenter().loadNovelRankList(mQueryString, currentPage + 1, true);
                break;
            default:
                throw new IllegalStateException("Wrong query_type.");
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(savedInstanceState != null && savedInstanceState.containsKey(CONTRACT_LIST_CONTENT)) {
            ArrayList<NovelModel> list = savedInstanceState.getParcelableArrayList(CONTRACT_LIST_CONTENT);
            mNovelList.addAll(list);
            mNovelListAdapter.notifyDataSetChanged();
            mCurrentListPageNumber = savedInstanceState.getInt(CONTRACT_LIST_PAGE);
            mQueryType = savedInstanceState.getInt(CONTRACT_QUERY_TYPE);
            mFragmentTitle = savedInstanceState.getString(CONTRACT_FRAGMENT_TITLE);
            mQueryString = savedInstanceState.getString(CONTRACT_QUERY_STRING);
            mSubQueryString = savedInstanceState.getString(CONTRACT_SUB_QUERY_STRING);
            mIsQueryByTag = savedInstanceState.getBoolean(CONTRACT_IS_QUERY_BY_TAG);
        } else {
            mListView.getSwipeToRefresh().measure(1,1);
            mListView.getSwipeToRefresh().setRefreshing(true);
            loadData();
        }

        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            mainActivity.onSectionAttached(mFragmentTitle);
            // mainActivity.showDrawToggleAsUp(true);
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(CONTRACT_LIST_PAGE, mCurrentListPageNumber);
        outState.putParcelableArrayList(CONTRACT_LIST_CONTENT, mNovelList);
        outState.putInt(CONTRACT_QUERY_TYPE, mQueryType);
        outState.putString(CONTRACT_FRAGMENT_TITLE, mFragmentTitle);
        outState.putString(CONTRACT_QUERY_STRING, mQueryString);
        outState.putString(CONTRACT_SUB_QUERY_STRING, mSubQueryString);
        outState.putBoolean(CONTRACT_IS_QUERY_BY_TAG, mIsQueryByTag);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            MainActivity mainActivity = (MainActivity)activity;
            // mainActivity.showDrawToggleAsUp(false);
            mainActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_online_list, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_change_theme:
                getThemedActivity().setNightMode(!isNightMode());
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        getPresenter().destroy();
        super.onDestroy();
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        if(mIsShowCoverImage.get() != showCoverImage || mGrayScaleInNight.get() != grayScaleInNight) {
            //mNovelListAdapter = new NovelOnlineListAdapter(getActivity(), mNovelList, mIsShowCoverImage.get(), isNightMode(), mGrayScaleInNight.get());
            mNovelListAdapter = createAdapter();
            mListView.setAdapter(mNovelListAdapter);
            mNovelListAdapter.notifyDataSetChanged();
            showCoverImage = mIsShowCoverImage.get();
            grayScaleInNight = mGrayScaleInNight.get();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
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

    private synchronized void addToListView(List<NovelModel> novelModels) {
        RecyclerViewUtils.addOneByOne(mHandler, mNovelListAdapter, 50l, novelModels);
    }

    @Override
    public void getNovelListSuccess(List<NovelModel> novels, boolean append) {
        if(append) {
            if (novels.size() == 0) isNoMore = true;
            //isLoadingMore = false;
            if (novels.size() != 0) mCurrentListPageNumber++;
            addToListView(novels);
        } else {
            isNoMore = false;
            mCurrentListPageNumber = 0;
            mNovelList.clear();
            addToListView(novels);
            if (getResources().getBoolean(R.bool.isTablet)) {
                //isLoadingMore = true;
                loadMore(mCurrentListPageNumber);
            }
        }
    }

    @Override
    public void getNovelListFailure(Throwable e, Object... extras) {
        SnackbarUtils snackbarUtils = new SnackbarUtils(new SnackbarTextDelegate(getActivity()));
        snackbarUtils.showExceptionToast(this, e);
    }

    @Override
     public void setLoading(Boolean value) {
        isLoading = value;
        mListView.getSwipeToRefresh().setRefreshing(value);
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
        mListView.setLoadingMore(value);
        if (value)
            mListView.showMoreProgress();
        else
            mListView.hideMoreProgress();
    }

    protected NovelModelListAdapter createAdapter() {
        return new NovelOnlineListAdapter(
                getAppCompatActivity().getSupportActionBar().getThemedContext(),
                mNovelList,
                mIsShowCoverImage.get(),
                isNightMode(),
                mGrayScaleInNight.get()
        );
    }
}
