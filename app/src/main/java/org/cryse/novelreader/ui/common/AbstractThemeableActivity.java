package org.cryse.novelreader.ui.common;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.qualifier.PrefsNightMode;
import org.cryse.novelreader.ui.FadeTransitionActivity;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.prefs.BooleanPreference;

import java.io.ByteArrayOutputStream;

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
        if (savedInstanceState == null) {

        } else {
            //mTheme = savedInstanceState.getInt("theme");
        }

        setTheme(mTheme);
        super.onCreate(savedInstanceState);



        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setTranslucentStatus(true);

            getTintManager().setStatusBarTintEnabled(true);
            getTintManager().setStatusBarTintColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        }*/
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
        //recreate();
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
        //outState.putInt("theme", mTheme);

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
            //saveStateWhenReloadTheme();
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
