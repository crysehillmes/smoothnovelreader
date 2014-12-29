package org.cryse.novelreader.ui;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.cocosw.bottomsheet.BottomSheet;
import com.example.android.systemuivis.SystemUiHelper;

import org.cryse.novelreader.R;
import org.cryse.novelreader.model.NovelBookMarkModel;
import org.cryse.novelreader.model.NovelChangeSrcModel;
import org.cryse.novelreader.model.NovelChapterModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelChapterContentPresenter;
import org.cryse.novelreader.qualifier.PrefsFontSize;
import org.cryse.novelreader.qualifier.PrefsScrollMode;
import org.cryse.novelreader.ui.adapter.FlipNovelChapterAdapter;
import org.cryse.novelreader.ui.adapter.ReadViewPagerAdapter;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.ui.widget.ReadWidget;
import org.cryse.novelreader.ui.widget.ReadWidgetAdapter;

import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.PreferenceConverter;
import org.cryse.novelreader.util.ToastProxy;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.gesture.SimpleGestureDetector;
import org.cryse.novelreader.util.prefs.StringPreference;
import org.cryse.novelreader.view.NovelChapterContentView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import fr.castorflex.android.smoothprogressbar.SmoothProgressBar;
import fr.castorflex.android.smoothprogressbar.SmoothProgressDrawable;

public class NovelReadViewActivity extends AbstractThemeableActivity implements NovelChapterContentView {
    ReadWidget mReadWidget;

    @InjectView(R.id.activity_read_view_widget_container)
    FrameLayout mReadWidgetContainer;

    @InjectView(R.id.activity_chapter_read_status_page_pos_textview)
    protected TextView mPagePositionTextView = null;
    @InjectView(R.id.activity_chapter_read_status_current_chapter_textview)
    protected TextView mCurrentChapterTextView = null;
    @InjectView(R.id.activity_chapter_read_status_chapter_pos_textview)
    protected TextView mChapterPositionTextView = null;

    @InjectView(R.id.activity_chapter_read_progressbar)
    SmoothProgressBar mProgressBar;

    @Inject
    NovelChapterContentPresenter mPresenter;

    @Inject
    @PrefsFontSize
    StringPreference mFontSizePref;

    @Inject
    @PrefsScrollMode
    StringPreference mScrollMode;

    Handler mHandler;

    int mFlipWidth = 0;
    int mFlipHeight = 0;
    ReadWidgetAdapter mNovelReadAdapter;

    ArrayList<NovelChapterModel> mNovelChapters;

    NovelModel novelModel;
    int chapterIndex = 0;
    int chapterOffset = 0;
    float mFontSize = 0;
    String mCurrentContent;
    boolean mIsLoading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_readview);
        requestSystemUiHelper(SystemUiHelper.LEVEL_IMMERSIVE, SystemUiHelper.FLAG_IMMERSIVE_STICKY);
        ButterKnife.inject(this);
        mHandler = new Handler();
        createReadWidget();
        mReadWidget.setOnContentRequestListener(new ReadWidget.OnContentRequestListener() {
            @Override
            public void onRequestPrevious() {
                if(!isLoading()) {
                    if(chapterIndex - 1 < 0) {
                        mReadWidget.setLoading(false);
                        return;
                    }
                    readPrevChapter();
                } else
                    mReadWidget.setLoading(false);
            }

            @Override
            public void onRequestNext() {
                if(!isLoading()) {
                    if(chapterIndex + 1 >= mNovelChapters.size()) {
                        mReadWidget.setLoading(false);
                        return;
                    }
                    readNextChapter();
                } else
                    mReadWidget.setLoading(false);
            }
        });
        mReadWidget.setOnPageChangedListener(new ReadWidget.OnPageChangedListener() {
            @Override
            public void onPageChanged(int position) {
                mPagePositionTextView.setText(String.format("本章第 %d / %d 页", position + 1, mNovelReadAdapter.getCount()));
                chapterOffset = mNovelReadAdapter.getStringOffsetFromPage(mReadWidget.getCurrentPage());
                hideSystemUI();
            }
        });
        mProgressBar.setSmoothProgressDrawableCallbacks(new SmoothProgressDrawable.Callbacks() {
            @Override
            public void onStop() {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onStart() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        });
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
    }

    private ReadWidgetAdapter createReadWidgetAdapter(float fontSize) {
        int scrollMode = PreferenceConverter.getScrollMode(mScrollMode.get());
        if (scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_VERTICAL ||
                scrollMode == PreferenceConverter.SCROLL_MODE_FLIP_HORIZONTAL)
            return new FlipNovelChapterAdapter(this, fontSize);
        else if (scrollMode == PreferenceConverter.SCROLL_MODE_VIEWPAGER_HORIZONTAL) {
            return new ReadViewPagerAdapter(this, fontSize);
        } else {
            throw new IllegalStateException("Unsupported read view scroll mode.");
        }
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
        if(savedInstanceState != null) {
            hasSavedStated = true;
            mNovelChapters = savedInstanceState.getParcelableArrayList(DataContract.NOVEL_CHAPTER_LIST_NAME);
            splitedContent = savedInstanceState.getCharSequenceArrayList(DataContract.NOVEL_CHAPTER_SPLITTED_CONTENT);
            mCurrentContent = savedInstanceState.getString(DataContract.NOVEL_CHAPTER_CONTENT);
            novelModel = savedInstanceState.getParcelable(DataContract.NOVEL_OBJECT_NAME);
            chapterIndex = savedInstanceState.getInt(DataContract.NOVEL_CHAPTER_INDEX_NAME);
            chapterOffset = savedInstanceState.getInt(DataContract.NOVEL_CHAPTER_OFFSET_NAME);
        } else {
            Intent intent = getIntent();
            mNovelChapters = new ArrayList<NovelChapterModel>(getPresenter().getChaptersState());
            novelModel = intent.getParcelableExtra(DataContract.NOVEL_OBJECT_NAME);
            chapterIndex = intent.getIntExtra(DataContract.NOVEL_CHAPTER_INDEX_NAME, 0);
            chapterOffset = intent.getIntExtra(DataContract.NOVEL_CHAPTER_OFFSET_NAME, 0);
            if(intent.hasExtra(DataContract.NOVEL_HAS_STATE)) {
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
                View testView = getLayoutInflater().inflate(R.layout.layout_chapter_content_textview, null);
                TextView textView = (TextView) testView.findViewById(R.id.layout_chapter_content_textview);
                textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mFontSize);
                TextPaint textPaint = textView.getPaint();
                mNovelReadAdapter = createReadWidgetAdapter(mFontSize);
                mReadWidget.setAdapter(mNovelReadAdapter);
                // TODO: 当修改了排版算法之后应该是用正确的值而不是这里的经验值
                getPresenter().setSplitParams(
                        mFlipWidth - padding * 2,
                        mFlipHeight,
                        1.3f,
                        0f,
                        textPaint);
                if(finalHasSavedStated) {
                    getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                            mCurrentContent);
                } else {
                    if(chapterIndex > mNovelChapters.size() - 1) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void hideSystemUI() {
        if(isSystemUiHelperAvailable())
            getSystemUiHelper().hide();
    }

    private void addClickEventListener() {
        SimpleGestureDetector simpleGestureDetector = new SimpleGestureDetector(UIUtils.getDisplayDensity(this)) {
            @Override
            public boolean onGesture(View view, int gestureId) {
                if(view == mReadWidget.getReadDisplayView() && gestureId == SimpleGestureDetector.TAP) {
                    showBottomMenu();
                    return true;
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
                .applyColorFilter(isNightMode() ? getResources().getColor(R.color.white_54_percent) : Color.BLACK)
                .grid()
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
                        case R.id.menu_bottomsheet_changesrc:
                            onMenuItemChangeSrcClick();
                            break;
                        case R.id.menu_bottomsheet_nightmode:
                            onMenuItemNightModeClick();
                            break;
                        case R.id.menu_bottomsheet_reading_settings:
                            showBottomReadingSettingsMenu();
                            break;
                    }
                    dialog.dismiss();
                });
        if(isNightMode())
            bottomSheetBuilder.darkTheme();
        BottomSheet bottomSheet = bottomSheetBuilder.show();
        bottomSheet.setOnDismissListener(dialog -> hideSystemUI());
    }

    private void showBottomReadingSettingsMenu() {
        BottomSheet.Builder bottomSheetBuilder = new BottomSheet.Builder(NovelReadViewActivity.this)
                .title(getString(R.string.bottom_sheet_title))
                .sheet(R.menu.menu_readview_read_settings)
                .applyColorFilter(isNightMode() ? getResources().getColor(R.color.white_54_percent) : Color.BLACK)
                .grid()
                .listener((dialog, which) -> {
                    switch (which) {
                        case R.id.menu_bottomsheet_textsize:
                            onMenuItemFontSizeClick();
                            break;
                        case R.id.menu_bottomsheet_pagecurl_mode:
                            onMenuItemPageCurlModeClick();
                            break;
                    }
                    dialog.dismiss();
                });
        if(isNightMode())
            bottomSheetBuilder.darkTheme();
        BottomSheet bottomSheet = bottomSheetBuilder.show();
        bottomSheet.setOnDismissListener(dialog -> hideSystemUI());
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
        mPagePositionTextView.setText(String.format("本章第 %d / %d 页", mReadWidget.getCurrentPage() + 1, mNovelReadAdapter.getCount()));
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
        mPagePositionTextView.setText(String.format("本章第 %d / %d 页", mReadWidget.getCurrentPage() + 1, mNovelReadAdapter.getCount()));
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
        mPagePositionTextView.setText(String.format("本章第 %d / %d 页", mReadWidget.getCurrentPage() + 1, mNovelReadAdapter.getCount()));
        mCurrentChapterTextView.setText(title);

    }

    @Override
    public void onBookMarkSaved(int type, boolean isSuccess) {
        if(isSuccess && type == NovelBookMarkModel.BOOKMARK_TYPE_NORMAL) {
            // TODO: 书签保存成功提示
        } else {
            // TODO: 书签保存失败提示
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
                .itemsCallback(new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        getPresenter().changeSrc(mNovelChapters.get(chapterIndex), otherSrc.get(i));
                    }
                });
        MaterialDialog dialog = dialogBuilder.build();
        dialog.setOnDismissListener(dialogInterface -> hideSystemUI());
        dialog.show();
    }

    @Override
    public void onChangeSrc(NovelChapterModel chapterModel) {
        for(NovelChapterModel chapter : mNovelChapters) {
            if(chapter.getChapterIndex() == chapterModel.getChapterIndex()) {
                chapter.setSrc(chapterModel.getSrc());
                break;
            }
        }
    }

    @Override
    public synchronized void setLoading(Boolean isLoading) {
        mIsLoading = isLoading;
        if(isLoading)
            mProgressBar.progressiveStart();
        else {
            mReadWidget.setLoading(false);
            mProgressBar.progressiveStop();
        }
    }

    @Override
    public Boolean isLoading() {
        return mIsLoading;
    }

    @Override
    public void showToast(String text, ToastType toastType) {
        ToastProxy.showToast(this, text, toastType);
    }

    private void readNextChapter() {
        if(chapterIndex + 1 >= mNovelChapters.size())
            return;
        getPresenter().loadNextChapter(mNovelChapters.get(chapterIndex + 1));
    }

    private void readPrevChapter() {
        if(chapterIndex - 1 < 0)
            return;
        getPresenter().loadPrevChapter(mNovelChapters.get(chapterIndex - 1), true);
    }

    private void saveReadHistory() {
        int currentPage = mReadWidget.getCurrentPage();
        if(mNovelChapters == null || mNovelReadAdapter == null)
            return;
        NovelBookMarkModel lastReadBookMark = new NovelBookMarkModel(
                novelModel.getId(),
                mNovelChapters.get(chapterIndex).getTitle(),
                novelModel.getTitle(),
                chapterIndex,
                mNovelReadAdapter.getStringOffsetFromPage(currentPage),
                NovelBookMarkModel.BOOKMARK_TYPE_LASTREAD,
                new Date()
        );
        getPresenter().saveLastReadBookMark(lastReadBookMark);
    }

    public NovelChapterContentPresenter getPresenter() {
        return mPresenter;
    }

    public void onMenuItemChangeSrcClick(){
        getPresenter().getOtherSrc(mNovelChapters.get(chapterIndex));
    }

    public void onMenuItemNightModeClick(){
        setNightMode(!isNightMode());
    }


    public void onMenuItemFontSizeClick(){
        String[] fontSizes = getResources().getStringArray(R.array.readview_font_size_entries);
        int index = Arrays.binarySearch(fontSizes, mFontSizePref.get());
        MaterialDialog.Builder dialogBuilder = new MaterialDialog.Builder(this)
                .title(R.string.settings_item_font_size_dialog_title)
                .items(fontSizes)
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        mFontSizePref.set(charSequence.toString());
                        mFontSize = PreferenceConverter.getFontSize(NovelReadViewActivity.this, charSequence.toString());
                        mNovelReadAdapter.setFontSize(mFontSize);
                        getPresenter().getSplitTextPainter().setTextSize(mFontSize);
                        getPresenter().splitChapterAndDisplay(mNovelChapters.get(chapterIndex).getTitle(),
                                mCurrentContent);
                    }
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
                .itemsCallbackSingleChoice(index, new MaterialDialog.ListCallback() {
                    @Override
                    public void onSelection(MaterialDialog materialDialog, View view, int i, CharSequence charSequence) {
                        if(i == index) return;
                        mScrollMode.set(pageCurlModeValues[i]);
                        mReadWidgetContainer.removeAllViews();
                        reloadTheme(true);
                    }
                })
                .positiveText(R.string.dialog_choose);
        MaterialDialog dialog = dialogBuilder.build();
        dialog.setOnDismissListener(dialogInterface -> hideSystemUI());
        dialog.show();
    }

    public void onMenuItemNextChapterClick() {
        readNextChapter();
    }

    public void onMenuItemPreviousChapterClick() {
        readPrevChapter();
    }

    public void onMenuItemReloadClick() {
        getPresenter().loadChapter(mNovelChapters.get(chapterIndex), true);
    }
}
