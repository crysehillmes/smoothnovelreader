package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.view.NovelDetailView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NovelDetailPresenterImpl implements NovelDetailPresenter {
    private static final String LOG_TAG = NovelDetailPresenterImpl.class.getSimpleName();
    private NovelDetailView mView;
    private Subscription mLoadDetailSubscription;
    private Subscription mCheckFavoriteStatusSubscription;
    private Subscription mAddFavoriteSubscription;
    private NovelBusinessLogicLayer dataService;
    private SnackbarUtils mSnackbarUtils;
    private AndroidNavigation mDisplay;
    @Inject
    public NovelDetailPresenterImpl(NovelBusinessLogicLayer dataService, AndroidNavigation display, SnackbarUtils snackbarUtils) {
        this.dataService = dataService;
        this.mDisplay = display;
        this.mSnackbarUtils = snackbarUtils;
        this.mView = new EmptyNovelDetailView();
    }

    @Override
    public void bindView(NovelDetailView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        bindView(new EmptyNovelDetailView());
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDetailSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mCheckFavoriteStatusSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mAddFavoriteSubscription);

    }

    @Override
     public void loadNovelDetail(NovelModel novelModel) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadDetailSubscription);
        mLoadDetailSubscription = dataService.getNovelDetail(novelModel.getNovelId(), novelModel.getSource())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.showNovelDetail(result);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mSnackbarUtils.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.setLoading(false);
                            Timber.d("loadNovelDetail completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void checkNovelFavoriteStatus(NovelModel novelModel) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckFavoriteStatusSubscription);
        mCheckFavoriteStatusSubscription = dataService.isFavorite(novelModel.getNovelId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.setFavoriteButtonStatus(result);
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                        },
                        () -> {
                            Timber.d("checkNovelFavoriteStatus completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void addOrRemoveFavorite(NovelModel novelModel, boolean isAdd) {
        SubscriptionUtils.checkAndUnsubscribe(mAddFavoriteSubscription);
        Observable<Void> observable = isAdd ? dataService.addToFavorite(novelModel) : dataService.removeFromFavorite(novelModel.getNovelId());
        mAddFavoriteSubscription = observable.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                        },
                        () -> {
                            Timber.d("addOrRemoveFavorite completed!", LOG_TAG);
                        }
                );
    }

    @Override
    public void startReading(NovelModel novelModel) {
        mDisplay.showNovelChapterList(mView, novelModel);
    }

    private class EmptyNovelDetailView implements NovelDetailView {

        @Override
        public void showNovelDetail(NovelDetailModel novelDetail) {

        }

        @Override
        public void setFavoriteButtonStatus(boolean isFavorited) {

        }

        @Override
        public void setLoading(Boolean isLoading) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public void showSnackbar(CharSequence text, SimpleSnackbarType type) {

        }
    }
}
