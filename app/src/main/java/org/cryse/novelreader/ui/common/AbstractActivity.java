package org.cryse.novelreader.ui.common;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.example.android.systemuivis.SystemUiHelper;

import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.util.LUtils;
import org.cryse.novelreader.util.SubscriptionUtils;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractActivity extends AppCompatActivity {
    private LUtils mLUtils;
    private SystemUiHelper mSystemUiHelper;
    private Toolbar mToolbar;
    private View mPreLShadow;
    private ActionMode mActionMode;
    private Subscription mEventBusSubscription;
    private View mSnackbarRootView;
    @Inject
    RxEventBus mEventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLUtils = LUtils.getInstance(this);
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        mSnackbarRootView =  findViewById(android.R.id.content);
    }

    protected void setUpToolbar(int toolbarLayoutId, int customToolbarShadowId) {
        if (mToolbar == null) {
            mToolbar = (Toolbar) findViewById(toolbarLayoutId);
            mPreLShadow = findViewById(customToolbarShadowId);
            if (mToolbar != null) {
                //UIUtils.setInsets(this, mToolbar, false);
                if(Build.VERSION.SDK_INT < 21 && mPreLShadow != null) {
                    mPreLShadow.setVisibility(View.VISIBLE);
                } else if(Build.VERSION.SDK_INT >= 21 && mPreLShadow != null) {
                    mPreLShadow.setVisibility(View.GONE);
                }
                setSupportActionBar(mToolbar);
            } else {
                Log.e("AbstractActivity", "Toolbar is null");
            }
        }
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

    public LUtils getLUtils() {
        return mLUtils;
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

    protected abstract void injectThis();

    public Toolbar getToolbar() {
        return mToolbar;
    }

    public Context getThemedContext() {
        return getSupportActionBar().getThemedContext();
    }

    public ActionMode getActionMode() {
        Log.d("WWW", String.format("getActionMode: mActionMode == null = %b", mActionMode == null));
        return mActionMode;
    }

    public void setActionMode(ActionMode actionMode) {
        Log.d("WWW", String.format("setActionMode: mActionMode == null = %b", actionMode == null));
        this.mActionMode = actionMode;
    }

    @Override
    public ActionMode startSupportActionMode(final ActionMode.Callback callback) {
        // Fix for bug https://code.google.com/p/android/issues/detail?id=159527
        final ActionMode mode = super.startSupportActionMode(callback);
        if (mode != null) {
            mode.invalidate();
        }
        return mode;
    }

    public void setPreLShadowVisibility(boolean visibility) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP && mPreLShadow != null)
            mPreLShadow.setVisibility(visibility ? View.VISIBLE : View.GONE);
    }

    public void finishCompat() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            this.finishAfterTransition();
        else
            this.finish();
    }

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();


    protected void onEvent(AbstractEvent event) {

    }

    protected RxEventBus getEventBus() {
        return mEventBus;
    }

    protected View getSnackbarRootView() {
        if(mSnackbarRootView == null)
            mSnackbarRootView = findViewById(android.R.id.content);
        return mSnackbarRootView;
    }
}