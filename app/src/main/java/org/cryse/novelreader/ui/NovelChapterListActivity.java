package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.quentindommerc.superlistview.SuperListview;

import org.cryse.novelreader.util.ColorUtils;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.ui.adapter.NovelChapterListAdapter;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.view.NovelChaptersView;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.ToastProxy;
import org.cryse.novelreader.util.ToastType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class NovelChapterListActivity extends AbstractThemeableActivity implements NovelChaptersView{
    @Inject
    NovelChaptersPresenter mPresenter;

    @InjectView(R.id.novel_chapter_list_listview)
    SuperListview mListView;

    @InjectView(R.id.empty_view_text_prompt)
    TextView mEmptyViewText;

    NovelModel mNovel;
    ArrayList<NovelChapterModel> mNovelChapterList;

    NovelChapterListAdapter mChapterListAdapter;
    private MenuItem mMenuItemLastRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter_list);
        ButterKnife.inject(this);
        mEmptyViewText.setText(getString(R.string.empty_view_prompt));
        if(savedInstanceState != null) {
            mNovel = savedInstanceState.getParcelable(DataContract.NOVEL_OBJECT_NAME);
            mNovelChapterList = savedInstanceState.getParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME);
        } else {
            mNovel = getIntent().getParcelableExtra(DataContract.NOVEL_OBJECT_NAME);
        }
        UIUtils.setInsets(this, getToolbar(), false);
        initListView();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(mNovel.getTitle());
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        if(mNovelChapterList == null)
            mNovelChapterList = new ArrayList<NovelChapterModel>();
        mChapterListAdapter = new NovelChapterListAdapter(this, mNovelChapterList);
        mListView.setAdapter(mChapterListAdapter);
        mListView.getSwipeToRefresh().setColorSchemeResources(
                ColorUtils.getRefreshProgressBarColors()[0],
                ColorUtils.getRefreshProgressBarColors()[1],
                ColorUtils.getRefreshProgressBarColors()[2],
                ColorUtils.getRefreshProgressBarColors()[3]
        );
        mListView.setOnItemClickListener((parent, view, position, id) -> getPresenter().readChapter(
                mNovel,
                position,
                mNovelChapterList
        ));
        mListView.getList().setFastScrollEnabled(true);
        mListView.getList().setFastScrollAlwaysVisible(true);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        int index = -1, top = 0;
        if(savedInstanceState != null) {
            mNovel = savedInstanceState.getParcelable(DataContract.NOVEL_OBJECT_NAME);
            mNovelChapterList = savedInstanceState.getParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME);
            mChapterListAdapter.notifyDataSetChanged();
            // Restore last state for checked position.
            index = savedInstanceState.getInt("listview_index", -1);
            top = savedInstanceState.getInt("listview_top", 0);
        }
        if(mNovelChapterList.size() == 0) {
            mListView.getSwipeToRefresh().measure(1,1);
            mListView.getSwipeToRefresh().setRefreshing(true);
            getPresenter().loadChapters(mNovel);
        }
        if(index != -1) {
            mListView.getList().setSelectionFromTop(index, top);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getPresenter().checkLastReadState(mNovel.getId());
        getPresenter().loadChapters(mNovel);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_chapterlist, menu);
        mMenuItemLastRead = menu.findItem(R.id.menu_item_last_read_history);
        getPresenter().checkLastReadState(mNovel.getId());
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.menu_item_last_read_history:
                gotoLastRead();
                return true;
            case R.id.menu_item_chapters_refresh:
                refreshChapters();
                return true;
            case R.id.menu_item_chapters_detail:
                getPresenter().showNovelIntroduction(mNovel);
                return true;
            case R.id.menu_item_change_theme:
                setNightMode(!isNightMode());
                return true;
            case R.id.menu_item_chapters_offline_cache:
                chaptersOfflineCache();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(DataContract.NOVEL_OBJECT_NAME, mNovel);
        outState.putParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME, mNovelChapterList);

        int index = mListView.getList().getFirstVisiblePosition();
        View v = mListView.getList().getChildAt(0);
        int top = (v == null) ? 0 : v.getTop();

        outState.putInt("listview_index", index);
        outState.putInt("listview_top", top);

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        //mNovel = savedInstanceState.getParcelable(DataContract.NOVEL_OBJECT_NAME);
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void showChapterList(List<NovelChapterModel> chapterList) {
        mNovelChapterList.clear();
        mNovelChapterList.addAll(chapterList);
        mChapterListAdapter.notifyDataSetChanged();
    }

    @Override
    public void canGoToLastRead(Boolean value) {
        if(mMenuItemLastRead != null)
            mMenuItemLastRead.setVisible(value);
    }

    @Override
    public void setLoading(Boolean isLoading) {
        if(isLoading) {
            mListView.showMoreProgress();
            mListView.getSwipeToRefresh().setRefreshing(true);
        } else {
            mListView.hideMoreProgress();
            mListView.getSwipeToRefresh().setRefreshing(false);
        }
    }

    @Override
    public Boolean isLoading() {
        return mListView.getSwipeToRefresh().isRefreshing() || mListView.isLoadingMore();
    }

    @Override
    public void showToast(String text, ToastType toastType) {
        ToastProxy.showToast(this, text, toastType);
    }

    public NovelChaptersPresenter getPresenter() {
        return mPresenter;
    }

    private void gotoLastRead() {
        if(mNovel != null && mNovelChapterList != null)
            getPresenter().readLastPosition(mNovel, mNovelChapterList);
        else
            ToastProxy.showToast(this, getString(R.string.toast_chapter_list_loading), ToastType.TOAST_INFO);
    }

    private void refreshChapters() {
        mListView.getSwipeToRefresh().setRefreshing(true);
        getPresenter().loadChapters(mNovel, true);
    }

    boolean isCaching = false;
    NotificationCompat.Builder mBuilder;
    private void chaptersOfflineCache() {

        if(isCaching) return;
        int chapterCount = mNovelChapterList.size();
        final int[] resultCount = {0, 0};
        NotificationManager mNotifyManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(this);
        mBuilder.setContentTitle("缓存章节")
                .setContentText("")
                .setSmallIcon(R.drawable.ic_action_go_bottom);
        mBuilder.setProgress(chapterCount, 0, false);
        int id = 110;
        isCaching = true;
        getPresenter().preloadChapterContents(mNovel, mNovelChapterList).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            // onNext
                            if(result)
                                resultCount[0]++;
                            else
                                resultCount[1]++;
                            mBuilder.setProgress(chapterCount, resultCount[0] + resultCount[1], false);
                            mBuilder.setContentText(getResources().getString(R.string.novel_chapter_contents_cache_progress, resultCount[0] + resultCount[1], mNovelChapterList.size()));
                            mNotifyManager.notify(id, mBuilder.build());
                        },
                        error -> {
                            // onError
                            mBuilder.setContentText("Download failed")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            ToastProxy.showToast(this, getString(R.string.toast_generic_error), ToastType.TOAST_INFO);
                            mNotifyManager.notify(id, mBuilder.build());
                            isCaching = false;
                        },
                        () -> {
                            // onComplete
                            mBuilder.setContentText("Download failed")
                                    // Removes the progress bar
                                    .setProgress(0,0,false);
                            ToastProxy.showToast(this, getResources().getString(R.string.toast_chapter_contents_cache, resultCount[0], resultCount[1]), ToastType.TOAST_INFO);
                            mNotifyManager.notify(id, mBuilder.build());
                            isCaching = false;
                        }
                );
    }
}
