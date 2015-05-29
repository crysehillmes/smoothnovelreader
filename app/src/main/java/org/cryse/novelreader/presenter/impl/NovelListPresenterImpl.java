package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelListPresenter;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.view.NovelOnlineListView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import java.util.List;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NovelListPresenterImpl implements NovelListPresenter {
    private static final String LOG_TAG = NovelListPresenterImpl.class.getSimpleName();
    NovelOnlineListView mView;
    Subscription subscription;

    NovelBusinessLogicLayer novelBusinessLogicLayer;

    AndroidNavigation mDisplay;

    SnackbarUtils mSnackbarUtils;

    public NovelListPresenterImpl(NovelBusinessLogicLayer novelBusinessLogicLayer, AndroidNavigation display, SnackbarUtils snackbarUtils) {
        this.novelBusinessLogicLayer = novelBusinessLogicLayer;
        this.mDisplay = display;
        this.mSnackbarUtils = snackbarUtils;
        this.mView = new EmptyNovelListView();
    }

    @Override
    public void loadNovelCategoryList(String category, String subCategory, int page, int status, boolean isByTag, boolean append) {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        setLoadingStatus(append, true);
        subscription = novelBusinessLogicLayer.getCategories(category, subCategory, page, status, isByTag)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.getNovelListSuccess(result, append);
                        },
                        error -> {
                            setLoadingStatus(append, false);
                            mView.getNovelListFailure(error);
                            Timber.e(error, error.getMessage(), getLogTag());
                            /*mToastUtil.showExceptionToast(mView, error);*/
                        },
                        () -> {
                            setLoadingStatus(append, false);
                            Timber.d("Load completed!", getLogTag());
                        }
                );
    }

    @Override
    public void loadNovelRankList(String rank, int page, boolean append) {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        setLoadingStatus(append, true);
        subscription = novelBusinessLogicLayer.getRanks(rank, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.getNovelListSuccess(result, append);
                        },
                        error -> {
                            setLoadingStatus(append, false);
                            mView.getNovelListFailure(error);
                            Timber.e(error, error.getMessage(), getLogTag());
                            /*mToastUtil.showExceptionToast(mView, error);*/
                        },
                        () -> {
                            setLoadingStatus(append, false);
                            Timber.d("Load completed!", getLogTag());
                        }
                );
    }

    @Override
    public void searchNovel(String query, int page, boolean append) {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
        setLoadingStatus(append, true);
        subscription = novelBusinessLogicLayer.search(query, page)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.getNovelListSuccess(result, append);
                        },
                        error -> {
                            setLoadingStatus(append, false);
                            mView.getNovelListFailure(error);
                            Timber.e(error, error.getMessage(), getLogTag());
                            /*mToastUtil.showExceptionToast(mView, error);*/
                        },
                        () -> {
                            setLoadingStatus(append, false);
                            Timber.d("Load completed!", getLogTag());
                        }
                );
    }

    @Override
    public void showNovelIntroduction(
            NovelModel novelModel)
    {
        mDisplay.showNovelDetailView(
                mView,
                novelModel,
                true
        );
    }

    @Override
    public void bindView(NovelOnlineListView view) {
        mView = view;
    }

    @Override
    public void unbindView() {
        bindView(new EmptyNovelListView());
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(subscription);
    }

    public String getLogTag() {
        return LOG_TAG;
    }

    private void setLoadingStatus(boolean append, boolean isLoading) {
        if(append)
            mView.setLoadingMore(isLoading);
        else
            mView.setLoading(isLoading);
    }

    private class EmptyNovelListView implements NovelOnlineListView {

        @Override
        public void getNovelListSuccess(List<NovelModel> novels, boolean append) {

        }

        @Override
        public void getNovelListFailure(Throwable e, Object... extras) {

        }

        @Override
        public void setLoading(Boolean isLoading) {

        }

        @Override
        public Boolean isLoading() {
            return null;
        }

        @Override
        public boolean isLoadingMore() {
            return false;
        }

        @Override
        public void setLoadingMore(boolean value) {

        }

        @Override
        public void showSnackbar(CharSequence text, SimpleSnackbarType type) {

        }
    }
}
