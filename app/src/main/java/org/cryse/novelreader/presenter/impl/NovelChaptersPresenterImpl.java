package org.cryse.novelreader.presenter.impl;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelChaptersPresenter;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.view.NovelChaptersView;

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

    AndroidNavigation mDisplay;

    @Inject
    public NovelChaptersPresenterImpl(NovelBusinessLogicLayer mNovelBusinessLogicLayer, AndroidNavigation display) {
        this.mNovelBusinessLogicLayer = mNovelBusinessLogicLayer;
        this.mDisplay = display;
    }

    @Override
    public void loadChapters(NovelModel novelModel, boolean hideRedundantTitle, boolean scrollToLastRead) {
        loadChapters(novelModel, false, hideRedundantTitle, scrollToLastRead);
    }

    @Override
    public void loadChapters(NovelModel novelModel, boolean forceUpdate, boolean hideRedundantTitle, boolean scrollToLastRead) {
        SubscriptionUtils.checkAndUnsubscribe(mLoadChaptersSubscription);
        mLoadChaptersSubscription = mNovelBusinessLogicLayer.getChapterList(novelModel, forceUpdate, hideRedundantTitle)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.showChapterList(result, scrollToLastRead);
                            }
                        },
                        error -> {
                            Timber.e("Load chapter list error:", error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                                // TODO: return errorCode here
                                mView.showSnackbar(0, SimpleSnackbarType.ERROR, error);
                            }
                        },
                        () -> {
                            Timber.d("Load chapter list completed!", LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                            }
                        }
                );
    }

    public void checkNovelFavoriteStatus(NovelModel novelModel) {
        SubscriptionUtils.checkAndUnsubscribe(mCheckFavoriteStatusSubscription);
        mCheckFavoriteStatusSubscription = mNovelBusinessLogicLayer.isFavoriteLocal(novelModel.getNovelId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.checkFavoriteStatusComplete(result[0], result[1]);
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
    public void bindView(NovelChaptersView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
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
        mCheckLastReadSubscription = mNovelBusinessLogicLayer.checkLastReadBookMarkState(novelModel.getNovelId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.canGoToLastRead(result);
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.canGoToLastRead(null);
                                // TODO: return errorCode here
                                mView.showSnackbar(0, SimpleSnackbarType.ERROR, error);
                            }
                        },
                        () -> {
                            Timber.d("Check completed.", LOG_TAG);
                        }
                );
    }

    public void readChapter(NovelModel novelModel, String chapterId, List<ChapterModel> chapterList) {
        mDisplay.showNovelReadActivity(mView, novelModel, chapterId, 0, chapterList);
    }

    @Override
    public void readLastPosition(final NovelModel novelModel, final List<ChapterModel> chapterList) {
        SubscriptionUtils.checkAndUnsubscribe(mGetLastReadBookMarkSubscription);
        mGetLastReadBookMarkSubscription = mNovelBusinessLogicLayer.getLastReadBookMark(novelModel.getNovelId())
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mDisplay.showNovelReadActivity(mView, novelModel, result.getChapterId(), result.getChapterOffset(), chapterList);
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.canGoToLastRead(null);
                                // TODO: return errorCode here
                                mView.showSnackbar(0, SimpleSnackbarType.ERROR, error);
                            }
                        },
                        () -> {
                            Timber.d("Get read last position completed.", LOG_TAG);
                        }
                );
    }

    @Override
    public void showNovelIntroduction(NovelModel novelModel) {
        if (mView != null) {
            mDisplay.showNovelDetailView(mView, novelModel, false);
        }
    }

    public Observable<Boolean> preloadChapterContents(NovelModel novel, List<ChapterModel> chapterModels) {
        return mNovelBusinessLogicLayer.preloadChapterContents(novel, chapterModels);
    }
}
