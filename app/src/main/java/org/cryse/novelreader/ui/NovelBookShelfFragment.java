package org.cryse.novelreader.ui;

import android.animation.Animator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognizerIntent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.apache.tika.detect.DefaultDetector;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MimeTypes;
import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.LoadLocalFileDoneEvent;
import org.cryse.novelreader.event.LoadLocalFileStartEvent;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.service.ChapterContentsCacheService;
import org.cryse.novelreader.service.LoadLocalTextService;
import org.cryse.novelreader.ui.adapter.NovelBookShelfListAdapter;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.LayerEnablingAnimatorListener;
import org.cryse.novelreader.util.PathUriUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.view.NovelBookShelfView;
import org.cryse.widget.persistentsearch.DefaultVoiceRecognizerDelegate;
import org.cryse.widget.persistentsearch.PersistentSearchView;
import org.cryse.widget.persistentsearch.VoiceRecognitionDelegate;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class NovelBookShelfFragment extends AbstractFragment implements NovelBookShelfView {
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 1023;
    private static final String LOG_TAG = NovelBookShelfFragment.class.getName();
    private static final int OPEN_TEXT_FILE_RESULT_CODE = 10010;
    private static final String SEARCH_FRAGMENT_TAG = "search_fragment_tag";
    private static final Detector DETECTOR = new DefaultDetector(
            MimeTypes.getDefaultMimeTypes());

    @Inject
    NovelBookShelfPresenter mPresenter;

    ArrayList<NovelModel> mNovelList;

    NovelBookShelfListAdapter mCollectionAdapter;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.searchview)
    PersistentSearchView mSearchView;
    @Bind(R.id.view_search_tint)
    View mSearchTintView;
    @Bind(R.id.search_fragment_container)
    FrameLayout mSearchContainer;

    @Bind(R.id.novel_listview)
    SuperRecyclerView mCollectionView;

    @Bind(R.id.empty_view_text_prompt)
    TextView mEmptyViewText;

    ServiceConnection mBackgroundServiceConnection;
    MaterialDialog mAddLocalFileProgressDialog = null;
    private MenuItem mSearchMenuItem;
    private ChapterContentsCacheService.ChapterContentsCacheBinder mServiceBinder;

    public static NovelBookShelfFragment newInstance(Bundle args) {
        NovelBookShelfFragment fragment = new NovelBookShelfFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static String detectMimeType(final String filePath) {
        TikaInputStream tikaIS = null;
        try {
            File targetFile = new File(filePath);
            tikaIS = TikaInputStream.get(targetFile);

        /*
         * You might not want to provide the file's name. If you provide an Excel
         * document with a .xls extension, it will get it correct right away; but
         * if you provide an Excel document with .doc extension, it will guess it
         * to be a Word document
         */
            final Metadata metadata = new Metadata();
            metadata.set(Metadata.RESOURCE_NAME_KEY, targetFile.getName());

            return DETECTOR.detect(tikaIS, metadata).toString();
        } catch (IOException ex) {
            return "UNKNOWN";
        } finally {
            if (tikaIS != null) {
                try {
                    tikaIS.close();
                } catch (IOException e) {
                    Timber.d(e, e.getMessage(), LOG_TAG);
                }
            }
        }
    }

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
        ButterKnife.bind(this, contentView);
        getThemedActivity().setSupportActionBar(mToolbar);
        final ActionBar actionBar = getThemedActivity().getSupportActionBar();
        if (actionBar != null) {
            actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        mToolbar.setBackgroundColor(getPrimaryColor());
        mEmptyViewText.setText(getActivity().getString(R.string.empty_view_no_book_on_shelf_prompt));
        setUpSearchView();
        initListView();
        UIUtils.setInsets(getActivity(), mCollectionView, false, false, true, Build.VERSION.SDK_INT < 21);
        mCollectionView.setClipToPadding(false);
        return contentView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        mNovelList = new ArrayList<NovelModel>();
        mCollectionAdapter = new NovelBookShelfListAdapter(getActivity(), mNovelList);
        int columnCount = getResources().getInteger(R.integer.boolshelf_list_col);
        RecyclerView.LayoutManager layoutManager;
        if (columnCount == 1) {
            layoutManager = new LinearLayoutManager(getContext());
        } else {
            layoutManager = new GridLayoutManager(getContext(), columnCount);
        }
        mCollectionView.setLayoutManager(layoutManager);
        mCollectionView.getRecyclerView().setHasFixedSize(true);
        mCollectionView.setAdapter(mCollectionAdapter);
        mCollectionView.getSwipeToRefresh().setColorSchemeResources(
                ColorUtils.getRefreshProgressBarColors()[0],
                ColorUtils.getRefreshProgressBarColors()[1],
                ColorUtils.getRefreshProgressBarColors()[2],
                ColorUtils.getRefreshProgressBarColors()[3]
        );
        mCollectionView.setRefreshListener(() -> {
            if (mCollectionAdapter.getItemCount() > 0)
                mPresenter.getNovelUpdates();
            else
                mCollectionView.getSwipeToRefresh().setRefreshing(false);
        });
        mCollectionAdapter.setOnItemClickListener((view, position, id) -> {
            if (!mCollectionAdapter.isAllowSelection())
                mPresenter.showNovelChapterList(mNovelList.get(position));
            else {
                mCollectionAdapter.toggleSelection(position);
                if (getActionMode() != null) {
                    getActionMode().setTitle(getString(R.string.cab_selection_count, mCollectionAdapter.getSelectedItemCount()));
                    if (mCollectionAdapter.getSelectedItemCount() == 0)
                        getActionMode().finish();
                }
            }
        });

        mCollectionAdapter.setOnItemLongClickListener((view, position, id) -> {
            if (!mCollectionAdapter.isAllowSelection()) {
                setActionMode(getAppCompatActivity().startSupportActionMode(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        mCollectionAdapter.setAllowSelection(true);
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
                        mCollectionAdapter.clearSelections();
                        mCollectionAdapter.setAllowSelection(false);
                        setActionMode(null);
                    }
                }));
                mCollectionAdapter.toggleSelection(position);
                getActionMode().setTitle(getString(R.string.cab_selection_count, mCollectionAdapter.getSelectedItemCount()));
            } else {
                if (getActionMode() != null) {
                    mCollectionAdapter.toggleSelection(position);
                    getActionMode().setTitle(getString(R.string.cab_selection_count, mCollectionAdapter.getSelectedItemCount()));
                    if (mCollectionAdapter.getSelectedItemCount() == 0)
                        getActionMode().finish();
                }
            }
            return true;
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mCollectionView.getSwipeToRefresh().setRefreshing(true);
        mPresenter.loadFavoriteNovels();
        Activity activity = getActivity();
        if (activity instanceof MainActivity) {
            ((MainActivity) activity).onSectionAttached(getString(R.string.drawer_bookshelf));
        }
        if (getView() != null) {
            getView().setFocusableInTouchMode(true);
            getView().requestFocus();
            getView().setOnKeyListener(new View.OnKeyListener() {
                @Override
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        //Toast.makeText(getContext(), "onBackPressed", Toast.LENGTH_SHORT).show();
                        if (mSearchView.isSearching()) {
                            mSearchView.closeSearch();
                            return true;
                        }
                        return false;
                    }
                    return false;
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mPresenter.bindView(this);
        Intent service = new Intent(getActivity().getApplicationContext(), ChapterContentsCacheService.class);
        getActivity().bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        mPresenter.unbindView();
        getActivity().unbindService(mBackgroundServiceConnection);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.loadFavoriteNovels();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.destroy();
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
        mSearchMenuItem = menu.findItem(R.id.menu_item_add_online_book);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).getNavigationDrawer().openDrawer();
                    return true;
                } else {
                    return false;
                }
            case R.id.menu_item_change_theme:
                getThemedActivity().setNightMode(!isNightMode());
                return true;
            case R.id.menu_item_add_online_book:
                if (mSearchMenuItem != null) {
                    View menuItemView = getView().findViewById(R.id.menu_item_add_online_book);
                    mSearchView.setStartPositionFromMenuItem(menuItemView, getView().getMeasuredWidth());
                    mSearchView.openSearch();
                    return true;
                } else {
                    return false;
                }
                /*mPresenter.goSearch();
                return true;*/
            case R.id.menu_item_add_local_text_book:
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("text/plain");
                startActivityForResult(intent, OPEN_TEXT_FILE_RESULT_CODE);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setUpSearchView() {
        VoiceRecognitionDelegate delegate = new DefaultVoiceRecognizerDelegate(this, VOICE_RECOGNITION_REQUEST_CODE);
        if (delegate.isVoiceRecognitionAvailable()) {
            mSearchView.setVoiceRecognitionDelegate(delegate);
        }
        mSearchTintView.setOnClickListener(v -> mSearchView.cancelEditing());
        mSearchView.setSearchListener(new PersistentSearchView.SearchListener() {

            @Override
            public void onSearchEditOpened() {
                //Use this to tint the screen
                mSearchTintView.setVisibility(View.VISIBLE);
                mSearchTintView
                        .animate()
                        .alpha(1.0f)
                        .setDuration(300)
                        .setListener(new LayerEnablingAnimatorListener(mSearchTintView))
                        .start();

            }

            @Override
            public void onSearchEditClosed() {
                mSearchTintView
                        .animate()
                        .alpha(0.0f)
                        .setDuration(300)
                        .setListener(new LayerEnablingAnimatorListener(mSearchTintView) {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                mSearchTintView.setVisibility(View.GONE);
                            }
                        })
                        .start();
            }

            @Override
            public boolean onSearchEditBackPressed() {
                if (mSearchView.isEditing()) {
                    mSearchView.cancelEditing();
                    return true;
                }
                return false;
            }

            @Override
            public void onSearchExit() {
                SearchFragment searchFragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                if (searchFragment != null) {
                    slideOutToButtom(mSearchContainer, true, () -> {
                        //searchFragment::clearSearch
                        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                        fragmentTransaction.remove(searchFragment);
                        fragmentTransaction.commit();
                    });
                } else {
                    slideOutToButtom(mSearchContainer, true, null);
                }
            }

            @Override
            public void onSearchTermChanged(String term) {

            }

            @Override
            public void onSearch(String string) {
                SearchFragment searchFragment = (SearchFragment) getChildFragmentManager().findFragmentByTag(SEARCH_FRAGMENT_TAG);
                if (searchFragment == null) {

                    searchFragment = SearchFragment.newInstance("", (novelModel, position) -> {
                        mPresenter.showNovelDetail(novelModel);
                    });
                    FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                    fragmentTransaction.add(R.id.search_fragment_container, searchFragment, SEARCH_FRAGMENT_TAG);
                    fragmentTransaction.commit();
                }
                if (!mSearchContainer.isShown()) {
                    final SearchFragment finalSearchFragment = searchFragment;
                    slideInToTop(mSearchContainer, true, () -> {
                        finalSearchFragment.search(string);
                    });
                } else {
                    searchFragment.search(string);
                }

            }

            @Override
            public void onSearchCleared() {

            }

        });
    }

    @Override
    public void showBooksOnShelf(List<NovelModel> books) {
        mNovelList.clear();
        mNovelList.addAll(books);
        mCollectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void showAddLocalBookProgressDialog(boolean show) {
        if (show) {
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
        if (isLoading) {
            mCollectionView.showMoreProgress();
            mCollectionView.getSwipeToRefresh().setRefreshing(true);
        } else {
            mCollectionView.hideMoreProgress();
            mCollectionView.getSwipeToRefresh().setRefreshing(false);
        }
    }

    @Override
    public Boolean isLoading() {
        return mCollectionView.getSwipeToRefresh().isRefreshing() || mCollectionView.isLoadingMore();
    }

    private void removeNovels() {
        int count = mCollectionAdapter.getSelectedItemCount();
        List<String> removeIds = new ArrayList<String>();
        String currentCachingNovelId = null;
        int[] reverseSortedPositions = mCollectionAdapter.getSelectedItemReversePositions();
        if (mServiceBinder != null && mServiceBinder.getCurrentCachingNovelId() != null) {
            currentCachingNovelId = mServiceBinder.getCurrentCachingNovelId();
        }
        for (int position : reverseSortedPositions) {
            if (currentCachingNovelId != null && currentCachingNovelId.compareTo(mNovelList.get(position).getNovelId()) == 0) {
                showSnackbar(getString(R.string.toast_chapter_contents_caching_cannot_delete, mNovelList.get(position).getTitle()), SimpleSnackbarType.WARNING);
            } else {
                removeIds.add(mNovelList.get(position).getNovelId());
                if (mServiceBinder != null) {
                    if (mServiceBinder.removeFromQueueIfExist(mNovelList.get(position).getNovelId())) {
                        showSnackbar(getString(R.string.notification_action_chapter_contents_cancel_novel, mNovelList.get(position).getTitle()), SimpleSnackbarType.INFO);
                    }
                }
                ((NovelBookShelfListAdapter) mCollectionView.getAdapter()).remove(position);
            }
        }
        mPresenter.removeFromFavorite(removeIds.toArray(new String[removeIds.size()]));
        mCollectionAdapter.clearSelections();
        mCollectionAdapter.notifyDataSetChanged();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPEN_TEXT_FILE_RESULT_CODE && resultCode == Activity.RESULT_OK && data != null) {
            String filePath = PathUriUtils.getPath(getActivity(), data.getData());
            /*mPresenter.addLocalTextFile(filePath, null);
            showAddLocalBookProgressDialog(true);*/
            readLocalTextFile(filePath, null);
        } else if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            ArrayList<String> matches = data
                    .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            mSearchView.populateEditText(matches);
        }
    }

    public void readLocalTextFile(String textFilePath, String customTitle) {
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            LoadLocalTextService.ReadLocalTextFileBinder binder = activity.getReadLocalTextFileBinder();
            String mime = detectMimeType(textFilePath);
            if (MimeTypes.PLAIN_TEXT.equalsIgnoreCase(mime)) {
                binder.addToCacheQueue(textFilePath, customTitle);
                showSnackbar(getString(R.string.toast_read_local_file_background), SimpleSnackbarType.INFO);
            } else {
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.dialog_error_mime_title)
                        .content(R.string.dialog_error_mime_content)
                        .positiveText(android.R.string.ok).build().show();
            }
        }

    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof LoadLocalFileDoneEvent) {
            mPresenter.loadFavoriteNovels();
        } else if (event instanceof LoadLocalFileStartEvent) {
            mPresenter.loadFavoriteNovels();
        }
    }

    private void slideInToTop(View v, boolean animated, Runnable runOnAnimationEnd) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        v.setTranslationY(metrics.heightPixels);
        v.setAlpha(0);
        if (!v.isShown())
            v.setVisibility(View.VISIBLE);
        v.animate().
                translationY(0).
                alpha(1).
                setDuration(animated ? 500 : 0).
                setInterpolator(new AccelerateDecelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(v) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }
                });
    }

    private void slideOutToButtom(View v, boolean animated, Runnable runOnAnimationEnd) {
        DisplayMetrics metrics = getResources().getDisplayMetrics();
        v.setTranslationY(0);
        v.setAlpha(1);
        v.animate().
                translationY(metrics.heightPixels).
                alpha(0).
                setDuration(animated ? 300 : 0).
                setInterpolator(new AccelerateInterpolator())
                .setListener(new LayerEnablingAnimatorListener(v) {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        v.setVisibility(View.INVISIBLE);
                        if (runOnAnimationEnd != null)
                            runOnAnimationEnd.run();
                    }
                });
    }
}
