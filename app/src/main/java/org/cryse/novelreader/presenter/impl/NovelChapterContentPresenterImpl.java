package org.cryse.novelreader.presenter.impl;

import android.text.TextPaint;

import org.cryse.novelreader.logic.NovelBusinessLogicLayer;
import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.util.SnackbarUtils;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.novelreader.util.textsplitter.PageSplitter;
import org.cryse.novelreader.view.NovelChapterContentView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.util.async.Async;
import timber.log.Timber;

public class NovelChapterContentPresenterImpl implements NovelChapterContentPresenter {
    private static final String LOG_TAG = NovelChapterContentPresenterImpl.class.getSimpleName();
    private static final int PREV = 0;
    private static final int CURRENT = 1;
    private static final int NEXT = 2;
    NovelChapterContentView mView;
    Subscription mSubscription;
    Subscription mSplitSubscription;
    Subscription mGetOtherSrcSubscription;
    NovelBusinessLogicLayer mNovelBusinessLogicLayer;
    AndroidNavigation mDisplay;
    SnackbarUtils mSnackbarUtils;
    int mWidth;
    int mHeight;
    float mLineSpacingMultiplier;
    float mLineSpacingExtra;
    TextPaint mTextPaint;

    @Inject
    public NovelChapterContentPresenterImpl(
            NovelBusinessLogicLayer mNovelBusinessLogicLayer,
            AndroidNavigation display,
            SnackbarUtils snackbarUtils) {
        this.mNovelBusinessLogicLayer = mNovelBusinessLogicLayer;
        this.mDisplay = display;
        this.mSnackbarUtils = snackbarUtils;
        this.mView = null;
    }

    @Override
    public void loadChapter(ChapterModel novelChapterModel, boolean forceUpdate) {
        loadChapter(novelChapterModel, CURRENT, forceUpdate, null);
    }

    @Override
    public void loadNextChapter(ChapterModel novelChapterModel) {
        loadChapter(novelChapterModel, NEXT, false, null);
    }

    @Override
    public void loadPrevChapter(ChapterModel novelChapterModel, boolean jumpToLast) {
        loadChapter(novelChapterModel, PREV, false, jumpToLast);
    }

    private void loadChapter(final ChapterModel novelChapterModel, final int type, boolean forceUpdate, Boolean autoJump) {
        mView.setLoading(true);
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelBusinessLogicLayer.getChapterContent(novelChapterModel, forceUpdate)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            splitChapterAndDisplay(novelChapterModel.getTitle(), result.getContent(), type, autoJump);
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                                mSnackbarUtils.showExceptionToast(mView, error);
                            }
                            showEmptyContent(type, generateEmptyString(novelChapterModel.getTitle(), mSnackbarUtils.getEmptyContentText()));

                        },
                        () -> {
                            if (mView != null) {
                                mView.setLoading(false);
                            }
                        }
                );
    }

    public void splitChapterAndDisplay(String title, String content) {
        splitChapterAndDisplay(title, content, CURRENT, null);
    }

    private void splitChapterAndDisplay(String title, String content, int type, Boolean autoJump) {
        mSplitSubscription = splitNovelChapterAsync(title, content)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                switch (type) {
                                    case PREV:
                                        mView.showPrevChapter(content, result, autoJump);
                                        break;
                                    case CURRENT:
                                        mView.showChapter(content, result);
                                        break;
                                    case NEXT:
                                        mView.showNextChapter(content, result);
                                        break;
                                }
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mView.setLoading(false);
                                if (content == null || error instanceof NullPointerException)
                                    mSnackbarUtils.showEmptyContentToast(mView);
                                else
                                    mSnackbarUtils.showExceptionToast(mView, error);
                            }
                            showEmptyContent(type, generateEmptyString(title, mSnackbarUtils.getEmptyContentText()));
                        },
                        () -> {

                            if (mView != null) {
                                mView.setLoading(false);
                            }
                        }
                );
    }

    @Override
    public void addBookMark(BookmarkModel bookMarkModel) {
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelBusinessLogicLayer.addBookMark(bookMarkModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {

                            if (mView != null) {
                                mView.onBookMarkSaved(BookmarkModel.BOOKMARK_TYPE_NORMAL, false);
                                mSnackbarUtils.showExceptionToast(mView, error);
                            }
                        },
                        () -> {

                            if (mView != null) {
                                mView.onBookMarkSaved(BookmarkModel.BOOKMARK_TYPE_NORMAL, true);
                            }
                        }
                );
    }

    @Override
    public void saveLastReadBookMark(BookmarkModel bookMarkModel) {
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelBusinessLogicLayer.saveLastReadBookMark(bookMarkModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {
                            if (mView != null) {
                                mView.onBookMarkSaved(BookmarkModel.BOOKMARK_TYPE_LASTREAD, false);
                                mSnackbarUtils.showExceptionToast(mView, error);
                            }
                        },
                        () -> {
                            if (mView != null) {
                                mView.onBookMarkSaved(BookmarkModel.BOOKMARK_TYPE_LASTREAD, true);
                            }
                        }
                );
    }

    @Override
    public List<ChapterModel> getChaptersState() {
        return mDisplay.getChaptersInRunTimeStore();
    }

    @Override
    public void removeChaptersState() {
        mDisplay.removeChaptersInRunTimeStore();
    }

    @Override
    public void saveChaptersState(List<ChapterModel> chapters) {
        mDisplay.saveChaptersInRunTimeStore(chapters);
    }

    @Override
    public void bindView(NovelChapterContentView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        this.mView = null;
    }

    @Override
    public void destroy() {
        this.mSubscription.unsubscribe();
    }

    private Observable<List<CharSequence>> splitNovelChapterAsync(final String title, final String content) {
        return Async.start(() -> splitNovelChapter(title, content));
    }

    private List<CharSequence> splitNovelChapter(String title, String content) {
        if (mWidth == 0 || mHeight == 0 || mTextPaint == null)
            throw new IllegalStateException("setSplitParams must be called before splitNovelChapter");
        PageSplitter pageSplitter = new PageSplitter(mWidth, mHeight, mLineSpacingMultiplier, mLineSpacingExtra);
        pageSplitter.append(title + "\n");
        pageSplitter.append(content);
        pageSplitter.split(mTextPaint);
        return pageSplitter.getPages();
    }

    public void setSplitParams(int width, int height, float lineSpacingMultiplier, float lineSpacingExtra, TextPaint textPaint) {
        this.mWidth = width;
        this.mHeight = height;
        this.mLineSpacingMultiplier = lineSpacingMultiplier;
        this.mLineSpacingExtra = lineSpacingExtra;
        this.mTextPaint = textPaint;
    }

    @Override
    public TextPaint getSplitTextPainter() {
        return mTextPaint;
    }

    @Override
    public void getOtherSrc(ChapterModel novelChapterModel) {
        SubscriptionUtils.checkAndUnsubscribe(mGetOtherSrcSubscription);
        mGetOtherSrcSubscription = mNovelBusinessLogicLayer.getOtherChapterSrc(
                novelChapterModel.getNovelId(),
                novelChapterModel.getSource(),
                novelChapterModel.getTitle()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.onGetOtherSrcFinished(result);
                            }
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if (mView != null) {
                                mSnackbarUtils.showExceptionToast(mView, error);
                            }
                        },
                        () -> {

                        }
                );
    }

    @Override
    public void changeSrc(ChapterModel novelChapterModel, NovelChangeSrcModel changeSrcModel) {
        mView.setLoading(true);
        SubscriptionUtils.checkAndUnsubscribe(mGetOtherSrcSubscription);
        mGetOtherSrcSubscription = mNovelBusinessLogicLayer.changeChapterSrc(novelChapterModel, changeSrcModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            if (mView != null) {
                                mView.onChangeSrc(result);
                                loadChapter(result, true);
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
                            Timber.d("changeSrc onCompleted.", LOG_TAG);
                        }
                );
    }

    private void showEmptyContent(int type, String title) {
        if (mView == null) return;
        List<CharSequence> charSequences = new ArrayList<>(1);
        charSequences.add(title);

        switch (type) {
            case PREV:
                mView.showPrevChapter(title, charSequences, true);
                break;
            case CURRENT:
                mView.showChapter(title, charSequences);
                break;
            case NEXT:
                mView.showNextChapter(title, charSequences);
                break;
        }
    }

    private String generateEmptyString(String title, String promptText) {
        return title + "\n" + promptText;
    }
}
