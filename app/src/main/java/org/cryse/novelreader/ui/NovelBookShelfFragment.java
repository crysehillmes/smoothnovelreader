package org.cryse.novelreader.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.widget.recyclerview.ItemClickSupport;
import org.cryse.widget.recyclerview.SuperRecyclerView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.ui.adapter.NovelBookShelfListAdapter;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.view.NovelBookShelfView;
import org.cryse.novelreader.util.ToastProxy;
import org.cryse.novelreader.util.ToastType;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelBookShelfFragment extends AbstractFragment implements NovelBookShelfView{
    @Inject
    NovelBookShelfPresenter presenter;

    ArrayList<NovelModel> novelList;

    NovelBookShelfListAdapter bookShelfListAdapter;

    @InjectView(R.id.novel_listview)
    SuperRecyclerView mShelfListView;

    @InjectView(R.id.empty_view_text_prompt)
    TextView mEmptyViewText;

    boolean mIsSearchViewOpen = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_bookshelf, null);
        ButterKnife.inject(this, contentView);
        mEmptyViewText.setText(getActivity().getString(R.string.empty_view_no_book_on_shelf_prompt));
        initListView();
        UIUtils.setInsets(getActivity(), mShelfListView, true, Build.VERSION.SDK_INT < 21);
        mShelfListView.setClipToPadding(false);
        return contentView;
    }

    @SuppressLint("ResourceAsColor")
    private void initListView() {
        novelList = new ArrayList<NovelModel>();
        bookShelfListAdapter = new NovelBookShelfListAdapter(getActivity(), novelList);
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
        mShelfListView.setOnItemClickListener((parent, view, position, id) -> {
            if(!bookShelfListAdapter.isAllowSelection())
                getPresenter().showNovelChapterList(novelList.get(position));
            else {
                bookShelfListAdapter.toggleSelection(position);
                if(getActionMode() != null) {
                    getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
                    if(bookShelfListAdapter.getSelectedItemCount() == 0)
                        getActionMode().finish();
                }
            }
        });

        mShelfListView.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView parent, View view, int position, long id) {
                if (!bookShelfListAdapter.isAllowSelection()) {
                    ActionMode actionMode = getActionBarActivity().getSupportActionBar().startActionMode(new ActionMode.Callback() {
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
                            setActionMode(null);
                        }
                    });
                    setActionMode(actionMode);
                    bookShelfListAdapter.toggleSelection(position);
                    getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
                } else {
                    if(getActionMode() != null) {
                        bookShelfListAdapter.toggleSelection(position);
                        getActionMode().setTitle(getString(R.string.cab_selection_count, bookShelfListAdapter.getSelectedItemCount()));
                        if(bookShelfListAdapter.getSelectedItemCount() == 0)
                            getActionMode().finish();
                    }
                }
                return true;
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mShelfListView.getSwipeToRefresh().setRefreshing(true);
        getPresenter().getFavoritedNovels();
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity)activity).setToolbarTitleFromFragment(getString(R.string.drawer_bookshelf));
        }
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
        getPresenter().getFavoritedNovels();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
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
                getPresenter().goSearch();
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
    public void showToast(String text, ToastType toastType) {
        ToastProxy.showToast(getActivity(), text, toastType);
    }

    private void removeNovels() {
        int count = bookShelfListAdapter.getSelectedItemCount();
        String[] novelIds = bookShelfListAdapter.getSelectedItemIds();
        int index = 0;

        int[] reverseSortedPositions = bookShelfListAdapter.getSelectedItemReversePositions();
        for (int position : reverseSortedPositions) {
            novelIds[index] = novelList.get(position).getId();
            ((NovelBookShelfListAdapter) mShelfListView.getAdapter()).remove(position);
            index++;
        }
        getPresenter().removeFromFavorite(novelIds);
        bookShelfListAdapter.clearSelections();
        bookShelfListAdapter.notifyDataSetChanged();
    }

    public NovelBookShelfPresenter getPresenter() {
        return presenter;
    }
}
