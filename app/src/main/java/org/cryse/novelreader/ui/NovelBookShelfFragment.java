package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v7.view.ActionMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.LoadLocalFileDoneEvent;
import org.cryse.novelreader.event.LoadLocalFileStartEvent;
import org.cryse.novelreader.service.ChapterContentsCacheService;
import org.cryse.novelreader.service.LoadLocalTextService;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.PathUriUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.ui.adapter.NovelBookShelfListAdapter;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.view.NovelBookShelfView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelBookShelfFragment extends AbstractFragment implements NovelBookShelfView {
    private static final String LOG_TAG = NovelBookShelfFragment.class.getName();
    @Inject
    NovelBookShelfPresenter presenter;

    ArrayList<NovelModel> novelList;

    NovelBookShelfListAdapter bookShelfListAdapter;

    @InjectView(R.id.novel_listview)
    SuperRecyclerView mShelfListView;

    @InjectView(R.id.empty_view_text_prompt)
    TextView mEmptyViewText;

    ServiceConnection mBackgroundServiceConnection;
    private ChapterContentsCacheService.ChapterContentsCacheBinder mServiceBinder;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        mBackgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (ChapterContentsCacheService.ChapterContentsCacheBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceBinder = null;
            }
        };
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(getActivity()).inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_bookshelf, null);
        ButterKnife.inject(this, contentView);
        mEmptyViewText.setText(getActivity().getString(R.string.empty_view_no_book_on_shelf_prompt));
        initListView();
        UIUtils.setInsets(getActivity(), mShelfListView, false, false, true, Build.VERSION.SDK_INT < 21);
        mShelfListView.setClipToPadding(false);
        return contentView;
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        novelList = new ArrayList<NovelModel>();
        bookShelfListAdapter = new NovelBookShelfListAdapter(getActivity(), novelList);
        mShelfListView.getList().setHasFixedSize(true);
        mShelfListView.setAdapter(bookShelfListAdapter);
        mShelfListView.getSwipeToRefresh().setColorSchemeResources(
                ColorUtils.getRefreshProgressBarColors()[0],
                ColorUtils.getRefreshProgressBarColors()[1],
                ColorUtils.getRefreshProgressBarColors()[2],
                ColorUtils.getRefreshProgressBarColors()[3]
        );
        mShelfListView.setRefreshListener(() -> {
            if (bookShelfListAdapter.getItemCount() > 0)
                getPresenter().getNovelUpdates();
            else
                mShelfListView.getSwipeToRefresh().setRefreshing(false);
        });
        mShelfListView.setOnItemClickListener((view, position, id) -> {
            if (!bookShelfListAdapter.isAllowSelection())
                getPresenter().showNovelChapterList(novelList.get(position));
            else {
                bookShelfListAdapter.toggleSelection(position);
                if (getActionMode() != null) {
                    getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
                    if (bookShelfListAdapter.getSelectedItemCount() == 0)
                        getActionMode().finish();
                }
            }
        });

        mShelfListView.setOnItemLongClickListener((view, position, id) -> {
            if (!bookShelfListAdapter.isAllowSelection()) {
                setActionMode(getAppCompatActivity().startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        bookShelfListAdapter.setAllowSelection(true);
                        MenuInflater inflater = actionMode.getMenuInflater();
                        inflater.inflate(R.menu.menu_bookshelf_cab, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.menu_bookshelf_cab_delete:
                                removeNovels();
                                actionMode.finish();
                                return true;
                        }
                        return false;
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode actionMode) {
                        bookShelfListAdapter.clearSelections();
                        bookShelfListAdapter.setAllowSelection(false);
                        Log.d("AAA","AAAAAA");
                        setActionMode(null);
                    }
                }));
                bookShelfListAdapter.toggleSelection(position);
                getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
            } else {
                if (getActionMode() != null) {
                    bookShelfListAdapter.toggleSelection(position);
                    getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
                    if (bookShelfListAdapter.getSelectedItemCount() == 0)
                        getActionMode().finish();
                }
            }
            return true;
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShelfListView.getSwipeToRefresh().setRefreshing(true);
        getPresenter().loadFavoriteNovels();
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity)activity).onSectionAttached(getString(R.string.drawer_bookshelf));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().bindView(this);
        Intent service = new Intent(getActivity().getApplicationContext(), ChapterContentsCacheService.class);
        getActivity().bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().unbindView();
        getActivity().unbindService(mBackgroundServiceConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().loadFavoriteNovels();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_bookshelf, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_change_theme:
                getThemedActivity().setNightMode(!isNightMode());
                return true;
            case R.id.menu_item_add_book:
                showAddBookDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void showBooksOnShelf(List<NovelModel> books) {
        novelList.clear();
        novelList.addAll(books);
        bookShelfListAdapter.notifyDataSetChanged();
    }

    MaterialDialog mAddLocalFileProgressDialog = null;
    @Override
    public void showAddLocalBookProgressDialog(boolean show) {
        if(show) {
            if (mAddLocalFileProgressDialog != null && mAddLocalFileProgressDialog.isShowing())
                mAddLocalFileProgressDialog.dismiss();
            mAddLocalFileProgressDialog = new MaterialDialog.Builder(getActivity())
                    .title("Adding")
                    .content("Please wait...")
                    .progress(true, 0)
                    .show();
        } else {
            if (mAddLocalFileProgressDialog != null && mAddLocalFileProgressDialog.isShowing())
                mAddLocalFileProgressDialog.dismiss();
        }

    }

    @Override
    public void setLoading(Boolean isLoading) {
        if(isLoading) {
            mShelfListView.showMoreProgress();
            mShelfListView.getSwipeToRefresh().setRefreshing(true);
        } else {
            mShelfListView.hideMoreProgress();
            mShelfListView.getSwipeToRefresh().setRefreshing(false);
        }
    }

    @Override
    public Boolean isLoading() {
        return mShelfListView.getSwipeToRefresh().isRefreshing() || mShelfListView.isLoadingMore();
    }

    @Override
    public void showSnackbar(CharSequence text, SimpleSnackbarType snackbarType) {
        Snackbar snackbar = SnackbarUtils.makeSimple(getSnackbarRootView(), text, snackbarType, Snackbar.LENGTH_SHORT);
        snackbar.show();
    }

    private void removeNovels() {
        int count = bookShelfListAdapter.getSelectedItemCount();
        List<String> removeIds = new ArrayList<String>();
        String currentCachingNovelId = null;
        int[] reverseSortedPositions = bookShelfListAdapter.getSelectedItemReversePositions();
        if(mServiceBinder != null && mServiceBinder.getCurrentCachingNovelId() != null) {
            currentCachingNovelId = mServiceBinder.getCurrentCachingNovelId();
        }
        for (int position : reverseSortedPositions) {
            if(currentCachingNovelId != null && currentCachingNovelId.compareTo(novelList.get(position).getId()) == 0) {
                showSnackbar(getString(R.string.toast_chapter_contents_caching_cannot_delete, novelList.get(position).getTitle()), SimpleSnackbarType.WARNING);
            } else {
                removeIds.add(novelList.get(position).getId());
                if(mServiceBinder != null) {
                    if(mServiceBinder.removeFromQueueIfExist(novelList.get(position).getId())) {
                        showSnackbar(getString(R.string.notification_action_chapter_contents_cancel_novel, novelList.get(position).getTitle()), SimpleSnackbarType.INFO);
                }
                }
                ((NovelBookShelfListAdapter) mShelfListView.getAdapter()).remove(position);
            }
        }
        getPresenter().removeFromFavorite(removeIds.toArray(new String[removeIds.size()]));
        bookShelfListAdapter.clearSelections();
        bookShelfListAdapter.notifyDataSetChanged();
    }

    public NovelBookShelfPresenter getPresenter() {
        return presenter;
    }

    protected void showAddBookDialog() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.action_add_book_source_dialog_title)
                .items(R.array.action_add_book_sources)
                .itemsCallback((dialog, view, which, text) -> {
                    if (which == 0) {
                        getPresenter().goSearch();
                    } else {
                        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                        intent.setType("text/plain");
                        startActivityForResult(intent, OPEN_TEXT_FILE_RESULT_CODE);
                    }
                })
                .show();
    }

    private static final int OPEN_TEXT_FILE_RESULT_CODE = 10010;
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_TEXT_FILE_RESULT_CODE && data != null) {
            String filePath = PathUriUtils.getPath(getActivity(), data.getData());
            /*getPresenter().addLocalTextFile(filePath, null);
            showAddLocalBookProgressDialog(true);*/
            readLocalTextFile(filePath, null);
        }
    }

    public void readLocalTextFile(String textFilePath, String customTitle) {
        if(getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            LoadLocalTextService.ReadLocalTextFileBinder binder = activity.getReadLocalTextFileBinder();
            binder.addToCacheQueue(textFilePath, customTitle);
            showSnackbar(getString(R.string.toast_read_local_file_background), SimpleSnackbarType.INFO);
        }

    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof LoadLocalFileDoneEvent) {
            getPresenter().loadFavoriteNovels();
        } else if(event instanceof LoadLocalFileStartEvent) {
            getPresenter().loadFavoriteNovels();
        }
    }
}
