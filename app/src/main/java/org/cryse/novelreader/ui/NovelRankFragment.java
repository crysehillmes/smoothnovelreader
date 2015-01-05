package org.cryse.novelreader.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import org.cryse.novelreader.ui.adapter.NovelCategoryItemAdapter;
import org.cryse.novelreader.ui.adapter.item.NovelCategoryItem;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.UIUtils;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelRankFragment extends AbstractFragment {

    protected View mContentView;
    @Inject
    AndroidDisplay mDisplay;

    @InjectView(R.id.rank_recyclerview)
    RecyclerView mRecyclerView;

    protected NovelCategoryItemAdapter mNovelListAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_rank_list,null);
        ButterKnife.inject(this, mContentView);
        initListView();
        mRecyclerView.setClipToPadding(false);
        UIUtils.setInsets(getActivity(), mRecyclerView, true, Build.VERSION.SDK_INT < 21);
        return mContentView;
    }

    private void initListView() {
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.rank_tag_list_col)));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mNovelListAdapter = new NovelCategoryItemAdapter(getActivity());
        mRecyclerView.setAdapter(mNovelListAdapter);
        String[] rankTitles = getResources().getStringArray(R.array.novel_rank_categorys_array);
        String[] rankValues = getResources().getStringArray(R.array.novel_rank_category_values_array);
        int[] iconIds = new int[]{
                R.drawable.ic_grid_whatshot,
                R.drawable.ic_grid_writing,
                R.drawable.ic_grid_book_finished,
                R.drawable.ic_grid_trending_up,
                R.drawable.ic_grid_male,
                R.drawable.ic_grid_female,
                0,
                0,
                0,
                0,
                0
        };
        for(int i = 0; i < rankTitles.length; i++) {
            mNovelListAdapter.add(
                    new NovelCategoryItem(
                            rankTitles[i],
                            rankValues[i],
                            iconIds[i]
                    )
            );
        }
        mNovelListAdapter.setOnItemClickListener((view, position, id) -> {
            NovelCategoryItem item = mNovelListAdapter.getItem(position);
            mDisplay.showRankFragment(
                    item.getTitle(),
                    item.getValue()
            );
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity)activity).setToolbarTitleFromFragment(getString(R.string.drawer_rank));
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
}
