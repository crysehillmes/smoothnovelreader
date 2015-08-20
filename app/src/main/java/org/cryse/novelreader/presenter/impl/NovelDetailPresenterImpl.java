package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.view.NovelDetailView;

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
        this.mView = null;
    }

    @Override
    public void bindView(NovelDetailView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
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
                            if (mView != null) {
                                mView.showNovelDetail(result);
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                                mSnackbarUtils.showExceptionToast(mView, error);
                            }
                        },
                        () -> {
                            Timber.d("loadNovelDetail completed!", LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                            }
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
                            if (mView != null) {
                                mView.setFavoriteButtonStatus(result);
                            }
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
        if (mView != null) {
            mDisplay.showNovelChapterList(mView, novelModel);
        }
    }
}
