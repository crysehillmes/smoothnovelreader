package org.cryse.novelreader.ui;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.animation.FadeInOutAnimator;
import org.cryse.novelreader.util.drawable.CompatTintUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadBottomPanelFragment extends AbstractFragment {
    private static final String LOG_TAG = ReadBottomPanelFragment.class.getSimpleName();
    private static final String READ_OPTIONS_FRAGMENT_TAG = "read_options_fragment_tag";

    @Bind(R.id.root_container)
    RelativeLayout mRootContainer;

    @Bind(R.id.options_container)
    FrameLayout mOptionsContainer;

    @Bind(R.id.fragment_read_bottom_panel_button_previous)
    ImageButton mPreviousButton;

    @Bind(R.id.fragment_read_bottom_panel_button_dark_mode)
    ImageButton mDarkModeButton;

    @Bind(R.id.fragment_read_bottom_panel_button_reload)
    ImageButton mReloadButton;

    @Bind(R.id.fragment_read_bottom_panel_button_read_options)
    ImageButton mReadOptionsButton;

    @Bind(R.id.fragment_read_bottom_panel_button_next)
    ImageButton mNextButton;

    private OnReadBottomPanelItemClickListener mOnReadBottomPanelItemClickListener;
    private ReadOptionsFragment.OnReadOptionsChangedListener mOnReadOptionsChangedListener = new ReadOptionsFragment.OnReadOptionsChangedListener() {
        @Override
        public void onCloseReadOptions() {
            closeSubFragment(null);
            /*if(mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onCloseReadOptions();*/
        }

        @Override
        public void onFontSizeChanged(String fontSizeString) {
            if (mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onFontSizeChanged(fontSizeString);
        }

        @Override
        public void onLineSpacingChanged(String lineSpacing) {
            if (mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onLineSpacingChanged(lineSpacing);
        }

        @Override
        public void onColorSchemaChanged() {
            if (mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onColorSchemaChanged();

        }

        @Override
        public void onTraditionalChanged() {
            if (mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onTraditionalChanged();
        }

        @Override
        public void onFontChanged() {
            if (mOnReadBottomPanelItemClickListener != null)
                mOnReadBottomPanelItemClickListener.onFontChanged();
        }
    };

    public static ReadBottomPanelFragment newInstance(OnReadBottomPanelItemClickListener onReadBottomPanelItemClickListener) {
        ReadBottomPanelFragment readBottomPanelFragment = new ReadBottomPanelFragment();
        Bundle args = new Bundle();
        readBottomPanelFragment.setArguments(args);
        readBottomPanelFragment.mOnReadBottomPanelItemClickListener = onReadBottomPanelItemClickListener;
        return readBottomPanelFragment;
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(getActivity()).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentExit(this, LOG_TAG);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.fragment_read_bottom_panel, null);
        ButterKnife.bind(this, contentView);
        setInitialValues();
        addListeners();
        return contentView;
    }

    private void setInitialValues() {
        int tintColor = ColorUtils.getColorFromAttr(getContext(), R.attr.smooth_theme_text_color_primary);
        Resources resources = getResources();
        mPreviousButton.setImageDrawable(CompatTintUtils.getTintedDrawable(resources, R.drawable.ic_action_chapter_previous, tintColor));
        mDarkModeButton.setImageDrawable(CompatTintUtils.getTintedDrawable(resources, R.drawable.ic_action_dark_mode, tintColor));
        mReloadButton.setImageDrawable(CompatTintUtils.getTintedDrawable(resources, R.drawable.ic_action_reload, tintColor));
        mReadOptionsButton.setImageDrawable(CompatTintUtils.getTintedDrawable(resources, R.drawable.ic_action_read_options, tintColor));
        mNextButton.setImageDrawable(CompatTintUtils.getTintedDrawable(resources, R.drawable.ic_action_chapter_next, tintColor));
    }

    private void addListeners() {
        View.OnClickListener onClickListener = v -> {
            if (mOnReadBottomPanelItemClickListener != null) {
                switch (v.getId()) {
                    case R.id.root_container:
                        mOnReadBottomPanelItemClickListener.onClose();
                        break;
                    case R.id.fragment_read_bottom_panel_button_previous:
                        mOnReadBottomPanelItemClickListener.onPreviousClick();
                        break;
                    case R.id.fragment_read_bottom_panel_button_dark_mode:
                        mOnReadBottomPanelItemClickListener.onDarkModeClick();
                        break;
                    case R.id.fragment_read_bottom_panel_button_reload:
                        mOnReadBottomPanelItemClickListener.onReloadClick();
                        break;
                    case R.id.fragment_read_bottom_panel_button_read_options:
                        if (mOptionsContainer.isShown()) {
                            closeSubFragment(null);
                        } else {
                            showReadOptionsPanel();
                        }
                        break;
                    case R.id.fragment_read_bottom_panel_button_next:
                        mOnReadBottomPanelItemClickListener.onNextClick();
                        break;
                }
            }
        };
        mRootContainer.setOnClickListener(onClickListener);
        mPreviousButton.setOnClickListener(onClickListener);
        mDarkModeButton.setOnClickListener(onClickListener);
        mReloadButton.setOnClickListener(onClickListener);
        mReadOptionsButton.setOnClickListener(onClickListener);
        mNextButton.setOnClickListener(onClickListener);
    }

    private void showReadOptionsPanel() {
        Fragment fragment = getChildFragmentManager().findFragmentByTag(READ_OPTIONS_FRAGMENT_TAG);

        if (fragment == null) {
            fragment = ReadOptionsFragment.newInstance(mOnReadOptionsChangedListener);
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
            fragmentTransaction.add(R.id.options_container, fragment, READ_OPTIONS_FRAGMENT_TAG);
            // fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.commit();
        }
        if (!mOptionsContainer.isShown()) {
            FadeInOutAnimator.fadeIn(getContext(), mOptionsContainer, true,
                    () -> {
                        //finalSearchFragment.search(string);
                    });
        } else {
            //readOptionsFragment.search(string);
        }
    }

    private void closeSubFragment(Runnable postClose) {
        if (mOptionsContainer.isShown()) {
            Fragment fragment = getChildFragmentManager().findFragmentByTag(READ_OPTIONS_FRAGMENT_TAG);
            if (fragment != null) {
                FadeInOutAnimator.fadeOut(
                        getContext(),
                        mOptionsContainer,
                        true,
                        () -> {
                            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
                            fragmentTransaction.remove(fragment);
                            fragmentTransaction.commit();
                            getChildFragmentManager().executePendingTransactions();
                            if (postClose != null)
                                postClose.run();
                        });
            } else {
                FadeInOutAnimator.fadeOut(
                        getContext(),
                        mOptionsContainer,
                        true,
                        () -> {
                            if (postClose != null)
                                postClose.run();
                        });
            }
        }
    }

    public interface OnReadBottomPanelItemClickListener {
        void onClose();

        void onNextClick();

        void onPreviousClick();

        void onDarkModeClick();

        void onReloadClick();

        void onCloseReadOptions();

        void onFontSizeChanged(String fontSizeString);

        void onLineSpacingChanged(String lineSpacing);

        void onColorSchemaChanged();

        void onTraditionalChanged();

        void onFontChanged();
    }
}
