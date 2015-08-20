package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.qualifier.PrefsGrayScaleInNight;
import org.cryse.novelreader.qualifier.PrefsShowCoverImage;
import org.cryse.novelreader.ui.adapter.NovelModelListAdapter;
import org.cryse.novelreader.ui.adapter.NovelOnlineListAdapter;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SnackbarTextDelegate;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.view.NovelOnlineListView;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class SearchActivity extends AbstractThemeableActivity implements NovelOnlineListView {
    private static final String LOG_TAG = SearchActivity.class.getName();
    @Inject
    NovelListPresenter mPresenter;

    @Inject
    @PrefsShowCoverImage
    BooleanPreference mIsShowCoverImage;

    @Inject
    @PrefsGrayScaleInNight
    BooleanPreference mGrayScaleInNight;

    @Bind(R.id.novel_searchlistview)
    SuperRecyclerView mListView;
    SearchView mSearchView = null;
    private NovelModelListAdapter mSearchListAdapter;
    private List<NovelModel> mSearchNovelList;
    private String mQueryString = null;
    private int currentListPageNumber = 0;
    private boolean isNoMore = false;
    private boolean isLoadingMore = false;
    private boolean isLoading = false;
    private boolean showCoverImage;
    private boolean grayScaleInNight;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        ButterKnife.bind(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showCoverImage = mIsShowCoverImage.get();
        grayScaleInNight = mGrayScaleInNight.get();

        UIUtils.setInsets(this, mListView, false, false, true, Build.VERSION.SDK_INT < 21);
        String query = getIntent().getStringExtra(SearchManager.QUERY);
        query = query == null ? "" : query;
        mQueryString = query;

        initListView();
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
        if(mIsShowCoverImage.get() != showCoverImage || mGrayScaleInNight.get() != grayScaleInNight) {
            mSearchListAdapter = new NovelOnlineListAdapter(this, mSearchNovelList, mIsShowCoverImage.get(), isNightMode(), mGrayScaleInNight.get());
            mListView.setAdapter(mSearchListAdapter);
            mSearchListAdapter.notifyDataSetChanged();
            showCoverImage = mIsShowCoverImage.get();
            grayScaleInNight = mGrayScaleInNight.get();
        }
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(this).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    public NovelListPresenter getPresenter() {
        return mPresenter;
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        //mListView.getList().setItemAnimator(new ScaleInOutItemAnimator(mListView.getList()));
        mSearchNovelList = new ArrayList<NovelModel>();
        mSearchListAdapter = new NovelOnlineListAdapter(this, mSearchNovelList, mIsShowCoverImage.get(), isNightMode(), mGrayScaleInNight.get());
        mListView.setAdapter(mSearchListAdapter);
        mListView.getSwipeToRefresh().setColorSchemeResources(
                ColorUtils.getRefreshProgressBarColors()[0],
                ColorUtils.getRefreshProgressBarColors()[1],
                ColorUtils.getRefreshProgressBarColors()[2],
                ColorUtils.getRefreshProgressBarColors()[3]
        );

        mListView.setOnMoreListener((numberOfItems, numberBeforeMore, currentItemPos) -> {
            if (!isNoMore && !isLoadingMore && currentListPageNumber < 4) { // load more would load +1, so current must less than 4
                getPresenter().searchNovel(mQueryString, currentListPageNumber + 1, true);
            } else {
                mListView.setLoadingMore(false);
                mListView.hideMoreProgress();
            }
        });
        mListView.setOnItemClickListener((view, position, id) -> {
            NovelModel novelModel = mSearchListAdapter.getItem(position);
            getPresenter().showNovelIntroduction(novelModel);
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        final MenuItem searchItem = menu.findItem(R.id.menu_search);
        if (searchItem != null) {
            SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
            final SearchView view = (SearchView) searchItem.getActionView();
            mSearchView = view;
            if (view == null) {
                // LOGW(TAG, "Could not set up search view, view is null.");
            } else {
                view.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                view.setIconified(false);
                view.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                    @Override
                    public boolean onQueryTextSubmit(String s) {
                        search(s);
                        view.clearFocus();
                        return true;
                    }

                    @Override
                    public boolean onQueryTextChange(String s) {

                        return true;
                    }
                });
                view.setOnCloseListener(new SearchView.OnCloseListener() {
                    @Override
                    public boolean onClose() {
                        finish();
                        return false;
                    }
                });
            }

            if (!TextUtils.isEmpty(mQueryString)) {
                view.setQuery(mQueryString, false);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_search:
                return true;
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void search(String query) {
        mQueryString = query;
        if(mQueryString != null && mQueryString.length() > 0) {
            mListView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().searchNovel(mQueryString, 0, false);
        } else {
            mSearchNovelList.clear();
        }
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
        SnackbarUtils snackbarUtils = new SnackbarUtils(new SnackbarTextDelegate(this));
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

    @Override
    public void showSnackbar(CharSequence text, SimpleSnackbarType type) {
        Snackbar snackbar = SnackbarUtils.makeSimple(getSnackbarRootView(), text, type, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }
}
