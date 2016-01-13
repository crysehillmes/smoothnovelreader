package org.cryse.novelreader.ui.common;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;

import com.afollestad.appthemeengine.ATE;
import com.afollestad.appthemeengine.ATEActivity;
import com.afollestad.appthemeengine.Config;
import com.example.android.systemuivis.SystemUiHelper;

import org.cryse.novelreader.R;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.RxEventBus;
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

public abstract class AbstractActivity extends ATEActivity implements SnackbarSupport {
    private View mSnackbarRootView;
    private SystemUiHelper mSystemUiHelper;
    private Subscription mEventBusSubscription;
    private boolean mIsDestroyed;
    private int mPrimaryColor;
    private int mPrimaryDarkColor;
    private int mAccentColor;
    protected String mATEKey;
    protected BooleanPrefs mIsNightMode;
    RxEventBus mEventBus = RxEventBus.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Default config
        if (!ATE.config(this, "light_theme").isConfigured(1)) {
            ATE.config(this, "light_theme")
                    .activityTheme(R.style.AppTheme)
                    .primaryColorRes(R.color.colorPrimaryLightDefault)
                    .accentColorRes(R.color.colorAccentLightDefault)
                    .lightToolbarMode(Config.LIGHT_TOOLBAR_AUTO)
                    .coloredActionBar(true)
                    .coloredNavigationBar(false)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        if (!ATE.config(this, "dark_theme").isConfigured(1)) {
            ATE.config(this, "dark_theme")
                    .activityTheme(R.style.AppThemeDark)
                    .primaryColorRes(R.color.colorPrimaryDarkDefault)
                    .accentColorRes(R.color.colorAccentDarkDefault)
                    .lightToolbarMode(Config.LIGHT_TOOLBAR_AUTO)
                    .coloredActionBar(true)
                    .coloredNavigationBar(true)
                    .usingMaterialDialogs(true)
                    .commit();
        }
        mATEKey = getATEKey();
        super.onCreate(savedInstanceState);
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
        mIsNightMode = Prefs.getBooleanPrefs(
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE,
                PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE_VALUE
        );
        mPrimaryColor = Config.primaryColor(this, mATEKey);
        mPrimaryDarkColor = Config.primaryColorDark(this, mATEKey);
        mAccentColor = Config.accentColor(this, mATEKey);
    }

    @Nullable
    @Override
    protected final String getATEKey() {
        return PreferenceManager.getDefaultSharedPreferences(this).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
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
        mIsNightMode.set(!mIsNightMode.get());
        Config.markChanged(this, "light_theme");
        Config.markChanged(this, "dark_theme");
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
}