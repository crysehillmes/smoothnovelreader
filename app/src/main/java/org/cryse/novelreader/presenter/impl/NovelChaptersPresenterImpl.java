package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.view.NovelChaptersView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;

import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class NovelChaptersPresenterImpl implements NovelChaptersPresenter {
    private static final String LOG_TAG = NovelChaptersPresenterImpl.class.getSimpleName();
    NovelChaptersView mView;

    Subscription mLoadChaptersSubscription;

    Subscription mCheckLastReadSubscription;

    Subscription mGetLastReadBookMarkSubscription;

    Subscription mCheckFavoriteStatusSubscription;

    NovelBusinessLogicLayer mNovelBusinessLogicLayer;

    AndroidDisplay mDisplay;

    ToastUtil mToastUtil;

    @Inject
    public NovelChaptersPresenterImpl(NovelBusinessLogicLayer mNovelBusinessLogicLayer, AndroidDisplay display, ToastUtil toastUtil) {
        this.mNovelBusinessLogicLayer = mNovelBusinessLogicLayer;
        this.mDisplay = display;
        this.mToastUtil = toastUtil;
    }

    @Override
    public void loadChapters(NovelModel novelModel) {
        loadChapters(novelModel, false);
    }

    @Override
    public void loadChapters(NovelModel novelModel, boolean forceUpdate) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadChaptersSubscription);
        mLoadChaptersSubscription = mNovelBusinessLogicLayer.getChapterList(novelModel, forceUpdate)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.showChapterList(result);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e("Load chapter list error:", error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.setLoading(false);
                            Timber.d("Load chapter list completed!", LOG_TAG);
                        }
                );
    }

    public void checkNovelFavoriteStatus(NovelModel novelModel) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckFavoriteStatusSubscription);
        mCheckFavoriteStatusSubscription = mNovelBusinessLogicLayer.isFavorite(novelModel.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.checkFavoriteStatusComplete(result);
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
    public void bindView(NovelChaptersView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        bindView(new EmptyNovelChaptersView());
    }

    @Override
    public void destroy() {
        SubscriptionUtils.checkAndUnsubscribe(mLoadChaptersSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mCheckLastReadSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mGetLastReadBookMarkSubscription);
        SubscriptionUtils.checkAndUnsubscribe(mCheckFavoriteStatusSubscription);
        //mDisplay.removeChaptersInRunTimeStore();
    }

    public void checkLastReadState(NovelModel novelModel) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckLastReadSubscription);
        mCheckLastReadSubscription = mNovelBusinessLogicLayer.checkLastReadBookMarkState(novelModel.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.canGoToLastRead(result);
                        },
                        error -> {
                            mView.canGoToLastRead(null);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            Timber.d("Check completed.", LOG_TAG);
                        }
                );
    }

    public void readChapter(NovelModel novelModel, String chapterId, List<NovelChapterModel> chapterList) {
        mDisplay.showNovelReadActivity(mView, novelModel, chapterId, 0, chapterList);
    }

    @Override
    public void readLastPosition(final NovelModel novelModel, final List<NovelChapterModel> chapterList) {
        SubscriptionUtils.checkAndUnsubscribe(mGetLastReadBookMarkSubscription);
        mGetLastReadBookMarkSubscription = mNovelBusinessLogicLayer.getLastReadBookMark(novelModel.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mDisplay.showNovelReadActivity(mView, novelModel, result.getChapterId(), result.getChapterOffset(), chapterList);
                        },
                        error -> {
                            mView.canGoToLastRead(null);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            Timber.d("Get read last position completed.", LOG_TAG);
                        }
                );
    }

    @Override
    public void showNovelIntroduction(NovelModel novelModel) {
        mDisplay.showNovelDetailView(mView, novelModel, false);
    }

    public Observable<Boolean> preloadChapterContents(NovelModel novel, List<NovelChapterModel> chapterModels) {
        return mNovelBusinessLogicLayer.preloadChapterContents(novel, chapterModels);
    }

    private class EmptyNovelChaptersView implements NovelChaptersView {

        @Override
        public void showChapterList(List<NovelChapterModel> chapterList) {

        }

        @Override
        public void canGoToLastRead(NovelBookMarkModel bookMark) {

        }

        @Override
        public void checkFavoriteStatusComplete(Boolean isFavorite) {

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
