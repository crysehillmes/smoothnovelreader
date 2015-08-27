package org.cryse.novelreader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Spinner;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.qualifier.PrefsFontSize;
import org.cryse.novelreader.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.prefs.StringPreference;
import org.cryse.widget.NumberPicker;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadOptionsFragment extends AbstractFragment {
    private static final String LOG_TAG = ReadOptionsFragment.class.getSimpleName();
    @Inject
    @PrefsFontSize
    StringPreference mFontSizePreference;

    @Inject
    @PrefsLineSpacing
    StringPreference mLineSpacingPreference;

    @Bind(R.id.root_container)
    FrameLayout mRootContainer;

    @Bind(R.id.numberpicker_fragment_read_options_text_size)
    NumberPicker mFontSizeNumberPicker;

    @Bind(R.id.numberpicker_fragment_read_options_line_spacing)
    NumberPicker mLineSpacingNumberPicker;

    @Bind(R.id.switch_fragment_read_options_traditional)
    SwitchCompat mTraditionalSwitch;

    @Bind(R.id.spinner_fragment_read_options_font)
    Spinner mFontSpinner;

    private OnReadOptionsChangedListener mOnReadOptionsChangedListener;

    public static ReadOptionsFragment newInstance(OnReadOptionsChangedListener onReadOptionsChangedListener) {
        ReadOptionsFragment readOptionsFragment = new ReadOptionsFragment();
        Bundle args = new Bundle();
        readOptionsFragment.setArguments(args);
        readOptionsFragment.mOnReadOptionsChangedListener = onReadOptionsChangedListener;
        return readOptionsFragment;
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
        View contentView = inflater.inflate(R.layout.fragment_read_settings, null);
        ButterKnife.bind(this, contentView);
        setInitialValues();
        addListeners();
        return contentView;
    }

    private void setInitialValues() {
        // Font Size:
        mFontSizeNumberPicker.setStep(2);
        String[] mFontSizeValues = getResources().getStringArray(R.array.readview_font_size_entries);
        int maxFontSize = Integer.valueOf(mFontSizeValues[mFontSizeValues.length - 1]);
        int minFontSize = Integer.valueOf(mFontSizeValues[0]);
        int currentFontSize = Integer.valueOf(mFontSizePreference.get());
        mFontSizeNumberPicker.setMaxValue(maxFontSize);
        mFontSizeNumberPicker.setMinValue(minFontSize);
        mFontSizeNumberPicker.setCurrentValue(currentFontSize);


        // Line Spacing:
        mLineSpacingNumberPicker.setStep(25);
        mLineSpacingNumberPicker.setValueDisplaySuffix("%");
        String[] mLineSpacingValues = getResources().getStringArray(R.array.readview_line_spacing_entries);
        int maxLineSpacing = Integer.valueOf(removePercentSuffix(mLineSpacingValues[mLineSpacingValues.length - 1]));
        int minLineSpacing = Integer.valueOf(removePercentSuffix(mLineSpacingValues[0]));
        int currentLineSpacing = Integer.valueOf(removePercentSuffix(mLineSpacingPreference.get()));
        mLineSpacingNumberPicker.setMaxValue(maxLineSpacing);
        mLineSpacingNumberPicker.setMinValue(minLineSpacing);
        mLineSpacingNumberPicker.setCurrentValue(currentLineSpacing);
    }

    private void addListeners() {
        mRootContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnReadOptionsChangedListener != null) {
                    mOnReadOptionsChangedListener.onCloseReadOptions();
                }
            }
        });
        mFontSizeNumberPicker.setOnNumberPickedListener(new NumberPicker.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                String fontSizeString = Integer.toString(number);
                if (fontSizeString.equals(mFontSizePreference.get())) return;
                mFontSizePreference.set(fontSizeString);
                if (mOnReadOptionsChangedListener != null) {
                    mOnReadOptionsChangedListener.onFontSizeChanged(fontSizeString);
                }
            }
        });
        mLineSpacingNumberPicker.setOnNumberPickedListener(new NumberPicker.OnNumberPickedListener() {
            @Override
            public void onNumberPicked(int number) {
                String lineSpacingString = Integer.toString(number);
                String currentValue = mLineSpacingPreference.get();
                if (currentValue.endsWith("%"))
                    currentValue = currentValue.substring(0, currentValue.length() - 1);
                if (lineSpacingString.equals(currentValue)) return;
                mLineSpacingPreference.set(lineSpacingString);
                if (mOnReadOptionsChangedListener != null) {
                    mOnReadOptionsChangedListener.onLineSpacingChanged(lineSpacingString);
                }
            }
        });
    }

    private String removePercentSuffix(String input) {
        if (input.endsWith("%"))
            return input.replace("%", "");
        return input;
    }

    public interface OnReadOptionsChangedListener {
        void onCloseReadOptions();

        void onFontSizeChanged(String fontSizeString);

        void onLineSpacingChanged(String lineSpacing);

        void onColorSchemaChanged();

        void onTraditionalChanged();

        void onFontChanged();
    }
}
