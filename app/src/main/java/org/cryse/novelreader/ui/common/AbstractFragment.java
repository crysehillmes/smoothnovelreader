package org.cryse.novelreader.ui.common;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.view.View;

import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.util.SubscriptionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class AbstractFragment extends android.support.v4.app.Fragment {
    private List<Runnable> mDeferredUiOperations = new ArrayList<Runnable>();

    @Inject
    RxEventBus mEventBus;

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

    public void setActionMode(ActionMode actionMode) {
        ((AbstractActivity)getAppCompatActivity()).setActionMode(actionMode);
    }

    public ActionMode getActionMode() {
        return ((AbstractActivity)getAppCompatActivity()).getActionMode();
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

    protected View getSnackbarRootView() {
        return getView();
    }
}
