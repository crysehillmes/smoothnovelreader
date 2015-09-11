package org.cryse.novelreader.ui.common;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.factory.StaticRunTimeStoreFactory;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.ThemeColorChangedEvent;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SnackbarSupport;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.ThemeEngine;
import org.cryse.novelreader.util.ToastErrorConstant;

public abstract class AbstractThemeableActivity extends AbstractActivity implements SnackbarSupport {
    protected Handler mMainThreadHandler;
    ThemeEngine mThemeEngine;
    RunTimeStore mRunTimeStore;
    private int mDarkTheme = R.style.SmoothTheme_Dark;
    private int mLightTheme = R.style.SmoothTheme_Light;
    private int mTheme;
    private boolean mIsOverrideStatusBarColor = true;
    private boolean mIsOverrideToolbarColor = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        mThemeEngine = ThemeEngine.get(this);
        mTheme = getAppTheme();
        setTheme(mTheme);
        super.onCreate(savedInstanceState);
        mRunTimeStore = StaticRunTimeStoreFactory.getInstance();
        mMainThreadHandler = new Handler();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            updateTaskDescription();
    }


    protected boolean hasSwipeBackLayout() {
        return true;
    }

    protected void setUpToolbar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            if (mIsOverrideToolbarColor)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getToolbarColor()));

        }
        if (mIsOverrideStatusBarColor) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                setStatusBarColor(getStatusBarColor());
            }
        }
    }

    public void setStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

    public void setNightMode(boolean isNightMode) {
        if (isNightMode != isNightMode()) {
            mThemeEngine.setNightMode(isNightMode);
            //mTheme = getAppTheme();
            reloadTheme();
        }
    }

    protected int getAppTheme() {
        if(isNightMode())
            return mDarkTheme;
        else
            return mLightTheme;
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
        if (event instanceof ThemeColorChangedEvent) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (mIsOverrideStatusBarColor)
                    setStatusBarColor(getStatusBarColor());
                updateTaskDescription();
            }
            if (getSupportActionBar() != null)
                getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getToolbarColor()));
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    protected void updateTaskDescription() {
        Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        setTaskDescription(
                new ActivityManager.TaskDescription(
                        getTitle().toString(),
                        iconBitmap,
                        mThemeEngine.getPrimaryColor(this)
                )
        );
        iconBitmap.recycle();
    }

    @Override
    public void showSnackbar(CharSequence text, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                text,
                type,
                SimpleSnackbarType.LENGTH_SHORT
        ).show();
    }

    @Override
    public void showSnackbar(int errorCode, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                getString(ToastErrorConstant.errorCodeToStringRes(errorCode)),
                type,
                SimpleSnackbarType.LENGTH_SHORT
        ).show();
    }

    public int getStatusBarColor() {
        return mThemeEngine.getPrimaryDarkColor(this);
    }

    public void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(color);
        }
    }

    public int getToolbarColor() {
        return mThemeEngine.getPrimaryColor(this);
    }
}
