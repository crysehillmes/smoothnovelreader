package org.cryse.novelreader.ui.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.qualifier.PrefsNightMode;
import org.cryse.novelreader.ui.FadeTransitionActivity;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.prefs.BooleanPreference;

import javax.inject.Inject;

public abstract class AbstractThemeableActivity extends AbstractActivity {
    @Inject
    @PrefsNightMode
    BooleanPreference mPrefNightMode;

    @Inject
    RunTimeStore mRunTimeStore;

    private int mDarkTheme = R.style.SmoothTheme_Dark;
    private int mLightTheme = R.style.SmoothTheme_Light;
    private int mTheme;

    @Override
    protected void injectThis() {
        // do not inject in parent.
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SmoothReaderApplication.get(this).inject(this);
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void reload() {

        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();

        int statusHeight = UIUtils.calculateStatusBarSize(this);
        Bitmap screenShot = Bitmap.createBitmap(drawingCache,0,statusHeight,drawingCache.getWidth(),drawingCache.getHeight() - statusHeight);

        Intent intent = new Intent(this, FadeTransitionActivity.class);
        mRunTimeStore.put("screen_shot", screenShot);
        startActivity(intent);
        overridePendingTransition(0, 0);
        recreate();
    }



    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mTheme != getAppTheme() || isNeedToReload()) {
            reload();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

    }

    protected boolean isNeedToReload() {
        return false;
    }

    public void reloadTheme() {
        reloadTheme(false);
    }

    public void reloadTheme(boolean forceReload) {
        int appTheme = getAppTheme();
        if(this.mTheme != appTheme || forceReload) {
            this.mTheme = appTheme;
            reload();
        }
    }

    public boolean isNightMode() {
        return mPrefNightMode.get();
    }

    public int getAppTheme() {
        if(isNightMode())
            return mDarkTheme;
        else
            return mLightTheme;
    }

    public void setNightMode(boolean isNightMode) {
        if(isNightMode != isNightMode()) {
            mPrefNightMode.set(isNightMode);
            //mTheme = getAppTheme();
            reloadTheme();
        }
    }
}
