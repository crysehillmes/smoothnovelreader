package org.cryse.novelreader.presenter.impl;

import android.support.v4.util.Pair;
import android.view.View;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.view.NovelBookShelfView;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NovelBookShelfPresenterImpl implements NovelBookShelfPresenter {
    private static final String LOG_TAG = NovelBookShelfPresenterImpl.class.getSimpleName();
    NovelBookShelfView mView;

    Subscription subscription;

    NovelBusinessLogicLayer mNovelBusinessLogicLayer;

    AndroidNavigation mDisplay;

    SnackbarUtils mToastUtil;

    @Inject
    public NovelBookShelfPresenterImpl(NovelBusinessLogicLayer mNovelBusinessLogicLayer, AndroidNavigation display, SnackbarUtils snackbarUtils) {
        this.mNovelBusinessLogicLayer = mNovelBusinessLogicLayer;
        this.mDisplay = display;
        this.mToastUtil = snackbarUtils;
        this.mView = null;
    }

    @Override
    public void loadFavoriteNovels() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        subscription = mNovelBusinessLogicLayer.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.showBooksOnShelf(result);
                            }
                        },
                        error -> {
                            if (mView != null) {
                                mView.setLoading(false);
                                mToastUtil.showExceptionToast(mView, error);
                            }
                            Timber.e(error, error.getMessage(), LOG_TAG);
                        },
                        () -> {
                            if (mView != null) {
                                mView.setLoading(false);
                            }
                            Timber.d("Load completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void getNovelUpdates() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        subscription = mNovelBusinessLogicLayer.getNovelUpdate()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.showBooksOnShelf(result);
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                                mToastUtil.showExceptionToast(mView, error);
                            }
                        },
                        () -> {
                            if (mView != null) {
                                mView.setLoading(false);
                            }
                            Timber.d("Load completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void removeFromFavorite(String... novelIds) {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        subscription = mNovelBusinessLogicLayer.removeFromFavorite(novelIds)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {
                            if (mView != null) {
                                mView.setLoading(false);
                                mToastUtil.showExceptionToast(mView, error);
                            }
                            Timber.e(error, error.getMessage(), LOG_TAG);
                        },
                        () -> {
                            if (mView != null) {
                                mView.setLoading(false);
                            }
                            Timber.d("Load completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void showNovelChapterList(NovelModel novelModel) {
        mDisplay.showNovelChapterList(mView, novelModel);
    }

    @Override
    public void goSearch(String queryString, Pair<View, String>... transitionPairs) {
        mDisplay.showSearchActivity(mView, queryString, transitionPairs);
    }

    @Override
    public void showNovelDetail(NovelModel novelModel) {
        mDisplay.showNovelDetailView(
                mView,
                novelModel,
                true
        );
    }

    @Override
    public void bindView(NovelBookShelfView view) {
        Timber.d(String.format("bindView: %s", view.getClass().getName()), LOG_TAG);
        this.mView = view;
    }

    @Override
    public void unbindView() {
        Timber.d("unbindView", LOG_TAG);
        mView = null;
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
    }
}
