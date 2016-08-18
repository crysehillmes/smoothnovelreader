package org.cryse.novelreader.ui.common;

import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.View;

import com.example.android.systemuivis.SystemUiHelper;

import org.cryse.novelreader.R;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SnackbarSupport;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastErrorConstant;
import org.cryse.utils.preference.BooleanPrefs;
import org.cryse.utils.preference.Prefs;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractActivity extends AppCompatActivity implements SnackbarSupport {
    private View mSnackbarRootView;
    private SystemUiHelper mSystemUiHelper;
    private Subscription mEventBusSubscription;
    private boolean mIsDestroyed;
    private int mPrimaryColor;
    private int mPrimaryDarkColor;
    private int mAccentColor;
    protected BooleanPrefs mIsNightMode;
    protected boolean mCurrentNightMode;
    RxEventBus mEventBus = RxEventBus.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
        mIsNightMode = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE,
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE
        );
        mCurrentNightMode = mIsNightMode.get();
        mPrimaryColor = ColorUtils.getColorFromAttr(this, R.attr.colorPrimary);
        mPrimaryDarkColor = ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark);
        mAccentColor = ColorUtils.getColorFromAttr(this, R.attr.colorAccent);
        /*mPrimaryColor = Config.primaryColor(this, mATEKey);
        mPrimaryDarkColor = Config.primaryColorDark(this, mATEKey);
        mAccentColor = Config.accentColor(this, mATEKey);*/
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mSnackbarRootView =  findViewById(android.R.id.content);
    }

    @Override
    protected void onResume() {
        super.onResume();
        analyticsTrackEnter();
        if(mCurrentNightMode != mIsNightMode.get()) {
            recreate();
        }
        /*boolean appliedDayNight = isDelegateNightMode();
        if(appliedDayNight != mIsNightMode.get()) {
            recreate();
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        analyticsTrackExit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.checkAndUnsubscribe(mEventBusSubscription);
        mIsDestroyed = true;
    }

    public boolean isNightMode() {
        return mIsNightMode.get();
    }

    public void toggleNightMode() {
        boolean isNightMode = !mIsNightMode.get();
        mIsNightMode.set(isNightMode);
        /*Config.markChanged(this, "light_theme");
        Config.markChanged(this, "dark_theme");*/
        AppCompatDelegate.setDefaultNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        // getDelegate().setLocalNightMode(isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        getDelegate().applyDayNight(); // (isNightMode ? AppCompatDelegate.MODE_NIGHT_YES : AppCompatDelegate.MODE_NIGHT_NO);
        recreate();
    }

    /**
     * Converts an intent into a {@link Bundle} suitable for use as fragment arguments.
     */
    public static Bundle intentToFragmentArguments(Intent intent) {
        Bundle arguments = new Bundle();
        if (intent == null) {
            return arguments;
        }

        final Uri data = intent.getData();
        if (data != null) {
            arguments.putParcelable("_uri", data);
        }

        final Bundle extras = intent.getExtras();
        if (extras != null) {
            arguments.putAll(intent.getExtras());
        }

        return arguments;
    }

    /**
     * Converts a fragment arguments bundle into an intent.
     */
    public static Intent fragmentArgumentsToIntent(Bundle arguments) {
        Intent intent = new Intent();
        if (arguments == null) {
            return intent;
        }

        final Uri data = arguments.getParcelable("_uri");
        if (data != null) {
            intent.setData(data);
        }

        intent.putExtras(arguments);
        intent.removeExtra("_uri");
        return intent;
    }

    protected abstract void injectThis();

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();


    protected void onEvent(AbstractEvent event) {

    }

    protected RxEventBus getEventBus() {
        return mEventBus;
    }

    public boolean isTablet() {
        return getResources().getBoolean(R.bool.isTablet);
    }

    protected View getSnackbarRootView() {
        if(mSnackbarRootView == null)
            mSnackbarRootView = findViewById(android.R.id.content);
        return mSnackbarRootView;
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

    protected int getPrimaryColor() {
        return mPrimaryColor;
    }

    protected int getPrimaryDarkColor() {
        return mPrimaryDarkColor;
    }

    protected int getAccentColor() {
        return mAccentColor;
    }

    public boolean isActivityDestroyed() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return isDestroyed() || isFinishing();
        } else {
            return mIsDestroyed || isFinishing();
        }
    }

    public SystemUiHelper getSystemUiHelper() {
        if(mSystemUiHelper == null)
            throw new IllegalStateException("You should call requestSystemUiHelper before you do this.");
        return mSystemUiHelper;
    }

    public boolean isPreKitKat() {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT;
    }

    public void requestSystemUiHelper(int level, int flags) {
        if(level == SystemUiHelper.LEVEL_IMMERSIVE) {
            if(isPreKitKat()) {
                level = SystemUiHelper.LEVEL_LOW_PROFILE;
                flags = SystemUiHelper.FLAG_LAYOUT_IN_SCREEN_OLDER_DEVICES;
            }
        }
        mSystemUiHelper = new SystemUiHelper(this, level, flags);
    }

    public boolean isSystemUiHelperAvailable() {
        return !(mSystemUiHelper == null);
    }

    private boolean isDelegateNightMode() {
        int currentNightMode = getResources().getConfiguration().uiMode
                & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                return false;
                // Night mode is not active, we're in day time
            case Configuration.UI_MODE_NIGHT_YES:
                return true;
                // Night mode is active, we're at night!
            case Configuration.UI_MODE_NIGHT_UNDEFINED:
                return false;
                // We don't know what mode we're in, assume notnight
            default:
                return false;
        }
    }
}