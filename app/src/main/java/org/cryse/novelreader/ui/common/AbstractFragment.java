package org.cryse.novelreader.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
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

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractFragment extends android.support.v4.app.Fragment implements SnackbarSupport {
    @Inject
    RxEventBus mEventBus;
    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();
    private Subscription mEventBusSubscription;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mEventBusSubscription = mEventBus.toObservable()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onEvent);
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

    public AbstractThemeableActivity getThemedActivity() {
        return (AbstractThemeableActivity)getActivity();
    }

    protected String getFragmentName() {
        return getClass().getCanonicalName();
    }

    public ActionMode getActionMode() {
        return ((AbstractActivity)getAppCompatActivity()).getActionMode();
    }

    public void setActionMode(ActionMode actionMode) {
        ((AbstractActivity) getAppCompatActivity()).setActionMode(actionMode);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        getActivity().invalidateOptionsMenu();
    }

    public Boolean isNightMode() {
        if(isAdded())
            return ((AbstractThemeableActivity)getAppCompatActivity()).isNightMode();
        else
            return null;

    }

    protected abstract void analyticsTrackEnter();

    protected abstract void analyticsTrackExit();

    protected void onEvent(AbstractEvent event) {

    }

    protected int getPrimaryColor() {
        if (getThemedActivity() != null) {
            return getThemedActivity().getThemeEngine().getPrimaryColor(getActivity());
        } else {
            return ColorUtils.getColorFromAttr(getActivity(), R.attr.colorPrimary);
        }
    }

    protected int getPrimaryDarkColor() {
        if (getThemedActivity() != null) {
            return getThemedActivity().getThemeEngine().getPrimaryDarkColor(getActivity());
        } else {
            return ColorUtils.getColorFromAttr(getActivity(), R.attr.colorPrimaryDark);
        }
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
