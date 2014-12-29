package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelDataService;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelBookShelfPresenter;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.view.NovelBookShelfView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

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

    NovelDataService mNovelDataService;

    AndroidDisplay mDisplay;

    ToastUtil mToastUtil;

    @Inject
    public NovelBookShelfPresenterImpl(NovelDataService mNovelDataService, AndroidDisplay display, ToastUtil toastUtil) {
        this.mNovelDataService = mNovelDataService;
        this.mDisplay = display;
        this.mToastUtil = toastUtil;
        this.mView = new EmptyBookShelfView();
    }

    @Override
    public void getFavoritedNovels() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        subscription = mNovelDataService.getFavorited()
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
        subscription = mNovelDataService.getNovelUpdate()
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
        subscription = mNovelDataService.removeFromFavorite(novelIds)
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
        this.mView = view;
    }

    @Override
    public void unbindView() {
        bindView(new EmptyBookShelfView());
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
    }

    private class EmptyBookShelfView implements NovelBookShelfView {

        @Override
        public void showBooksOnShelf(List<NovelModel> books) {

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
