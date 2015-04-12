package org.cryse.novelreader.ui.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;

import org.cryse.novelreader.R;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.ThemeColorChangedEvent;
import org.cryse.novelreader.ui.FadeTransitionActivity;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.ThemeEngine;
import org.cryse.novelreader.util.UIUtils;

import javax.inject.Inject;

public abstract class AbstractThemeableActivity extends AbstractActivity {
    @Inject
    ThemeEngine mThemeEngine;

    @Inject
    RunTimeStore mRunTimeStore;

    private int mDarkTheme = R.style.SmoothTheme_Dark;
    private int mLightTheme = R.style.SmoothTheme_Light;
    private int mTheme;
    private boolean mIsOverrideStatusBarColor = true;
    private boolean mIsOverrideToolbarColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int width = displaymetrics.widthPixels;
    }

    @Override
    protected void setUpToolbar(int toolbarLayoutId, int customToolbarShadowId) {
        super.setUpToolbar(toolbarLayoutId, customToolbarShadowId);
        if(getSupportActionBar() != null && mIsOverrideToolbarColor)
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mThemeEngine.getPrimaryColor(this)));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if(mIsOverrideStatusBarColor)
                getWindow().setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    protected void reload() {
        // Style中定义的动画用于平滑recreate(), 而这里则是用于防止返回时的闪烁，
        // 目前不清楚如何直接处理 recreate 的动画，只能用这种方法了
        // overridePendingTransition(0,0);
        // recreate() 不能在 onResume() 或 onCreate() 中调用，因为 recreate() 时先会结束当前的生命周期。

        View view = getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap drawingCache = view.getDrawingCache();

        int statusHeight = UIUtils.calculateStatusBarSize(this);
        Bitmap screenShot = Bitmap.createBitmap(drawingCache,0,statusHeight,drawingCache.getWidth(),drawingCache.getHeight() - statusHeight);

        Intent intent = new Intent(this, FadeTransitionActivity.class);
        /*ByteArrayOutputStream bs = new ByteArrayOutputStream();
        screenShot.compress(Bitmap.CompressFormat.PNG, 50, bs);
        intent.putExtra("screen_shot", bs.toByteArray());
        *//*intent.putExtra("screen_shot", screenShot);*/
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
        return mThemeEngine.isNightMode();
    }

    protected int getAppTheme() {
        if(isNightMode())
            return mDarkTheme;
        else
            return mLightTheme;
    }

    public void setNightMode(boolean isNightMode) {
        if(isNightMode != isNightMode()) {
            mThemeEngine.setNightMode(isNightMode);
            //mTheme = getAppTheme();
            reloadTheme();
        }
    }

    public void setIsOverrideStatusBarColor(boolean isOverrideStatusBarColor) {
        this.mIsOverrideStatusBarColor = isOverrideStatusBarColor;
    }

    public void setIsOverrideToolbarColor(boolean isOverrideToolbarColor) {
        this.mIsOverrideToolbarColor = isOverrideToolbarColor;
    }

    public ThemeEngine getThemeEngine() {
        return mThemeEngine;
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if(event instanceof ThemeColorChangedEvent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if(mIsOverrideStatusBarColor)
                    getWindow().setStatusBarColor(mThemeEngine.getPrimaryDarkColor(this));
            }
            if(getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(mThemeEngine.getPrimaryColor(this)));
        }
    }
}
