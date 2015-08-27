package org.cryse.novelreader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadBottomPanelFragment extends AbstractFragment {
    private static final String LOG_TAG = ReadBottomPanelFragment.class.getSimpleName();

    @Bind(R.id.root_container)
    FrameLayout mRootContainer;

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
    }

    private void addListeners() {
        mRootContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnReadBottomPanelItemClickListener != null) {
                    mOnReadBottomPanelItemClickListener.onClose();
                }
            }
        });
        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnReadBottomPanelItemClickListener != null) {
                    switch (v.getId()) {
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
                            mOnReadBottomPanelItemClickListener.onReadOptionsClick();
                            break;
                        case R.id.fragment_read_bottom_panel_button_next:
                            mOnReadBottomPanelItemClickListener.onNextClick();
                            break;
                    }
                }
            }
        };
        mPreviousButton.setOnClickListener(onClickListener);
        mDarkModeButton.setOnClickListener(onClickListener);
        mReloadButton.setOnClickListener(onClickListener);
        mReadOptionsButton.setOnClickListener(onClickListener);
        mNextButton.setOnClickListener(onClickListener);
    }

    public interface OnReadBottomPanelItemClickListener {
        void onClose();

        void onReadOptionsClick();

        void onNextClick();

        void onPreviousClick();

        void onDarkModeClick();

        void onReloadClick();
    }
}
