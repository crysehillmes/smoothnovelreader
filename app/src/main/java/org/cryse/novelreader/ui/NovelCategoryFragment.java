package org.cryse.novelreader.ui;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.ui.adapter.NovelCategoryItemAdapter;
import org.cryse.novelreader.ui.adapter.item.NovelCategoryItem;
import org.cryse.novelreader.ui.adapter.item.NovelCategoryItemGroup;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class NovelCategoryFragment extends AbstractFragment {
    private View mContentView;

    @InjectView(R.id.category_group_viewpager)
    ViewPager mViewPager;
    @InjectView(R.id.tabs)
    PagerSlidingTabStrip mPagerTabStrip;

    CategoryGroupPagerAdapter mCategoryAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContentView = inflater.inflate(R.layout.fragment_category_list,null);
        ButterKnife.inject(this, mContentView);
        UIUtils.setInsets(getActivity(), mContentView, true, Build.VERSION.SDK_INT < 21);
        //UIUtils.setInsets(getActivity(), mViewPager, true, Build.VERSION.SDK_INT < 21);
        initViewPager();
        return mContentView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Activity activity = getActivity();
        if(activity instanceof MainActivity) {
            ((MainActivity)activity).setToolbarTitleFromFragment(getString(R.string.drawer_category));
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("group_items")) {
            List<NovelCategoryItemGroup> items = savedInstanceState.getParcelableArrayList("group_items");
            mCategoryAdapter.addAll(items);
            mCategoryAdapter.notifyDataSetChanged();
        } else {
            loadViewPagerData();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mCategoryAdapter != null && mCategoryAdapter.getItems() != null)
            outState.putParcelableArrayList("group_items", mCategoryAdapter.getItems());
    }

    @Override
    public void onResume() {
        super.onResume();
        ViewCompat.setElevation(getThemedActivity().getToolbar(), 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        ViewCompat.setElevation(getThemedActivity().getToolbar(),
                getResources().getDimensionPixelSize(R.dimen.toolbar_elevation));
    }

    public void initViewPager() {
        mCategoryAdapter = new CategoryGroupPagerAdapter(getChildFragmentManager());
        mViewPager.setAdapter(mCategoryAdapter);
        mPagerTabStrip.setViewPager(mViewPager);
    }

    public void loadViewPagerData() {
        NovelCategoryItemGroup traditionalCategoryGroup = new NovelCategoryItemGroup(
                NovelCategoryItemGroup.TYPE_TRADITIONAL_CATEGORY,
                getString(R.string.novel_categorys),
                new ArrayList<NovelCategoryItem>());
        String[] categoryTitles = getResources().getStringArray(R.array.novel_categorys_array);
        String[] categoryValues = getResources().getStringArray(R.array.novel_categorys_array);
        for(int i = 0; i < categoryTitles.length; i++) {
            traditionalCategoryGroup.getItems().add(
                    new NovelCategoryItem(
                            categoryTitles[i],
                            categoryValues[i]
                    )
            );
        }
        mCategoryAdapter.addGroup(traditionalCategoryGroup);

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_leadrole),
                getResources().getStringArray(R.array.novel_category_by_leadrole_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_leading_charater),
                getResources().getStringArray(R.array.novel_category_by_leading_charater_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_leading_relationship),
                getResources().getStringArray(R.array.novel_category_by_leading_relationship_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_class),
                getResources().getStringArray(R.array.novel_category_by_class_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_background),
                getResources().getStringArray(R.array.novel_category_by_background_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_storyline),
                getResources().getStringArray(R.array.novel_category_by_storyline_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_writing_style),
                getResources().getStringArray(R.array.novel_category_by_writing_style_array)
        );

        addTagsGroupToAdapter(
                getString(R.string.novel_category_by_word_count),
                getResources().getStringArray(R.array.novel_category_by_word_count_array)
        );
        mCategoryAdapter.notifyDataSetChanged();
    }

    private void addTagsGroupToAdapter(String tagHeader, String[] tags) {
        NovelCategoryItemGroup itemGroup = new NovelCategoryItemGroup(
                NovelCategoryItemGroup.TYPE_TAG_CATEGORY,
                tagHeader,
                new ArrayList<NovelCategoryItem>());
        for(int i = 0; i < tags.length; i++) {
            itemGroup.getItems().add(
                    new NovelCategoryItem(
                            tags[i],
                            tags[i]
                    )
            );
        }
        mCategoryAdapter.addGroup(itemGroup);
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

    public static class CategoryGroupPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<NovelCategoryItemGroup> mItemGroups;

        public CategoryGroupPagerAdapter(FragmentManager fm) {
            super(fm);
            mItemGroups = new ArrayList<NovelCategoryItemGroup>();
        }

        public void addGroup(NovelCategoryItemGroup itemGroup) {
            mItemGroups.add(itemGroup);
        }

        public void addAll(List<NovelCategoryItemGroup> itemGroups) {
            mItemGroups.addAll(itemGroups);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mItemGroups.get(position).getGroupName();
        }

        @Override
        public int getCount() {
            return mItemGroups.size();
        }

        @Override
        public Fragment getItem(int position) {
            return CategorySubListFragment.newInstance(
                    position,
                    mItemGroups.get(position)
            );
        }

        public ArrayList<NovelCategoryItemGroup> getItems() {
            return mItemGroups;
        }
    }

    public static class CategorySubListFragment extends Fragment {
        private int mPosition;
        private NovelCategoryItemGroup mItemGroup;

        @Inject
        AndroidDisplay mDisplay;

        public static CategorySubListFragment newInstance(int position, NovelCategoryItemGroup itemGroup) {
            CategorySubListFragment fragment = new CategorySubListFragment();
            Bundle args = new Bundle();
            args.putInt("position", position);
            args.putParcelable("item_group", itemGroup);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            ((SmoothReaderApplication)getActivity().getApplication()).inject(this);
            Bundle args = getArguments();
            if(savedInstanceState != null) {
                mPosition = savedInstanceState.getInt("position");
                mItemGroup = savedInstanceState.getParcelable("item_group");
            } else if(args != null) {
                mPosition = args.getInt("position");
                mItemGroup = args.getParcelable("item_group");
            }
        }


        @Nullable
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View view = inflater.inflate(R.layout.layout_category_list_recyclerview,null);
            RecyclerView recyclerView = (RecyclerView)view;
            recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), getResources().getInteger(R.integer.category_tag_list_col)));
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            NovelCategoryItemAdapter adapter = new NovelCategoryItemAdapter(getActivity());
            recyclerView.setAdapter(adapter);
            adapter.addAll(mItemGroup.getItems());

            adapter.setOnItemClickListener((view1, position, id) -> {
                NovelCategoryItem item = adapter.getItem(position);
                switch (mItemGroup.getGroupType()) {
                    case NovelCategoryItemGroup.TYPE_TRADITIONAL_CATEGORY:
                        mDisplay.showCategoryFragment(
                                item.getTitle(),
                                item.getValue(),
                                "",
                                false
                        );
                        break;
                    case NovelCategoryItemGroup.TYPE_TAG_CATEGORY:
                        String categoryHeaderTitle = mItemGroup.getGroupName();
                        mDisplay.showCategoryFragment(
                                item.getTitle(),
                                categoryHeaderTitle,
                                item.getValue(),
                                true
                        );
                        break;
                }
            });
            return view;
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            outState.putInt("position", mPosition);
            outState.putParcelable("item_group", mItemGroup);
        }
    }
}
