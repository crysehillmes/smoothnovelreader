package org.cryse.novelreader.ui;

import android.animation.ObjectAnimator;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.view.ViewCompat;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.service.ChapterContentsCacheService;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.DataContract;
import org.cryse.novelreader.util.ToastProxy;
import org.cryse.novelreader.util.ToastType;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.view.NovelDetailView;
import org.cryse.widget.CheckableFrameLayout;
import org.cryse.widget.ObservableScrollView;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import timber.log.Timber;


public class NovelDetailActivity extends AbstractThemeableActivity implements NovelDetailView, ObservableScrollView.Callbacks {
    private static final String LOG_TAG = NovelDetailActivity.class.getName();
    // Constant
    private static final float PHOTO_ASPECT_RATIO = 1.7777777f;
    private static final float GAP_FILL_DISTANCE_MULTIPLIER = 1.5f;


    // View field
    @InjectView(R.id.scroll_view)
    ObservableScrollView mScrollView;

    @InjectView(R.id.scroll_view_child)
    FrameLayout mScrollViewChild;

    @InjectView(R.id.session_photo_container)
    FrameLayout mPhotoViewContainer;

    @InjectView(R.id.session_photo)
    ImageView mPhotoView;

    @InjectView(R.id.details_container)
    LinearLayout mDetailsContainer;

    @InjectView(R.id.novel_brief_info_layout)
    LinearLayout mBriefInfoLayout;

    @InjectView(R.id.detail_status_textview)
    TextView mStatusTextView;

    @InjectView(R.id.detail_chapter_count_textview)
    TextView mChapterCountTextView;

    @InjectView(R.id.detail_latest_chapter_textview)
    TextView mLatestChapterTextView;

    @InjectView(R.id.detail_abstract_header)
    TextView mAbstractHeaderTextView;

    @InjectView(R.id.novel_abstract)
    TextView mAbstractTextView;

    @InjectView(R.id.novel_tags_scrollview)
    HorizontalScrollView mTagsScrollView;

    @InjectView(R.id.novel_tags_container)
    LinearLayout mTagsContainer;

    @InjectView(R.id.detail_achieve_block)
    LinearLayout mAchieveLayout;

    @InjectView(R.id.detail_achieve_header)
    TextView mAchieveHeaderTextView;

    @InjectView(R.id.detail_achieves_container)
    LinearLayout mAchievesContainer;

    @InjectView(R.id.header_novel)
    LinearLayout mHeaderBox;

    @InjectView(R.id.novel_title)
    TextView mTitleTextView;

    @InjectView(R.id.novel_subtitle)
    TextView mSubtitleTextView;

    @InjectView(R.id.add_novel_button)
    CheckableFrameLayout mAddNovelButton;

    @InjectView(R.id.detail_loading_progress)
    ProgressBar mLoadingProgress;

    private float mMaxHeaderElevation;
    private float mFABElevation;

    private int mTagColorDotSize;

    private Handler mHandler;

    private int mPhotoHeightPixels;
    private int mHeaderHeightPixels;
    private int mAddNovelButtonHeightPixels;

    private boolean mHasPhoto;

    private String mTitleString;
    private int mDetailPrimaryColor;
    private boolean mHasSummaryContent = false;

    @Inject
    NovelDetailPresenter mPresenter;

    MenuItem mStartReadingMenuItem;

    private NovelModel mNovel;
    private NovelDetailModel mNovelDetail;
    private boolean mIsFavorited = false;
    private boolean mShowStartReadingButton = true;

    ServiceConnection mBackgroundServiceConnection;
    private ChapterContentsCacheService.ChapterContentsCacheBinder mServiceBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        /*UIUtils.tryTranslateHttpIntent(this);
        BeamUtils.tryUpdateIntentFromBeam(this);*/
        // requestWindowFeature(Window.FEATURE_ACTION_BAR);
        // requestWindowFeature(Window.FEATURE_ACTION_BAR_OVERLAY);

        if (shouldBeFloatingWindow()) {
            setupFloatingWindow();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novel_detail);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        ButterKnife.inject(this);

        mBackgroundServiceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                mServiceBinder = (ChapterContentsCacheService.ChapterContentsCacheBinder) service;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                mServiceBinder = null;
            }
        };

        getSupportActionBar().setBackgroundDrawable(null);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if(shouldBeFloatingWindow()) {
            getToolbar().setNavigationIcon(R.drawable.ic_ab_close);
        }
        setPreLShadowVisibility(false);
        if (savedInstanceState == null) {
            Uri sessionUri = getIntent().getData();
            /*BeamUtils.setBeamSessionUri(this, sessionUri);*/
        }
        setTitle("");

        final Intent intent = getIntent();
        mNovel = intent.getParcelableExtra(DataContract.NOVEL_OBJECT_NAME);
        if(intent.hasExtra(DataContract.NOVEL_INTRODUCTION_CONTRACT_SHOW_START_READING)) {
            mShowStartReadingButton = intent.getBooleanExtra(DataContract.NOVEL_INTRODUCTION_CONTRACT_SHOW_START_READING, true);
        }
        if(savedInstanceState != null && savedInstanceState.containsKey("novel_detail_object")) {
            mNovelDetail = savedInstanceState.getParcelable("novel_detail_object");
            setLoading(false);
        }
        mFABElevation = getResources().getDimensionPixelSize(R.dimen.fab_elevation);
        mMaxHeaderElevation = getResources().getDimensionPixelSize(
                R.dimen.novel_detail_max_header_elevation);

        mTagColorDotSize = getResources().getDimensionPixelSize(R.dimen.tag_color_dot_size);

        mHandler = new Handler();

        mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }
        mAddNovelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mServiceBinder != null && mServiceBinder.isCaching() && mServiceBinder.getCurrentCachingNovelId() != null) {
                    String currentCachingNovelId = mServiceBinder.getCurrentCachingNovelId();
                    if(mIsFavorited && currentCachingNovelId.compareTo(mNovel.getId()) == 0) {
                        ToastProxy.showToast(NovelDetailActivity.this, getString(R.string.toast_chapter_contents_caching_cannot_delete, mNovel.getTitle()), ToastType.TOAST_ALERT);
                        return;
                    }
                }
                if(mIsFavorited && mServiceBinder != null) {
                   if(mServiceBinder.removeFromQueueIfExist(mNovel.getId())) {
                       ToastProxy.showToast(NovelDetailActivity.this, getString(R.string.notification_action_chapter_contents_cancel_novel, mNovel.getTitle()), ToastType.TOAST_INFO);
                   }
                }
                boolean isFavorite = !mIsFavorited;
                showFavorited(isFavorite, true);
                getPresenter().addOrRemoveFavorite(mNovel, isFavorite);
                //helper.setSessionStarred(mSessionUri, starred, mTitleString);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mAddNovelButton.announceForAccessibility(isFavorite ?
                            getString(R.string.novel_details_a11y_novel_added) :
                            getString(R.string.novel_details_a11y_novel_removed));
                }
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mNovelDetail != null)
            outState.putParcelable("novel_detail_object", mNovelDetail);
    }

    /*@Override
    public Intent getParentActivityIntent() {
        // TODO: make this Activity navigate up to the right screen depending on how it was launched
        return new Intent(this, MyScheduleActivity.class);
    }*/

    private void setupFloatingWindow() {
        // configure this Activity as a floating window, dimming the background
        WindowManager.LayoutParams params = getWindow().getAttributes();
        params.width = getResources().getDimensionPixelSize(R.dimen.novel_details_floating_width);
        params.height = getResources().getDimensionPixelSize(R.dimen.novel_details_floating_height);
        params.alpha = 1;
        params.dimAmount = 0.7f;
        params.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        getWindow().setAttributes(params);
    }

    private boolean shouldBeFloatingWindow() {
        Resources.Theme theme = getTheme();
        TypedValue floatingWindowFlag = new TypedValue();
        if (theme == null || !theme.resolveAttribute(R.attr.isFloatingWindow, floatingWindowFlag, true)) {
            // isFloatingWindow flag is not defined in theme
            return false;
        }
        return (floatingWindowFlag.data != 0);
    }

    private void recomputePhotoAndScrollingMetrics() {
        final int actionBarSize = UIUtils.calculateActionBarSize(getThemedContext());
        final int statusBarSize = UIUtils.calculateStatusBarSize(getThemedContext());
        mHeaderHeightPixels = mHeaderBox.getHeight();

        mPhotoHeightPixels = 0 ;
        if (mHasPhoto) {
            mPhotoHeightPixels = (int) (mPhotoView.getWidth() / PHOTO_ASPECT_RATIO);
            mPhotoHeightPixels = Math.min(mPhotoHeightPixels, mScrollView.getHeight() * 2 / 3);
        }

        ViewGroup.LayoutParams lp;
        lp = mPhotoViewContainer.getLayoutParams();
        if (lp.height != mPhotoHeightPixels) {
            lp.height = mPhotoHeightPixels;
            mPhotoViewContainer.setLayoutParams(lp);
        }

        ViewGroup.MarginLayoutParams mlp = (ViewGroup.MarginLayoutParams)
                mDetailsContainer.getLayoutParams();
        if (mlp.topMargin != mHeaderHeightPixels + mPhotoHeightPixels) {
            mlp.topMargin = mHeaderHeightPixels + mPhotoHeightPixels;
            mDetailsContainer.setLayoutParams(mlp);
        }

        onScrollChanged(0, 0); // trigger scroll handling
    }

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().bindView(this);
        Intent service = new Intent(this.getApplicationContext(), ChapterContentsCacheService.class);
        this.bindService(service, mBackgroundServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        getPresenter().unbindView();
        this.unbindService(mBackgroundServiceConnection);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getPresenter().destroy();
        if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }
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

    private ViewTreeObserver.OnGlobalLayoutListener mGlobalLayoutListener
            = new ViewTreeObserver.OnGlobalLayoutListener() {
        @Override
        public void onGlobalLayout() {
            mAddNovelButtonHeightPixels = mAddNovelButton.getHeight();
            recomputePhotoAndScrollingMetrics();
        }
    };

    @Override
    public void onScrollChanged(int deltaX, int deltaY) {
// Reposition the header bar -- it's normally anchored to the top of the content,
        // but locks to the top of the screen on scroll
        int scrollY = mScrollView.getScrollY();

        float newTop = Math.max(mPhotoHeightPixels, scrollY);
        mHeaderBox.setTranslationY(newTop);
        mAddNovelButton.setTranslationY(newTop + mHeaderHeightPixels
                - mAddNovelButtonHeightPixels / 2);
        mLoadingProgress.setTranslationY(newTop + mHeaderHeightPixels
                - mAddNovelButtonHeightPixels / 2);

        float gapFillProgress = 1;
        if (mPhotoHeightPixels != 0) {
            gapFillProgress = Math.min(Math.max(UIUtils.getProgress(scrollY,
                    0,
                    mPhotoHeightPixels), 0), 1);
        }

        ViewCompat.setElevation(mHeaderBox, gapFillProgress * mMaxHeaderElevation);
        ViewCompat.setElevation(mAddNovelButton, gapFillProgress * mMaxHeaderElevation
                + mFABElevation);

        // Move background photo (parallax effect)
        mPhotoViewContainer.setTranslationY(scrollY * 0.5f);
    }

    @Override
    public void onResume() {
        super.onResume();
        loadNovelDetail();
        getPresenter().checkNovelFavoriteStatus(mNovel);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_novel_detail, menu);
        mStartReadingMenuItem = menu.findItem(R.id.menu_item_start_read);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(mStartReadingMenuItem != null)
            mStartReadingMenuItem.setVisible(mShowStartReadingButton);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.menu_item_start_read:
                getPresenter().startReading(mNovel);
                //getActivity().finish();
                break;
            case R.id.menu_item_detail_refresh:
                mNovelDetail = null;
                loadNovelDetail();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showNovelInformation() {
        mDetailPrimaryColor = ColorUtils.getPreDefinedColorFromId(getThemedContext(), mNovel.getId(), mNovel.getTitle().length());
        mTitleString = mNovel.getTitle();

        if (mDetailPrimaryColor == 0) {
            // no color -- use default
            mDetailPrimaryColor = getResources().getColor(R.color.primary_color);
        } else {
            // make sure it's opaque
            mDetailPrimaryColor = UIUtils.setColorAlpha(mDetailPrimaryColor, 255);
        }

        mHeaderBox.setBackgroundColor(mDetailPrimaryColor);
        getLUtils().setStatusBarColor(
                UIUtils.scaleColor(mDetailPrimaryColor, 0.8f, false));

        String subtitle = mNovel.getAuthor();
        mTitleTextView.setText(mTitleString);
        mSubtitleTextView.setText(subtitle);


        mPhotoViewContainer.setBackgroundColor(UIUtils.scaleSessionColorToDefaultBG(mDetailPrimaryColor));
        //mPhotoView.setImageResource(chooseBackgroundPhoto());
        //PicassoHelper.load(this.getActivity(), "http://www.apache.org/foundation/images/bitcoin_logo.png", mPhotoView);
        mHasPhoto = false;
        recomputePhotoAndScrollingMetrics();

        postOnScroll();
    }

    private void loadNovelDetail() {
        showNovelInformation();
        if(mNovelDetail != null)
            showNovelDetailInformation();
        else {
            getPresenter().loadNovelDetail(mNovel);
        }
    }

    private void postOnScroll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onScrollChanged(0, 0); // trigger scroll handling
                mScrollViewChild.setVisibility(View.VISIBLE);
                //mAbstract.setTextIsSelectable(true);
            }
        });
    }

    private void showFavorited(boolean isFavorited, boolean allowAnimate) {
        mIsFavorited = isFavorited;

        mAddNovelButton.setChecked(mIsFavorited, allowAnimate);

        ImageView iconView = (ImageView) mAddNovelButton.findViewById(R.id.add_schedule_icon);
        getLUtils().setOrAnimatePlusCheckIcon(
                iconView, isFavorited, allowAnimate);
        mAddNovelButton.setContentDescription(getString(isFavorited
                ? R.string.remove_from_bookshelf_description
                : R.string.add_to_bookshelf_description));
    }

    private void showNovelDetailInformation() {
        setDetailSectionHeaderColor();
        mStatusTextView.setText(getString(R.string.novel_detail_status_fomat, mNovel.getStatus()));
        if(mNovelDetail.getChapterNumber() <= 0) {
            mChapterCountTextView.setText(getString(R.string.novel_detail_chapter_count_unknown));
        } else {
            mChapterCountTextView.setText(getString(R.string.novel_detail_chapter_count_fomat, mNovelDetail.getChapterNumber()));
        }
        mLatestChapterTextView.setText(getString(R.string.novel_detail_latest_chapter_fomat, mNovelDetail.getLatestChapter()));

        final String detailAbstract = mNovelDetail.getSummary().replace("\t","");
        if (!TextUtils.isEmpty(detailAbstract)) {
            UIUtils.setTextMaybeHtml(mAbstractTextView, UIUtils.addIndentToStart(detailAbstract));
            mAbstractHeaderTextView.setVisibility(View.VISIBLE);
            mAbstractTextView.setVisibility(View.VISIBLE);
            mHasSummaryContent = true;
        } else {
            mAbstractHeaderTextView.setVisibility(View.GONE);
            mAbstractTextView.setVisibility(View.GONE);
        }

        /*
        if(mAddNovelButton.getVisibility() != View.VISIBLE) {
            //mLoadingProgress.setVisibility(View.INVISIBLE);
            ObjectAnimator progressAnim = ObjectAnimator.ofFloat(mLoadingProgress, "alpha", 1.0f, 0.0f);
            progressAnim.setDuration(450);
            progressAnim.start();
            mAddNovelButton.setAlpha(0f);
            mAddNovelButton.setVisibility(View.VISIBLE);
            ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mAddNovelButton, "alpha", 0f, 1.0f);
            bgAnim.setDuration(450);
            bgAnim.start();
        }*/

        tryRenderTags();
        showAchieves();


        // Show empty message when all data is loaded, and nothing to show
        if ( !mHasSummaryContent) {
            //mRootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }
        postOnScroll();
    }

    private void tryRenderTags() {
        if( mNovelDetail == null || mNovelDetail.getTags() == null) {
            mTagsScrollView.setVisibility(View.GONE);
            return;
        }

        if (mNovelDetail.getTags().length <= 0) {
            mTagsScrollView.setVisibility(View.GONE);
        } else {
            mTagsScrollView.setVisibility(View.VISIBLE);
            mTagsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getThemedContext());
            String[] tags = mNovelDetail.getTags();

            if (tags.length == 0) {
                mTagsScrollView.setVisibility(View.GONE);
                return;
            }


            for (final String tag : tags) {
                if (tag.compareTo("0") == 0)
                    return;
                TextView chipView = (TextView) inflater.inflate(
                        R.layout.include_novel_detail_tag_chip, mTagsContainer, false);
                chipView.setText(tag);

                ShapeDrawable colorDrawable = new ShapeDrawable(new OvalShape());
                colorDrawable.setIntrinsicWidth(mTagColorDotSize);
                colorDrawable.setIntrinsicHeight(mTagColorDotSize);
                colorDrawable.getPaint().setStyle(Paint.Style.FILL);
                chipView.setCompoundDrawablesWithIntrinsicBounds(colorDrawable,
                        null, null, null);
                colorDrawable.getPaint().setColor(ColorUtils.getRandomPreDefinedColor(getThemedContext()));

                mTagsContainer.addView(chipView);
            }
        }
    }

    private void showAchieves() {
        if(mNovelDetail.getAchieves() == null)
            return;

        boolean hasAchieves;
        Timber.d(String.format("Achieve count: %d", mNovelDetail.getAchieves().length), "tag");
        if(mNovelDetail.getAchieves().length <= 0) {
            hasAchieves = false;
        } else {
            hasAchieves = true;
            mAchievesContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(getThemedContext());
            for(NovelDetailModel.NovelAchieveModel achieve : mNovelDetail.getAchieves()) {
                LinearLayout achieveView = (LinearLayout) inflater.inflate(
                        R.layout.layout_item_novel_achieve, mAchievesContainer, false);
                TextView achieveNameTextView = (TextView)achieveView.findViewById(R.id.detail_achieve_item_name);
                TextView achieveRankTextView = (TextView)achieveView.findViewById(R.id.detail_achieve_item_rank);
                TextView achieveDateTextView = (TextView)achieveView.findViewById(R.id.detail_achieve_item_date);

                // Achieve name
                achieveNameTextView.setText(achieve.getName());

                // Achieve rank
                achieveRankTextView.setText(achieve.getRank());
                achieveRankTextView.setTextColor(mDetailPrimaryColor);

                // Achieve date
                achieveDateTextView.setText(getString(R.string.novel_detail_achieve_date_fomat, achieve.getYear(), achieve.getMonth()));

                mAchievesContainer.addView(achieveView);
            }
        }
        mAchieveLayout.setVisibility(hasAchieves ? View.VISIBLE : View.GONE);

    }

    private void setDetailSectionHeaderColor() {
        mAchieveHeaderTextView.setTextColor(mDetailPrimaryColor);
        mAbstractHeaderTextView.setTextColor(mDetailPrimaryColor);
    }

    protected NovelDetailPresenter getPresenter(){
        return mPresenter;
    }

    @Override
    public void showNovelDetail(NovelDetailModel novelDetail) {
        this.mNovelDetail = novelDetail;
        showNovelDetailInformation();
    }

    @Override
    public void setFavoriteButtonStatus(boolean isFavorited) {
        showFavorited(isFavorited, false);
    }

    @Override
    public void setLoading(Boolean isLoading) {
        if(isLoading) {
            if (mAddNovelButton.getVisibility() != View.INVISIBLE) {
                //mLoadingProgress.setVisibility(View.INVISIBLE);
                ObjectAnimator progressAnim = ObjectAnimator.ofFloat(mLoadingProgress, "alpha", 0.0f, 1.0f);
                progressAnim.setDuration(450);
                progressAnim.start();
                mAddNovelButton.setAlpha(1f);
                mAddNovelButton.setVisibility(View.INVISIBLE);
                ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mAddNovelButton, "alpha", 1f, 0.0f);
                bgAnim.setDuration(450);
                bgAnim.start();
            }
        } else {
            if (mAddNovelButton.getVisibility() != View.VISIBLE) {
                //mLoadingProgress.setVisibility(View.INVISIBLE);
                ObjectAnimator progressAnim = ObjectAnimator.ofFloat(mLoadingProgress, "alpha", 1.0f, 0.0f);
                progressAnim.setDuration(450);
                progressAnim.start();
                mAddNovelButton.setAlpha(0f);
                mAddNovelButton.setVisibility(View.VISIBLE);
                ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mAddNovelButton, "alpha", 0f, 1.0f);
                bgAnim.setDuration(450);
                bgAnim.start();
            }
        }
    }

    @Override
    public Boolean isLoading() {
        return null;
    }

    @Override
    public void showToast(String text, ToastType toastType) {
        ToastProxy.showToast(this, text, toastType);
    }
}
