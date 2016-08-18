package org.cryse.novelreader.ui.common;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import org.cryse.novelreader.R;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SnackbarSupport;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastErrorConstant;

import java.util.ArrayList;
import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractFragment extends Fragment implements SnackbarSupport {
    private int mPrimaryColor;
    private int mPrimaryDarkColor;
    private int mAccentColor;

    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();

    RxEventBus mEventBus = RxEventBus.getInstance();

    private Subscription mEventBusSubscription;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
        mPrimaryColor = ColorUtils.getColorFromAttr(getActivity(), R.attr.colorPrimary);
        mPrimaryDarkColor = ColorUtils.getColorFromAttr(getActivity(), R.attr.colorPrimaryDark);
        mAccentColor = ColorUtils.getColorFromAttr(getActivity(), R.attr.colorAccent);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Nullable
    protected final String getATEKey() {
        return PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean("dark_theme", false) ?
                "dark_theme" : "light_theme";
    }

    protected List<Runnable> getDeferredUiOperations() {
        return mDeferredUiOperations;
    }

    protected void tryExecuteDeferredUiOperations() {
        for (Runnable r : mDeferredUiOperations) {
            r.run();
        }
        mDeferredUiOperations.clear();
    }

    protected abstract void injectThis();

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        analyticsTrackEnter();
    }

    @Override
    public void onPause() {
        super.onPause();
        analyticsTrackExit();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        SubscriptionUtils.checkAndUnsubscribe(mEventBusSubscription);
    }

    public AppCompatActivity getAppCompatActivity() {
        return (AppCompatActivity)getActivity();
    }

    public AbstractActivity getThemedActivity() {
        return (AbstractActivity)getActivity();
    }

    protected String getFragmentName() {
        return getClass().getCanonicalName();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        getActivity().invalidateOptionsMenu();
    }

    public Boolean isNightMode() {
        if(isAdded())
            return getThemedActivity().isNightMode();
        else
            return null;
    }

    public void toggleNightMode() {
        if(getThemedActivity() != null)
            getThemedActivity().toggleNightMode();
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

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();

    protected void onEvent(AbstractEvent event) {

    }

    protected View getSnackbarRootView() {
        return getView();
    }

    @Override
    public void showSnackbar(CharSequence text, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                text,
                type,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    @Override
    public void showSnackbar(int errorCode, SimpleSnackbarType type, Object... args) {
        SnackbarUtils.makeSimple(
                getSnackbarRootView(),
                getString(ToastErrorConstant.errorCodeToStringRes(errorCode)),
                type,
                Snackbar.LENGTH_SHORT
        ).show();
    }

    public RxEventBus getEventBus() {
        return mEventBus;
    }
}
