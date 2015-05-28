package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.view.NovelBookShelfView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import java.util.List;

import javax.inject.Inject;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NovelBookShelfPresenterImpl implements NovelBookShelfPresenter {
    private static final String LOG_TAG = NovelBookShelfPresenterImpl.class.getSimpleName();
    NovelBookShelfView mView;

    Subscription subscription;
    Subscription addLocalBookSubscription;

    NovelBusinessLogicLayer mNovelBusinessLogicLayer;

    AndroidNavigation mDisplay;

    ToastUtil mToastUtil;

    @Inject
    public NovelBookShelfPresenterImpl(NovelBusinessLogicLayer mNovelBusinessLogicLayer, AndroidNavigation display, ToastUtil toastUtil) {
        this.mNovelBusinessLogicLayer = mNovelBusinessLogicLayer;
        this.mDisplay = display;
        this.mToastUtil = toastUtil;
        this.mView = new EmptyBookShelfView();
    }

    @Override
    public void addLocalTextFile(String filePath, String customTitle) {
        SubscriptionUtils.checkAndUnsubscribe(addLocalBookSubscription);
        addLocalBookSubscription = mNovelBusinessLogicLayer.addLocalTextFile(filePath, customTitle)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            loadFavoriteNovels();
                        },
                        error -> {

                            mView.showAddLocalBookProgressDialog(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.showAddLocalBookProgressDialog(false);
                            Timber.d("Load completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void loadFavoriteNovels() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        subscription = mNovelBusinessLogicLayer.getFavorites()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.showBooksOnShelf(result);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.setLoading(false);
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
                            mView.showBooksOnShelf(result);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.setLoading(false);
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
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.setLoading(false);
                            Timber.d("Load completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void showNovelChapterList(NovelModel novelModel) {
        mDisplay.showNovelChapterList(mView, novelModel);
    }

    @Override
    public void goSearch() {
        mDisplay.showSearchActivity(mView);
    }

    @Override
    public void bindView(NovelBookShelfView view) {
        Timber.d(String.format("bindView: %s", view.getClass().getName()), LOG_TAG);
        this.mView = view;
    }

    @Override
    public void unbindView() {
        Timber.d("unbindView", LOG_TAG);
        bindView(new EmptyBookShelfView());
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        SubscriptionUtils.checkAndUnsubscribe(addLocalBookSubscription);
    }

    private class EmptyBookShelfView implements NovelBookShelfView {

        @Override
        public void showBooksOnShelf(List<NovelModel> books) {

        }

        @Override
        public void showAddLocalBookProgressDialog(boolean show) {

        }

        @Override
        public void setLoading(Boolean isLoading) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showToast(String text, ToastType toastType) {

        }
    }
}
