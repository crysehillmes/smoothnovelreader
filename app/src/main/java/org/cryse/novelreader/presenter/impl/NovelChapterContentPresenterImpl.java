package org.cryse.novelreader.presenter.impl;

import android.text.TextPaint;

import org.cryse.novelreader.logic.NovelDataService;
import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.view.NovelChapterContentView;
import org.cryse.novelreader.util.SubscriptionUtils;
import org.cryse.novelreader.util.ToastUtil;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;
import org.cryse.novelreader.util.textsplitter.PageSplitter;

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
    NovelChapterContentView mView;
    Subscription mSubscription;
    Subscription mSplitSubscription;
    Subscription mGetOtherSrcSubscription;
    NovelDataService mNovelDataService;

    AndroidDisplay mDisplay;

    ToastUtil mToastUtil;

    @Inject
    public NovelChapterContentPresenterImpl(
            NovelDataService mNovelDataService,
            AndroidDisplay display,
            ToastUtil toastUtil) {
        this.mNovelDataService = mNovelDataService;
        this.mDisplay = display;
        this.mToastUtil = toastUtil;
        this.mView = new EmptyNovelChapterContentView();
    }

    @Override
    public void loadChapter(NovelChapterModel novelChapterModel, boolean forceUpdate) {
        loadChapter(novelChapterModel, CURRENT, forceUpdate, null);
    }

    @Override
    public void loadNextChapter(NovelChapterModel novelChapterModel) {
        loadChapter(novelChapterModel, NEXT, false, null);
    }

    @Override
    public void loadPrevChapter(NovelChapterModel novelChapterModel, boolean jumpToLast) {
        loadChapter(novelChapterModel, PREV, false, jumpToLast);// 读取上一章这里的boolean 用来确定是不是自动跳到最后一页
    }

    private static final int PREV = 0;
    private static final int CURRENT = 1;
    private static final int NEXT = 2;

    private void loadChapter(final NovelChapterModel novelChapterModel, final int type, boolean forceUpdate, Boolean autoJump) {
        mView.setLoading(true);
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelDataService.getChapterContent(novelChapterModel, forceUpdate)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            splitChapterAndDisplay(novelChapterModel.getTitle(), result.getContent(), type, autoJump);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                            showEmptyContent(type, generateEmptyString(novelChapterModel.getTitle(), mToastUtil.getEmptyContentText()));
                        },
                        () -> {
                            mView.setLoading(false);
                        }
                );
    }

    public void splitChapterAndDisplay(String title, String content) {
        splitChapterAndDisplay(title, content, CURRENT, null);
    }

    private void splitChapterAndDisplay(String title, String content, int type, Boolean autoJump) {
        // 分割章节
        mSplitSubscription = splitNovelChapterAsync(title, content)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
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
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            if( content == null || error instanceof NullPointerException)
                                mToastUtil.showEmptyContentToast(mView);
                            else
                                mToastUtil.showExceptionToast(mView, error);
                            showEmptyContent(type, generateEmptyString(title, mToastUtil.getEmptyContentText()));
                        },
                        () -> {
                            mView.setLoading(false);
                        }
                );
    }


    @Override
    public void addBookMark(NovelBookMarkModel bookMarkModel) {
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelDataService.addBookMark(bookMarkModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {
                            mView.onBookMarkSaved(NovelBookMarkModel.BOOKMARK_TYPE_NORMAL, false);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.onBookMarkSaved(NovelBookMarkModel.BOOKMARK_TYPE_NORMAL, true);
                        }
                );
    }

    @Override
    public void saveLastReadBookMark(NovelBookMarkModel bookMarkModel) {
        SubscriptionUtils.checkAndUnsubscribe(mSubscription);
        mSubscription = mNovelDataService.saveLastReadBookMark(bookMarkModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                        },
                        error -> {
                            mView.onBookMarkSaved(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD, false);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            mView.onBookMarkSaved(NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD, true);
                        }
                );
    }

    @Override
    public List<NovelChapterModel> getChaptersState() {
        return mDisplay.getChaptersInRunTimeStore();
    }

    @Override
    public void removeChaptersState() {
        mDisplay.removeChaptersInRunTimeStore();
    }

    @Override
    public void saveChaptersState(List<NovelChapterModel> chapters) {
        mDisplay.saveChaptersInRunTimeStore(chapters);
    }

    @Override
    public void bindView(NovelChapterContentView view) {
        this.mView = view;
    }

    @Override
    public void unbindView() {
        bindView(new EmptyNovelChapterContentView());
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

    int mWidth;
    int mHeight;
    float mLineSpacingMultiplier;
    float mLineSpacingExtra;
    TextPaint mTextPaint;

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
    public void getOtherSrc(NovelChapterModel novelChapterModel) {
        SubscriptionUtils.checkAndUnsubscribe(mGetOtherSrcSubscription);
        mGetOtherSrcSubscription = mNovelDataService.getOtherChapterSrc(
                novelChapterModel.getId(),
                novelChapterModel.getSrc(),
                novelChapterModel.getTitle()).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.onGetOtherSrcFinished(result);
                        },
                        error -> {
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {

                        }
                );
    }

    @Override
    public void changeSrc(NovelChapterModel novelChapterModel, NovelChangeSrcModel changeSrcModel) {
        mView.setLoading(true);
        SubscriptionUtils.checkAndUnsubscribe(mGetOtherSrcSubscription);
        mGetOtherSrcSubscription = mNovelDataService.changeChapterSrc(novelChapterModel, changeSrcModel)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        result -> {
                            mView.onChangeSrc(result);
                            loadChapter(result, true);
                        },
                        error -> {
                            mView.setLoading(false);
                            Timber.e(error, error.getMessage(), LOG_TAG);
                            mToastUtil.showExceptionToast(mView, error);
                        },
                        () -> {
                            Timber.d("changeSrc onCompleted.", LOG_TAG);
                        }
                );
    }

    private void showEmptyContent(int type, String title) {
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

    private class EmptyNovelChapterContentView implements NovelChapterContentView {

        @Override
        public void showChapter(String content, List<CharSequence> splitedContent) {

        }

        @Override
        public void showNextChapter(String content, List<CharSequence> splitedContent) {

        }

        @Override
        public void showPrevChapter(String content, List<CharSequence> splitedContent, boolean jumpToLast) {

        }

        @Override
        public void onBookMarkSaved(int type, boolean isSuccess) {

        }

        @Override
        public void onGetOtherSrcFinished(List<NovelChangeSrcModel> otherSrc) {

        }

        @Override
        public void onChangeSrc(NovelChapterModel chapterModel) {

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
