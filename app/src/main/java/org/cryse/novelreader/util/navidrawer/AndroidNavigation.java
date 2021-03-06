package org.cryse.novelreader.util.navidrawer;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.cryse.novelreader.application.factory.StaticRunTimeStoreFactory;
import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.ui.NovelChapterListActivity;
import org.cryse.novelreader.ui.NovelDetailActivity;
import org.cryse.novelreader.ui.NovelReadViewActivity;
import org.cryse.novelreader.ui.SettingsActivity;
import org.cryse.novelreader.util.RunTimeStore;

import java.util.List;

import timber.log.Timber;

public class AndroidNavigation {
    private static final String TAG = AndroidNavigation.class.getCanonicalName();
    RunTimeStore mRunTimeStore;

    public AndroidNavigation() {
        mRunTimeStore = StaticRunTimeStoreFactory.getInstance();
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

    public void showNovelReadActivity(Object currentView, NovelModel novelModel, String chapterId, int chapterOffset, List<ChapterModel> chapterList) {
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

    public void saveChaptersInRunTimeStore(List<ChapterModel> chapters) {
        try {
            if(mRunTimeStore.containsKey(DataContract.NOVEL_CHAPTER_LIST_NAME)) {
                mRunTimeStore.remove(DataContract.NOVEL_CHAPTER_LIST_NAME);
            }
            mRunTimeStore.put(DataContract.NOVEL_CHAPTER_LIST_NAME, chapters);
        } catch (Exception e) {
            Timber.e(e, "saveChaptersInRunTimeStore error", TAG);
        }
    }

    public List<ChapterModel> getChaptersInRunTimeStore() {
        try {
            if (mRunTimeStore.containsKey(DataContract.NOVEL_CHAPTER_LIST_NAME)) {
                List<ChapterModel> result = (List<ChapterModel>) mRunTimeStore.get(DataContract.NOVEL_CHAPTER_LIST_NAME);
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
