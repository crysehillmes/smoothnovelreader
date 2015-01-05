package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelDataService;
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

    NovelDataService mNovelDataService;

    AndroidDisplay mDisplay;

    ToastUtil mToastUtil;

    @Inject
    public NovelChaptersPresenterImpl(NovelDataService mNovelDataService, AndroidDisplay display, ToastUtil toastUtil) {
        this.mNovelDataService = mNovelDataService;
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
        mLoadChaptersSubscription = mNovelDataService.getChapterList(novelModel, forceUpdate)
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
        mCheckFavoriteStatusSubscription = mNovelDataService.isFavorite(novelModel.getId())
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
        mCheckLastReadSubscription = mNovelDataService.checkLastReadBookMarkState(novelModel.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.canGoToLastRead(result);
                        },
                        error -> {
                            mView.canGoToLastRead(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            Timber.d("Check completed.", LOG_TAG);
                        }
                );
    }

    public void readChapter(NovelModel novelModel, int chapterIndex, List<NovelChapterModel> chapterList) {
        mDisplay.showNovelReadActivity(mView, novelModel, chapterIndex, 0, chapterList);
    }

    @Override
    public void readLastPosition(final NovelModel novelModel, final List<NovelChapterModel> chapterList) {
        SubscriptionUtils.checkAndUnsubscribe(mGetLastReadBookMarkSubscription);
        mGetLastReadBookMarkSubscription = mNovelDataService.getLastReadBookMark(novelModel.getId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mDisplay.showNovelReadActivity(mView, novelModel, result.getChapterIndex(), result.getChapterOffset(), chapterList);
                        },
                        error -> {
                            mView.canGoToLastRead(false);
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
        return mNovelDataService.preloadChapterContents(novel, chapterModels);
    }

    private class EmptyNovelChaptersView implements NovelChaptersView {

        @Override
        public void showChapterList(List<NovelChapterModel> chapterList) {

        }

        @Override
        public void canGoToLastRead(Boolean value) {

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
