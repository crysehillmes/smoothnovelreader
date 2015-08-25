package org.cryse.novelreader.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentTransaction;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.android.systemuivis.SystemUiHelper;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.model.Bookmark;
import org.cryse.novelreader.model.BookmarkModel;
import org.cryse.novelreader.model.ChapterModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.qualifier.PrefsFontSize;
import org.cryse.novelreader.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.qualifier.PrefsReadBackground;
import org.cryse.novelreader.qualifier.PrefsScrollMode;
import org.cryse.novelreader.ui.adapter.ReadViewFlipAdapter;
import org.cryse.novelreader.ui.adapter.ReadViewPagerAdapter;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.ui.widget.ReadWidget;
import org.cryse.novelreader.ui.widget.ReadWidgetAdapter;
import org.cryse.novelreader.util.PreferenceConverter;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.animation.SlideInOutAnimator;
import org.cryse.novelreader.util.gesture.SimpleGestureDetector;
import org.cryse.novelreader.util.prefs.IntegerPreference;
import org.cryse.novelreader.util.prefs.StringPreference;
import org.cryse.novelreader.view.NovelChapterContentView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class NovelReadViewActivity extends AbstractThemeableActivity implements NovelChapterContentView {
    private static final String LOG_TAG = NovelReadViewActivity.class.getName();
    private static final String READ_OPTIONS_FRAGMENT_TAG = "read_options_fragment_tag";

    @Bind(R.id.activity_chapter_read_options_panel_container)
    protected FrameLayout mReadOptionsPanelContainer;
    @Bind(R.id.activity_chapter_read_status_page_pos_textview)
    protected TextView mPagePositionTextView = null;
    @Bind(R.id.activity_chapter_read_status_current_chapter_textview)
    protected TextView mCurrentChapterTextView = null;
    @Bind(R.id.activity_chapter_read_status_chapter_pos_textview)
    protected TextView mChapterPositionTextView = null;
    ReadWidget mReadWidget;
    @Bind(R.id.activity_chapter_read_container)
    RelativeLayout mRootContainer;
    @Bind(R.id.activity_read_view_widget_container)
    FrameLayout mReadWidgetContainer;
    @Bind(R.id.activity_chapter_read_status_layout)
    RelativeLayout mReadStatusContainer;
    @Bind(R.id.activity_chapter_read_progressbar)
    ProgressBar mProgressBar;

    @Inject
    NovelChapterContentPresenter mPresenter;

    @Inject
    @PrefsFontSize
    StringPreference mFontSizePref;

    @Inject
    @PrefsLineSpacing
    StringPreference mLineSpacingPreference;

    @Inject
    @PrefsScrollMode
    StringPreference mScrollMode;

    @Inject
    @PrefsReadBackground
    IntegerPreference mReadBackgroundPrefs;

    Handler mHandler;

    int mFlipWidth = 0;
    int mFlipHeight = 0;
    ReadWidgetAdapter mNovelReadAdapter;

    ArrayList<ChapterModel> mNovelChapters;

    NovelModel novelModel;
    int chapterIndex = 0;
    int chapterOffset = 0;
    float mFontSize = 0;
    float mLineSpacing = 0;
    String mCurrentContent;
    boolean mIsLoading = false;
    private ReadOptionsFragment.OnReadOptionsChangedListener mOnReadOptionsChangedListener = new ReadOptionsFragment.OnReadOptionsChangedListener() {
        @Override
        public void onCloseReadOptions() {

        }

        @Override
        public void onFontSizeChanged(String fontSizeString) {
            mFontSize = PreferenceConverter.getFontSize(NovelReadViewActivity.this, fontSizeString);
            mNovelReadAdapter.setFontSize(mFontSize);
            getPresenter().getSplitParams().getTextPaint().setTextSize(mFontSize);
            getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                    mCurrentContent);
        }

        @Override
        public void onLineSpacingChanged(String lineSpacing) {
            mLineSpacing = PreferenceConverter.getLineSpacing(lineSpacing);
            mNovelReadAdapter.setLineSpacing(mLineSpacing);
            getPresenter().getSplitParams().setLineSpacingMultiplier(mLineSpacing);
            getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                    mCurrentContent);
        }

        @Override
        public void onColorSchemaChanged() {

        }

        @Override
        public void onTraditionalChanged() {

        }

        @Override
        public void onFontChanged() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_readview);
        requestSystemUiHelper(SystemUiHelper.LEVEL_IMMERSIVE, SystemUiHelper.FLAG_IMMERSIVE_STICKY);
        ButterKnife.bind(this);
        mHandler = new Handler();
        createReadWidget();
        mReadWidget.setOnContentRequestListener(new ReadWidget.OnContentRequestListener() {
            @Override
            public void onRequestPrevious() {
                if (!isLoading()) {
                    if (chapterIndex - 1 < 0) {
                        mReadWidget.setLoading(false);
                        return;
                    }
                    goPrevChapter();
                } else
                    mReadWidget.setLoading(false);
            }

            @Override
            public void onRequestNext() {
                if (!isLoading()) {
                    if (chapterIndex + 1 >= mNovelChapters.size()) {
                        mReadWidget.setLoading(false);
                        return;
                    }
                    goNextChapter();
                } else
                    mReadWidget.setLoading(false);
            }
        });
        mReadWidget.setOnPageChangedListener(new ReadWidget.OnPageChangedListener() {
            @Override
            public void onPageChanged(int position) {
                mPagePositionTextView.setText(
                        getResources().getString(
                                R.string.readview_page_offset,
                                position + 1,
                                mNovelReadAdapter.getCount()
                        )
                );
                chapterOffset = mNovelReadAdapter.getStringOffsetFromPage(mReadWidget.getCurrentPage());
                hideSystemUI();
            }
        });
        /*mProgressBar.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStart() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });*/
        addClickEventListener();
    }

    private void createReadWidget() {
        mReadWidgetContainer.removeAllViews();
        int scrollMode = PreferenceConverter.getScrollMode(mScrollMode.get());
        if(scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_VERTICAL)
            getLayoutInflater().inflate(R.layout.layout_read_view_flipcontroller_vertical, mReadWidgetContainer);
        else if(scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_HORIZONTAL)
            getLayoutInflater().inflate(R.layout.layout_read_view_flipcontroller_horizontal, mReadWidgetContainer);
        else if(scrollMode == PreferenceConverter.SCROLL_MODE_VIEWPAGER_HORIZONTAL)
            getLayoutInflater().inflate(R.layout.layout_read_view_pager, mReadWidgetContainer);
        mReadWidget = (ReadWidget)findViewById(R.id.activity_read_view_readwidget);
        setReadBackgroundColor();
    }

    private ReadWidgetAdapter createReadWidgetAdapter() {
        int scrollMode = PreferenceConverter.getScrollMode(mScrollMode.get());
        if (scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_VERTICAL ||
                scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_HORIZONTAL)
            return new ReadViewFlipAdapter(this, mFontSize, mLineSpacing, isNightMode() ? getResources().getColor(R.color.theme_read_bg_color_white) : mReadBackgroundPrefs.get());
        else if (scrollMode == PreferenceConverter.SCROLL_MODE_VIEWPAGER_HORIZONTAL) {
            return new ReadViewPagerAdapter(this, mFontSize, mLineSpacing, isNightMode() ? getResources().getColor(R.color.theme_read_bg_color_white) : mReadBackgroundPrefs.get());
        } else {
            throw new IllegalStateException("Unsupported read view scroll mode.");
        }
    }

    public void setReadBackgroundColor() {
        mRootContainer.setBackgroundColor(isNightMode() ? getResources().getColor(R.color.theme_read_bg_color_white) : mReadBackgroundPrefs.get());
        if(mNovelReadAdapter != null)
            mNovelReadAdapter.setBackgroundColor(isNightMode() ? getResources().getColor(R.color.theme_read_bg_color_white) : mReadBackgroundPrefs.get());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        boolean hasSavedStated = false;
        List<CharSequence> splitedContent = null;
        if (savedInstanceState != null) {
            hasSavedStated = true;
            mNovelChapters = savedInstanceState.getParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME);
            splitedContent = savedInstanceState.getCharSequenceArrayList(DataContract.NOVEL_CHAPTER_SPLITTED_CONTENT);
            mCurrentContent = savedInstanceState.getString(DataContract.NOVEL_CHAPTER_CONTENT);
            novelModel = savedInstanceState.getParcelable(DataContract.NOVEL_OBJECT_NAME);
            chapterIndex = savedInstanceState.getInt(DataContract.NOVEL_CHAPTER_INDEX_NAME);
            chapterOffset = savedInstanceState.getInt(DataContract.NOVEL_CHAPTER_OFFSET_NAME);
        } else {
            Intent intent = getIntent();
            mNovelChapters = new ArrayList<ChapterModel>(getPresenter().getChaptersState());
            novelModel = intent.getParcelableExtra(DataContract.NOVEL_OBJECT_NAME);
            String startChapterId = intent.getStringExtra(DataContract.NOVEL_CHAPTER_ID_NAME);
            chapterIndex = findChapterIndex(startChapterId);
            chapterOffset = intent.getIntExtra(DataContract.NOVEL_CHAPTER_OFFSET_NAME, 0);
            if (intent.hasExtra(DataContract.NOVEL_HAS_STATE)) {
                hasSavedStated = true;
                splitedContent = intent.getCharSequenceArrayListExtra(DataContract.NOVEL_CHAPTER_SPLITTED_CONTENT);
                mCurrentContent = intent.getStringExtra(DataContract.NOVEL_CHAPTER_CONTENT);
            }
        }
        ViewTreeObserver vto = mReadWidget.getReadDisplayView().getViewTreeObserver();
        Resources resources = getResources();
        final int padding = resources.getDimensionPixelSize(R.dimen.read_textview_padding);
        assert vto != null;
        final boolean finalHasSavedStated = hasSavedStated;
        final List<CharSequence> finalSplitedContent = splitedContent;
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                ViewTreeObserver obs = mReadWidget.getReadDisplayView().getViewTreeObserver();

                mFlipWidth = mReadWidget.getReadDisplayView().getWidth();
                mFlipHeight = mReadWidget.getReadDisplayView().getHeight();
                mFontSize = PreferenceConverter.getFontSize(NovelReadViewActivity.this, mFontSizePref.get());
                mLineSpacing = PreferenceConverter.getLineSpacing(mLineSpacingPreference.get());
                View testView = getLayoutInflater().inflate(R.layout.layout_chapter_content_textview, null);
                TextView textView = (TextView) testView.findViewById(R.id.layout_chapter_content_textview);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
                TextPaint textPaint = textView.getPaint();
                mNovelReadAdapter = createReadWidgetAdapter();
                mReadWidget.setAdapter(mNovelReadAdapter);
                getPresenter().setSplitParams(
                        new NovelChapterContentPresenter.TextSplitParam(
                                mFlipWidth - padding * 2,
                                mFlipHeight,
                                mLineSpacing,
                                0f,
                                textPaint
                        )
                );
                if (finalHasSavedStated) {
                    getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                            mCurrentContent);
                } else {
                    if (chapterIndex > mNovelChapters.size() - 1) {
                        chapterIndex = 0;
                        chapterOffset = 0;
                    }
                    getPresenter().loadChapter(mNovelChapters.get(chapterIndex), false);
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    obs.removeOnGlobalLayoutListener(this);
                } else {
                    obs.removeGlobalOnLayoutListener(this);
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(DataContract.NOVEL_HAS_STATE, true);
        outState.putParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME, mNovelChapters);
        outState.putCharSequenceArrayList(DataContract.NOVEL_CHAPTER_SPLITTED_CONTENT,
                mNovelReadAdapter.getContent());
        outState.putString(DataContract.NOVEL_CHAPTER_CONTENT, mCurrentContent);
        outState.putParcelable(DataContract.NOVEL_OBJECT_NAME, novelModel);
        outState.putInt(DataContract.NOVEL_CHAPTER_INDEX_NAME, chapterIndex);
        outState.putInt(DataContract.NOVEL_CHAPTER_OFFSET_NAME, chapterOffset);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSystemUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(this).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    @Override
    public void finish() {
        super.finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveReadHistory();
    }

    @Override
    protected void onStart() {
        super.onStart();
        getPresenter().bindView(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getPresenter().unbindView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void hideSystemUI() {
        if(isSystemUiHelperAvailable())
            getSystemUiHelper().hide();
    }

    private void addClickEventListener() {
        SimpleGestureDetector simpleGestureDetector = new SimpleGestureDetector(UIUtils.getDisplayDensity(this)) {
            @Override
            public boolean onGesture(View view, int gestureId, MotionEvent motionEvent) {
                if(view == mReadWidget.getReadDisplayView() && gestureId == SimpleGestureDetector.TAP) {
                    int viewWidth = view.getWidth();
                    int viewHeight = view.getHeight();
                    int x = (int)motionEvent.getX();
                    int y = (int)motionEvent.getY();
                    if((x > (viewWidth * 1 / 3) && x < (viewWidth * 2 / 3)) && (y > (viewHeight * 1 / 3) && y < (viewHeight * 2 / 3))) {
                        showBottomMenu();
                        return true;
                    } else if((x > 0 && x < (viewWidth * 1 / 3))) {
                        goPrevPage();
                        return true;
                    } else if((x > (viewWidth * 2 / 3) && x < viewWidth)) {
                        goNextPage();
                        return true;
                    }
                }
                return false;
            }

            @Override
            public boolean onTouchResult(View view, MotionEvent motionEvent) {
                return false;
            }
        };

        mReadWidget.getReadDisplayView().setOnTouchListener(simpleGestureDetector);
    }

    private void showBottomMenu() {
        BottomSheet.Builder bottomSheetBuilder = new BottomSheet.Builder(NovelReadViewActivity.this)
                .title(getString(R.string.bottom_sheet_title))
                .sheet(R.menu.menu_readview)
                .grid()
                .limit(R.integer.readview_bottom_sheet_item_limit)
                .listener((dialog, which) -> {
                    switch (which) {
                        case R.id.menu_bottomsheet_chapter_prev:
                            onMenuItemPreviousChapterClick();
                            break;
                        case R.id.menu_bottomsheet_reload:
                            onMenuItemReloadClick();
                            break;
                        case R.id.menu_bottomsheet_chapter_next:
                            onMenuItemNextChapterClick();
                            break;
                        /*case R.id.menu_bottomsheet_back:
                            finish();
                            break;*/
                        case R.id.menu_bottomsheet_changesrc:
                            showReadOptionsPanel();
                            // onMenuItemChangeSrcClick();
                            break;
                        case R.id.menu_bottomsheet_nightmode:
                            onMenuItemNightModeClick();
                            break;
                        case R.id.menu_bottomsheet_textsize:
                            onMenuItemFontSizeClick();
                            break;
                        case R.id.menu_bottomsheet_pagecurl_mode:
                            onMenuItemPageCurlModeClick();
                            break;
                        case R.id.menu_bottomsheet_background_color:
                            if (isNightMode())
                                showSnackbar(getString(R.string.toast_menu_not_available_in_night_mode), SimpleSnackbarType.INFO);
                            else
                                onMenuItemChooseReadBackground();
                            break;
                    }
                    dialog.dismiss();
                });
        BottomSheet bottomSheet = bottomSheetBuilder.show();
        bottomSheet.setOnDismissListener(dialog -> mHandler.postDelayed(() -> hideSystemUI(), 1500));
    }

    private synchronized void setViewContent(String title, List<CharSequence> content) {
        mNovelReadAdapter.replaceContent(content);
        mNovelReadAdapter.notifyDataSetChanged();
        mReadWidget.getReadDisplayView().invalidate();
        //mFlipView.setAdapter(mFlipNovelChapterAdapter);
        float total = mNovelChapters.size();
        float value = chapterIndex + 1;
        float percentage = value / total;
        mChapterPositionTextView.setText(String.format("%.2f %%", percentage * 100.0f));
        mCurrentChapterTextView.setText(title);
    }

    @Override
    public void showChapter(String content, List<CharSequence> splitedContent) {
        mCurrentContent = content;
        String title = mNovelChapters.get(chapterIndex).getTitle();
        setViewContent(title, splitedContent);
        mReadWidget.setCurrentPage(mNovelReadAdapter.getPageFromStringOffset(chapterOffset), false);
        mPagePositionTextView.setText(
                getResources().getString(
                        R.string.readview_page_offset,
                        mReadWidget.getCurrentPage() + 1,
                        mNovelReadAdapter.getCount()
                )
        );
        mCurrentChapterTextView.setText(title);
    }

    @Override
    public void showNextChapter(String content, List<CharSequence> splitedContent) {
        mCurrentContent = content;
        chapterIndex++;
        String title = mNovelChapters.get(chapterIndex).getTitle();
        setViewContent(title, splitedContent);
        chapterOffset = 0;
        mReadWidget.setCurrentPage(0, false);
        mPagePositionTextView.setText(
                getResources().getString(
                        R.string.readview_page_offset,
                        mReadWidget.getCurrentPage() + 1,
                        mNovelReadAdapter.getCount()
                )
        );
        mCurrentChapterTextView.setText(title);
    }

    @Override
    public void showPrevChapter(String content, List<CharSequence> splitedContent, boolean jumpToLast) {
        mCurrentContent = content;
        chapterIndex--;
        String title = mNovelChapters.get(chapterIndex).getTitle();
        setViewContent(title, splitedContent);
        chapterOffset = mNovelReadAdapter.getStringOffsetFromPage(mNovelReadAdapter.getCount() - 1);
        if(jumpToLast)
            mReadWidget.setCurrentPage(mNovelReadAdapter.getCount() - 1, false);
        else
            mReadWidget.setCurrentPage(0, false);
        mPagePositionTextView.setText(
                getResources().getString(
                        R.string.readview_page_offset,
                        mReadWidget.getCurrentPage() + 1,
                        mNovelReadAdapter.getCount()
                )
        );
        mCurrentChapterTextView.setText(title);

    }

    @Override
    public void onBookMarkSaved(int type, boolean isSuccess) {
        if(isSuccess && type == BookmarkModel.BOOKMARK_TYPE_NORMAL) {
        } else {
        }
    }

    @Override
    public void onGetOtherSrcFinished(List<NovelChangeSrcModel> otherSrc) {
        String[] srcs = new String[otherSrc.size()];
        for(int i = 0; i < otherSrc.size(); i++) {
            String fullUrl = otherSrc.get(i).getSrc();
            String domain;
            if(fullUrl.length() > 8 && fullUrl.indexOf("/", 7) < fullUrl.length())
                domain = fullUrl.substring(0, fullUrl.indexOf("/", 7));
            else
                domain = fullUrl;
            srcs[i] = domain;
        }

        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.bottom_sheet_item_change_src)
                .items(srcs)
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                .itemsCallback((materialDialog, view, selection, charSequence) -> getPresenter().changeSrc(mNovelChapters.get(chapterIndex), otherSrc.get(selection)));
        MaterialDialog dialog = dialogBuilder.build();
        dialog.setOnDismissListener(dialogInterface -> hideSystemUI());
        dialog.show();
    }

    @Override
    public void onChangeSrc(ChapterModel chapterModel) {
        for(ChapterModel chapter : mNovelChapters) {
            if(chapter.getChapterIndex() == chapterModel.getChapterIndex()) {
                chapter.setSource(chapterModel.getSource());
                break;
            }
        }
    }

    @Override
    public synchronized void setLoading(Boolean isLoading) {
        mIsLoading = isLoading;
        if(isLoading)
            mProgressBar.animate()
                    .alpha(1.0f)
                    .setDuration(300);
        else {
            mReadWidget.setLoading(false);
            mProgressBar.animate()
                    .alpha(0.0f)
                    .setDuration(300);
        }
    }

    @Override
    public Boolean isLoading() {
        return mIsLoading;
    }

    private void goNextChapter() {
        if(chapterIndex + 1 >= mNovelChapters.size())
            return;
        getPresenter().loadNextChapter(mNovelChapters.get(chapterIndex + 1));
    }

    private void goPrevChapter() {
        if(chapterIndex - 1 < 0)
            return;
        getPresenter().loadPrevChapter(mNovelChapters.get(chapterIndex - 1), true);
    }

    private void goNextPage() {
        int currentPage = mReadWidget.getCurrentPage();
        int pageCount = mReadWidget.getPageCount();
        if(currentPage + 1 >= 0 && currentPage + 1 < pageCount) {
            mReadWidget.setCurrentPage(currentPage + 1, true);
        } else if(currentPage + 1 >= pageCount) {
            goNextChapter();
        }
    }

    private void goPrevPage() {
        int currentPage = mReadWidget.getCurrentPage();
        int pageCount = mReadWidget.getPageCount();
        if(currentPage - 1 >= 0 && currentPage - 1 < pageCount) {
            mReadWidget.setCurrentPage(currentPage - 1, true);
        } else if(currentPage - 1 < 0) {
            goPrevChapter();
        }
    }

    private void saveReadHistory() {
        int currentPage = mReadWidget.getCurrentPage();
        if(mNovelChapters == null || mNovelReadAdapter == null)
            return;
        BookmarkModel lastReadBookMark = new Bookmark(
                novelModel.getNovelId(),
                mNovelChapters.get(chapterIndex).getChapterId(),
                novelModel.getTitle(),
                mNovelChapters.get(chapterIndex).getTitle(),
                mNovelReadAdapter.getStringOffsetFromPage(currentPage),
                BookmarkModel.BOOKMARK_TYPE_LASTREAD,
                new Date()
        );
        getPresenter().saveLastReadBookMark(lastReadBookMark);
    }

    public NovelChapterContentPresenter getPresenter() {
        return mPresenter;
    }

    public void onMenuItemChangeSrcClick() {
        if(checkIfLocal(chapterIndex))
            getPresenter().getOtherSrc(mNovelChapters.get(chapterIndex));
    }

    public void onMenuItemNightModeClick() {
        setNightMode(!isNightMode());
    }

    public void onMenuItemFontSizeClick(){
        String[] fontSizes = getResources().getStringArray(R.array.readview_font_size_entries);
        int index = Arrays.binarySearch(fontSizes, mFontSizePref.get());
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.settings_item_font_size_dialog_title)
                .items(fontSizes)
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                .itemsCallbackSingleChoice(index, (materialDialog, view, selection, charSequence) -> {
                    mFontSizePref.set(charSequence.toString());
                    mFontSize = PreferenceConverter.getFontSize(NovelReadViewActivity.this, charSequence.toString());
                    mNovelReadAdapter.setFontSize(mFontSize);
                    //getPresenter().getSplitTextPainter().setTextSize(mFontSize);
                    getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                            mCurrentContent);
                    return true;
                })
                .positiveText(R.string.dialog_choose);
        MaterialDialog dialog = dialogBuilder.build();
        dialog.setOnDismissListener(dialogInterface -> hideSystemUI());
        dialog.show();
    }

    public void onMenuItemPageCurlModeClick(){

        String[] pageCurlModes = getResources().getStringArray(R.array.scroll_mode_string_entries);
        String[] pageCurlModeValues = getResources().getStringArray(R.array.scroll_mode_value_entries);
        String currentMode = mScrollMode.get();
        int index = Arrays.binarySearch(pageCurlModeValues, currentMode);
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.settings_item_scroll_mode_title)
                .items(pageCurlModes)
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                .itemsCallbackSingleChoice(index, (materialDialog, view, selection, charSequence) -> {
                    if(selection == index) return false;
                    mScrollMode.set(pageCurlModeValues[selection]);
                    mReadWidgetContainer.removeAllViews();
                    reloadTheme(true);
                    return true;
                })
                .positiveText(R.string.dialog_choose);
        MaterialDialog dialog = dialogBuilder.build();
        dialog.setOnDismissListener(dialogInterface -> hideSystemUI());
        dialog.show();
    }

    public void onMenuItemNextChapterClick() {
        goNextChapter();
    }

    public void onMenuItemPreviousChapterClick() {
        goPrevChapter();
    }

    public void onMenuItemReloadClick() {
        if(checkIfLocal(chapterIndex))
            getPresenter().loadChapter(mNovelChapters.get(chapterIndex), true);
    }

    public void onMenuItemChooseReadBackground() {
        new ColorChooserDialog()
                .setColors(this, R.array.read_bg_colors)
                .show(this, mReadBackgroundPrefs.get(), (index, color, darker) -> {
                    mReadBackgroundPrefs.set(color);
                    setReadBackgroundColor();
                });
    }

    private int findChapterIndex(String chapterId) {
        for (int i = 0; i < mNovelChapters.size(); i++) {
            ChapterModel chapterModel = mNovelChapters.get(i);
            if (chapterModel.getChapterId().equals(chapterId)) {
                return i;
            }
        }
        throw new IllegalStateException("ChapterIndex not found.");
    }

    private boolean checkIfLocal(int chapterIndex) {
        return mNovelChapters.get(chapterIndex).getSource().contains("://");
    }

    private void showReadOptionsPanel() {
        ReadOptionsFragment readOptionsFragment = (ReadOptionsFragment) getSupportFragmentManager().findFragmentByTag(READ_OPTIONS_FRAGMENT_TAG);
        if (readOptionsFragment == null) {

            readOptionsFragment = ReadOptionsFragment.newInstance(mOnReadOptionsChangedListener);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.activity_chapter_read_options_panel_container, readOptionsFragment, READ_OPTIONS_FRAGMENT_TAG);
            fragmentTransaction.commit();
        }
        if (!mReadOptionsPanelContainer.isShown()) {
            final ReadOptionsFragment finalReadOptionsFragment = readOptionsFragment;
            DisplayMetrics metrics = getResources().getDisplayMetrics();
            SlideInOutAnimator.slideInToTop(this, mReadOptionsPanelContainer,
                    metrics.heightPixels - getResources().getDimensionPixelSize(R.dimen.read_options_height),
                    0,
                    0f,
                    1f,
                    true,
                    () -> {
                        //finalSearchFragment.search(string);
                    });
        } else {
            //readOptionsFragment.search(string);
        }
    }

    @Override
    public void onBackPressed() {
        if (mReadOptionsPanelContainer.isShown()) {
            ReadOptionsFragment readOptionsFragment = (ReadOptionsFragment) getSupportFragmentManager().findFragmentByTag(READ_OPTIONS_FRAGMENT_TAG);

            DisplayMetrics metrics = getResources().getDisplayMetrics();
            if (readOptionsFragment != null) {
                SlideInOutAnimator.slideOutToButtom(
                        this,
                        mReadOptionsPanelContainer,
                        0,
                        metrics.heightPixels - getResources().getDimensionPixelSize(R.dimen.read_options_height),
                        1f,
                        0f,
                        true,
                        () -> {
                            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.remove(readOptionsFragment);
                            fragmentTransaction.commit();
                        });
            } else {
                SlideInOutAnimator.slideOutToButtom(
                        this,
                        mReadOptionsPanelContainer,
                        0,
                        metrics.heightPixels - getResources().getDimensionPixelSize(R.dimen.read_options_height),
                        1f,
                        0f,
                        true, null);
            }
            return;
        }
        super.onBackPressed();
    }
}
