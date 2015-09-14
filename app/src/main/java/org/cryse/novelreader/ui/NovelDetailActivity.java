package org.cryse.novelreader.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.malinskiy.superrecyclerview.SuperRecyclerView;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.application.module.DetailActivityModule;
import org.cryse.novelreader.constant.DataContract;
import org.cryse.novelreader.logic.impl.NovelSourceManager;
import org.cryse.novelreader.model.NovelDetailModel;
import org.cryse.novelreader.model.NovelModel;
import org.cryse.novelreader.presenter.NovelDetailPresenter;
import org.cryse.novelreader.service.ChapterContentsCacheService;
import org.cryse.novelreader.ui.adapter.NovelDetailAdapter;
import org.cryse.novelreader.ui.adapter.item.NovelDetailItem;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.LUtils;
import org.cryse.novelreader.util.SimpleSnackbarType;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.view.NovelDetailView;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;


public class NovelDetailActivity extends AbstractThemeableActivity implements NovelDetailView/*, ObservableScrollView.Callbacks*/ {
    private static final String LOG_TAG = NovelDetailActivity.class.getName();
    // Constant


    // View field
    @Bind(R.id.appbarlayout)
    AppBarLayout mAppBarLayout;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    @Bind(R.id.page_novel_detail_textview_title)
    TextView mTitleTextView;

    @Bind(R.id.page_novel_detail_textview_author)
    TextView mAuthorTextView;

    @Bind(R.id.page_novel_detail_recyclerview)
    SuperRecyclerView mCollectionView;

    @Bind(R.id.page_novel_detail_fab_add)
    FloatingActionButton mAddFAB;

    @Inject
    NovelDetailPresenter mPresenter;
    @Inject
    NovelSourceManager mSourceManager;

    MenuItem mStartReadingMenuItem;
    ServiceConnection mBackgroundServiceConnection;
    private int mDetailPrimaryColor;
    private NovelModel mNovel;
    private NovelDetailModel mNovelDetail;
    private boolean mIsFavorited = false;
    private boolean mShowStartReadingButton = true;
    private ChapterContentsCacheService.ChapterContentsCacheBinder mServiceBinder;
    private NovelDetailAdapter mDetailAdapter;

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
        ButterKnife.bind(this);
        setUpToolbar(mToolbar);

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
            mToolbar.setNavigationIcon(R.drawable.ic_ab_close);
        }
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
        setupRecyclerView();
        /*mScrollView.addCallbacks(this);
        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.addOnGlobalLayoutListener(mGlobalLayoutListener);
        }*/
        mAddFAB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mServiceBinder != null && mServiceBinder.isCaching() && mServiceBinder.getCurrentCachingNovelId() != null) {
                    String currentCachingNovelId = mServiceBinder.getCurrentCachingNovelId();
                    if(mIsFavorited && currentCachingNovelId.compareTo(mNovel.getNovelId()) == 0) {
                        showSnackbar(getString(R.string.toast_chapter_contents_caching_cannot_delete, mNovel.getTitle()), SimpleSnackbarType.WARNING);
                        return;
                    }
                }
                if(mIsFavorited && mServiceBinder != null) {
                   if(mServiceBinder.removeFromQueueIfExist(mNovel.getNovelId())) {
                       showSnackbar(getString(R.string.notification_action_chapter_contents_cancel_novel, mNovel.getTitle()), SimpleSnackbarType.INFO);
                   }
                }
                boolean isFavorite = !mIsFavorited;
                showFavorited(isFavorite, true);
                getPresenter().addOrRemoveFavorite(mNovel, isFavorite);
                //helper.setSessionStarred(mSessionUri, starred, mTitleString);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    mAddFAB.announceForAccessibility(isFavorite ?
                            getString(R.string.novel_details_a11y_novel_added) :
                            getString(R.string.novel_details_a11y_novel_removed));
                }
            }
        });
    }

    /*@Override
    public Intent getParentActivityIntent() {
        // TODO: make this Activity navigate up to the right screen depending on how it was launched
        return new Intent(this, MyScheduleActivity.class);
    }*/

    private void setupRecyclerView() {
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mCollectionView.setLayoutManager(layoutManager);
        mDetailAdapter = new NovelDetailAdapter(this, null);
        mCollectionView.setAdapter(mDetailAdapter);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(mNovelDetail != null)
            outState.putParcelable("novel_detail_object", mNovelDetail);
    }

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

    /*private void recomputePhotoAndScrollingMetrics() {
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
    }*/

    @Override
    public void onStart() {
        super.onStart();
        getPresenter().bindView(this);
        Intent service = new Intent(this.getApplicationContext(), ChapterContentsCacheService.class);
        startService(service);
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
        /*if (mScrollView == null) {
            return;
        }

        ViewTreeObserver vto = mScrollView.getViewTreeObserver();
        if (vto.isAlive()) {
            vto.removeGlobalOnLayoutListener(mGlobalLayoutListener);
        }*/
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(this).getAppComponent().plus(
                new DetailActivityModule(this)
        ).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    /*@Override
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
    }*/

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
        mDetailPrimaryColor = ColorUtils.getPreDefinedColorFromId(getThemedContext().getResources(), mNovel.getNovelId(), mNovel.getTitle().length());
        if (mDetailPrimaryColor == 0) {
            // no color -- use default
            mDetailPrimaryColor = ColorUtils.getColor(getResources(), R.color.primary_color);
        } else {
            // make sure it's opaque
            mDetailPrimaryColor = UIUtils.setColorAlpha(mDetailPrimaryColor, 255);
        }
        setStatusBarColor(mDetailPrimaryColor);
        mAppBarLayout.setBackgroundColor(mDetailPrimaryColor);

        mTitleTextView.setText(mNovel.getTitle());
        mAuthorTextView.setText(mNovel.getAuthor());
    }

    private void loadNovelDetail() {
        showNovelInformation();
        if(mNovelDetail != null)
            showNovelDetailInformation();
        else {
            getPresenter().loadNovelDetail(mNovel);
        }
    }

    /*private void postOnScroll() {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onScrollChanged(0, 0); // trigger scroll handling
                mScrollViewChild.setVisibility(View.VISIBLE);
                //mAbstract.setTextIsSelectable(true);
            }
        });
    }*/

    private void showFavorited(boolean isFavorited, boolean allowAnimate) {
        mIsFavorited = isFavorited;

        /*if(isFavorited) {
            mAddFAB.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_checked, null));
        } else {
            mAddFAB.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.ic_action_checked, null));
        }*/
        //mAddFAB.setChecked(mIsFavorited, allowAnimate);

        LUtils.setOrAnimatePlusCheckIcon(this, mAddFAB, isFavorited, allowAnimate);
        mAddFAB.setContentDescription(getString(isFavorited
                ? R.string.remove_from_bookshelf_description
                : R.string.add_to_bookshelf_description));
    }

    private void showNovelDetailInformation() {
        List<NovelDetailItem> detailItems = new ArrayList<>();
        String detailAbstract = mNovelDetail.getSummary().replace("\t", "");

        if (!TextUtils.isEmpty(detailAbstract)) {
            //UIUtils.setTextMaybeHtml(mAbstractTextView, UIUtils.addIndentToStart(detailAbstract));
            SpannableStringBuilder builder = new SpannableStringBuilder();
            builder.append(detailAbstract);
            builder.append("\n\n");
            String copyRight = mSourceManager.getCopyRightStatement(this, mNovel.getType());
            int start = builder.length();
            int end = start + copyRight.length();
            builder.append(copyRight);
            builder.setSpan(
                    new ForegroundColorSpan(mDetailPrimaryColor),
                    start,
                    end,
                    Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
            );
            builder.append("\n");
            detailItems.add(new NovelDetailItem(builder));
            detailItems.add(new NovelDetailItem(
                            getString(R.string.novel_detail_abstract_header),
                            NovelDetailItem.TYPE_HEADER
                    )
            );
            if (mNovelDetail.getChapterNumber() <= 0) {
                detailItems.add(new NovelDetailItem(getString(R.string.novel_detail_chapter_count_unknown)));
            } else {
                detailItems.add(new NovelDetailItem(
                                getString(
                                        R.string.novel_detail_chapter_count_fomat,
                                        mNovelDetail.getChapterNumber()
                                )
                        )
                );
            }
            detailItems.add(new NovelDetailItem(
                            getString(
                                    R.string.novel_detail_latest_chapter_fomat,
                                    mNovelDetail.getLatestChapter()
                            )
                    )
            );
            mDetailAdapter.replaceWith(detailItems);
        }


        /*setDetailSectionHeaderColor();
        String copyRightStatement = mSourceManager.getCopyRightStatement(this, mNovel.getType());
        // TODO: mStatusTextView.setText(getString(R.string.novel_detail_status_fomat, mNovel.getStatus()));
        if(mNovelDetail.getChapterNumber() <= 0) {
            mChapterCountTextView.setText(getString(R.string.novel_detail_chapter_count_unknown));
        } else {
            mChapterCountTextView.setText(getString(R.string.novel_detail_chapter_count_fomat, mNovelDetail.getChapterNumber()));
        }
        mLatestChapterTextView.setText(getString(R.string.novel_detail_latest_chapter_fomat, mNovelDetail.getLatestChapter()));

        final String detailAbstract = mNovelDetail.getSummary().replace("\t","");
        if (!TextUtils.isEmpty(detailAbstract)) {
            UIUtils.setTextMaybeHtml(mAbstractTextView, UIUtils.addIndentToStart(detailAbstract));
            SpannableString copyRightString = new SpannableString(copyRightStatement);
            copyRightString.setSpan(new ForegroundColorSpan(mDetailPrimaryColor), 0, copyRightString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            mAbstractTextView.append("\n\n");
            mAbstractTextView.append(copyRightString);
            mAbstractHeaderTextView.setVisibility(View.VISIBLE);
            mAbstractTextView.setVisibility(View.VISIBLE);
            mHasSummaryContent = true;
        } else {
            mAbstractHeaderTextView.setVisibility(View.GONE);
            mAbstractTextView.setVisibility(View.GONE);
        }

        *//*
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
        }*//*

        tryRenderTags();
        showAchieves();


        // Show empty message when all data is loaded, and nothing to show
        if ( !mHasSummaryContent) {
            //mRootView.findViewById(android.R.id.empty).setVisibility(View.VISIBLE);
        }
        postOnScroll();*/
    }

    /*private void tryRenderTags() {
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
                colorDrawable.getPaint().setColor(ColorUtils.getRandomPreDefinedColor(getThemedContext().getResources()));

                mTagsContainer.addView(chipView);
            }
        }
    }*/

    /*private void showAchieves() {
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

    }*/

    /*private void setDetailSectionHeaderColor() {
        mAchieveHeaderTextView.setTextColor(mDetailPrimaryColor);
        mAbstractHeaderTextView.setTextColor(mDetailPrimaryColor);
    }*/

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
            /*if (mAddNovelButton.getVisibility() != View.INVISIBLE) {
                //mLoadingProgress.setVisibility(View.INVISIBLE);
                ObjectAnimator progressAnim = ObjectAnimator.ofFloat(mLoadingProgress, "alpha", 0.0f, 1.0f);
                progressAnim.setDuration(450);
                progressAnim.start();
                mAddNovelButton.setAlpha(1f);
                mAddNovelButton.setVisibility(View.INVISIBLE);
                ObjectAnimator bgAnim = ObjectAnimator.ofFloat(mAddNovelButton, "alpha", 1f, 0.0f);
                bgAnim.setDuration(450);
                bgAnim.start();
            }*/
        } else {
            /*if (mAddNovelButton.getVisibility() != View.VISIBLE) {
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
        }
    }

    @Override
    public Boolean isLoading() {
        return null;
    }
}
