package org.cryse.novelreader.util.navidrawer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.ui.NovelCategoryFragment;
import org.cryse.novelreader.ui.NovelBookShelfFragment;
import org.cryse.novelreader.ui.NovelChapterListActivity;
import org.cryse.novelreader.ui.NovelDetailActivity;
import org.cryse.novelreader.ui.NovelListFragment;
import org.cryse.novelreader.ui.NovelRankFragment;
import org.cryse.novelreader.ui.NovelReadViewActivity;
import org.cryse.novelreader.ui.SearchActivity;
import org.cryse.novelreader.ui.SettingsActivity;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.RunTimeStore;

import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

public class AndroidNavigation {
    private static final String TAG = AndroidNavigation.class.getCanonicalName();
    RunTimeStore mRunTimeStore;

    protected AppCompatActivity mActivity;
    protected FragmentManager mFragmentManager;

    @Inject
    public AndroidNavigation(RunTimeStore runTimeStore) {
        this.mRunTimeStore = runTimeStore;
    }

    public void attachMainActivity(AppCompatActivity activity) {
        mActivity = activity;
        mFragmentManager = mActivity.getSupportFragmentManager();
        SmoothReaderApplication.get(activity).inject(this);
    }

    public void switchContentFragment(String fragmentName, String backStackTag) {
        switchContentFragment(fragmentName, null, backStackTag);
    }

    public void switchContentFragment(String fragmentName, Bundle bundle, String backStackTag) {
        switchContentFragment(Fragment.instantiate(mActivity, fragmentName, bundle), backStackTag);
    }

    public void switchContentFragment(Fragment targetFragment, String backStackTag) {
        if(mActivity == null)
            throw new IllegalStateException("Should attach to MainActivity before call any method.");
        clearBackStack();
        FragmentTransaction fragmentTransaction = mFragmentManager
                .beginTransaction();
        fragmentTransaction.setCustomAnimations(android.R.anim.fade_in,
                android.R.anim.fade_out);
        if(backStackTag != null)
            fragmentTransaction.addToBackStack(backStackTag);
        fragmentTransaction.replace(R.id.container, targetFragment);
        fragmentTransaction.commit();
    }

    public void clearBackStack() {
        for(int i = 0; i < mFragmentManager.getBackStackEntryCount(); ++i) {
            mFragmentManager.popBackStack();
        }
    }

    public boolean popEntireFragmentBackStack() {
        final int backStackCount = mFragmentManager.getBackStackEntryCount();
        // Clear Back Stack
        for (int i = 0; i < backStackCount; i++) {
            mFragmentManager.popBackStack();
        }
        return backStackCount > 0;
    }

    private void startActivity(Context context, Intent intent) {
        context.startActivity(intent);
    }

    public void showNovelDetailView(
            Object currentView,
            NovelModel novelModel,
            boolean showStartRead)
    {
        Activity activity = getActivityFromView(currentView);

        Intent intent = new Intent(activity, NovelDetailActivity.class);
        intent.putExtra(DataContract.NOVEL_OBJECT_NAME, novelModel);
        intent.putExtra(DataContract.NOVEL_INTRODUCTION_CONTRACT_SHOW_START_READING, showStartRead);
        startActivity(activity, intent);
    }

    public void navigateToBookShelfFragment() {
        switchContentFragment(NovelBookShelfFragment.class.getName(), null);
    }

    public void navigateToCategoryListFragment() {
        switchContentFragment(NovelCategoryFragment.class.getName(), null);
    }

    public void navigateToRankFragment() {
        switchContentFragment(NovelRankFragment.class.getName(), null);
    }

    public void navigateToRankFragment(String title, String queryString) {
        NovelListFragment categoryFragment = NovelListFragment.newInstance(
                NovelListFragment.QUERY_TYPE_RANK,
                title,
                queryString,
                null,
                false
        );
        switchContentFragment(categoryFragment, "rank_list");
    }

    public void navigateToCategoryFragment(String title, String queryString, String subQueryString, boolean isQueryByTag) {
        NovelListFragment categoryFragment = NovelListFragment.newInstance(
            NovelListFragment.QUERY_TYPE_CATEGORY,
            title,
            queryString,
            subQueryString,
            isQueryByTag
        );
        switchContentFragment(categoryFragment, "category_list");
    }

    public void showSearchActivity(Object currentView) {
        Context context = getContextFromView(currentView);
        Intent intent = new Intent(context, SearchActivity.class);
        startActivity(context, intent);
    }

    public void navigateToSettingsActivity(Context context) {
        Intent intent = new Intent(context, SettingsActivity.class);
        context.startActivity(intent);
    }

    public void showNovelChapterList(Object currentView, NovelModel novel) {
        Context context = getContextFromView(currentView);
        Intent intent = new Intent(context, NovelChapterListActivity.class);
        Bundle mBundle = new Bundle();
        mBundle.putParcelable(DataContract.NOVEL_OBJECT_NAME, novel);
        intent.putExtras(mBundle);
        startActivity(context, intent);
    }

    public void showNovelReadActivity(Object currentView, NovelModel novelModel, String chapterId, int chapterOffset, List<NovelChapterModel> chapterList) {
        Context context = getContextFromView(currentView);
        Intent intent = new Intent(context, NovelReadViewActivity.class);
        //intent.putParcelableArrayListExtra(DataContract.NOVEL_CHAPTER_LIST_NAME, mNovelChapterList);

        intent.putExtra(DataContract.NOVEL_OBJECT_NAME, novelModel);
        intent.putExtra(DataContract.NOVEL_CHAPTER_ID_NAME, chapterId);
        intent.putExtra(DataContract.NOVEL_CHAPTER_OFFSET_NAME, chapterOffset);
        saveChaptersInRunTimeStore(chapterList);
        /*Bundle options = ActivityOptions.makeCustomAnimation(context, android.R.anim.fade_in, android.R.anim.fade_out).toBundle();*/
        context.startActivity(intent);
    }

    public void saveChaptersInRunTimeStore(List<NovelChapterModel> chapters) {
        try {
            if(mRunTimeStore.containsKey(DataContract.NOVEL_CHAPTER_LIST_NAME)) {
                mRunTimeStore.remove(DataContract.NOVEL_CHAPTER_LIST_NAME);
            }
            mRunTimeStore.put(DataContract.NOVEL_CHAPTER_LIST_NAME, chapters);
        } catch (Exception e) {
            Timber.e(e, "saveChaptersInRunTimeStore error", TAG);
        }
    }

    public List<NovelChapterModel> getChaptersInRunTimeStore() {
        try {
            if (mRunTimeStore.containsKey(DataContract.NOVEL_CHAPTER_LIST_NAME)) {
                List<NovelChapterModel> result = (List<NovelChapterModel>) mRunTimeStore.get(DataContract.NOVEL_CHAPTER_LIST_NAME);
                mRunTimeStore.remove(DataContract.NOVEL_CHAPTER_LIST_NAME);
                return result;
            } else {
                return null;
            }
        } catch (Exception e) {
            Timber.e(e, "getChaptersInRunTimeStore error", TAG);
            return null;
        }
    }

    public void removeChaptersInRunTimeStore() {
        try {
            if (mRunTimeStore.containsKey(DataContract.NOVEL_CHAPTER_LIST_NAME)) {
                mRunTimeStore.remove(DataContract.NOVEL_CHAPTER_LIST_NAME);
            }
        } catch (Exception e) {
            Timber.e(e, "removeChaptersInRunTimeStore error", TAG);
        }
    }

    public void clearRunTimeStore() {
        mRunTimeStore.clear();
    }

    private Context getContextFromView(Object view) {
        Context context;
        if(view instanceof Fragment)
            context = ((Fragment)view).getActivity();
        else if(view instanceof Activity)
            context = (Activity)view;
        else if(view instanceof Dialog)
            context = ((Dialog)view).getOwnerActivity();
        else
            throw new IllegalArgumentException("Param view must be a fragment,an activity or a dialog.");
        return context;
    }

    private Activity getActivityFromView(Object view) {
        Activity activity;
        if(view instanceof Fragment)
            activity = ((Fragment)view).getActivity();
        else if(view instanceof Activity)
            activity = (Activity)view;
        else if(view instanceof Dialog)
            activity = ((Dialog)view).getOwnerActivity();
        else
            throw new IllegalArgumentException("Param view must be a fragment,an activity or a dialog.");
        return activity;
    }
}
