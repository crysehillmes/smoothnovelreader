package org.cryse.novelreader.ui;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.Theme;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.application.module.ReadOptionsModule;
import org.cryse.novelreader.application.qualifier.PrefsFontSize;
import org.cryse.novelreader.application.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.application.qualifier.PrefsNightMode;
import org.cryse.novelreader.application.qualifier.PrefsReadColorSchema;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.colorschema.ColorSchema;
import org.cryse.novelreader.util.colorschema.ColorSchemaBuilder;
import org.cryse.novelreader.util.prefs.BooleanPreference;
import org.cryse.novelreader.util.prefs.IntegerPreference;
import org.cryse.novelreader.util.prefs.StringPreference;
import org.cryse.widget.NumberPicker;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReadOptionsFragment extends AbstractFragment {
    private static final String LOG_TAG = ReadOptionsFragment.class.getSimpleName();

    @Inject
    @PrefsNightMode
    BooleanPreference mIsNightModePreference;

    @Inject
    @PrefsFontSize
    StringPreference mFontSizePreference;

    @Inject
    @PrefsLineSpacing
    StringPreference mLineSpacingPreference;

    @Inject
    @PrefsReadColorSchema
    IntegerPreference mColorSchemaPreference;

    @Bind(R.id.root_container)
    RelativeLayout mRootContainer;

    @Bind(R.id.numberpicker_fragment_read_options_text_size)
    NumberPicker mFontSizeNumberPicker;

    @Bind(R.id.imageview_fragment_read_options_text_size)
    ImageView mFontSizeImageView;

    @Bind(R.id.numberpicker_fragment_read_options_line_spacing)
    NumberPicker mLineSpacingNumberPicker;

    @Bind(R.id.imageview_fragment_read_options_line_spacing)
    ImageView mLineSpacingImageView;

    @Bind(R.id.imageview_fragment_read_options_color_schema_value)
    ImageView mColorSchemaValueImageView;

    @Bind(R.id.imageview_fragment_read_options_color_schema)
    ImageView mColorSchemaImageView;

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
        SmoothReaderApplication.get(getContext()).getAppComponent().plus(new ReadOptionsModule(this)).inject(this);
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
        int colorSchemaIndex = mColorSchemaPreference.get();
        Drawable colorSchemaDisplayDrawable = ColorSchemaBuilder
                .with(getContext())
                .textRes(R.string.color_schema_display_text)
                .displayDrawableByIndex(
                        colorSchemaIndex,
                        UIUtils.dp2px(getContext(), 64f),
                        UIUtils.dp2px(getContext(), 36f)
                );
        mColorSchemaValueImageView.setImageDrawable(colorSchemaDisplayDrawable);

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
        mRootContainer.setOnClickListener(v -> {
            if (mOnReadOptionsChangedListener != null) {
                mOnReadOptionsChangedListener.onCloseReadOptions();
            }
        });
        mFontSizeNumberPicker.setOnNumberPickedListener(number -> {
            String fontSizeString = Integer.toString(number);
            if (fontSizeString.equals(mFontSizePreference.get())) return;
            mFontSizePreference.set(fontSizeString);
            if (mOnReadOptionsChangedListener != null) {
                mOnReadOptionsChangedListener.onFontSizeChanged(fontSizeString);
            }
        });
        mLineSpacingNumberPicker.setOnNumberPickedListener(number -> {
            String lineSpacingString = Integer.toString(number);
            String currentValue = mLineSpacingPreference.get();
            if (currentValue.endsWith("%"))
                currentValue = currentValue.substring(0, currentValue.length() - 1);
            if (lineSpacingString.equals(currentValue)) return;
            mLineSpacingPreference.set(lineSpacingString);
            if (mOnReadOptionsChangedListener != null) {
                mOnReadOptionsChangedListener.onLineSpacingChanged(lineSpacingString);
            }
        });
        mColorSchemaValueImageView.setOnClickListener(v -> new SimpleDrawableChooserDialog()
                .setTheme(mIsNightModePreference.get() ? Theme.DARK : Theme.LIGHT)
                .setDrawables(
                        ColorSchemaBuilder
                                .with(getContext())
                                .textRes(R.string.color_schema_display_text)
                                .displayDrawables(
                                        UIUtils.dp2px(getContext(), 56f),
                                        UIUtils.dp2px(getContext(), 56f)
                                ))
                .show((AppCompatActivity) getActivity(), mColorSchemaPreference.get(), (index, color, darker) -> {
                    mColorSchemaPreference.set(index);
                    ColorSchema newColorSchema = ColorSchemaBuilder
                            .with(getContext())
                            .textRes(R.string.color_schema_display_text)
                            .byIndex(index);
                    if (mOnReadOptionsChangedListener != null) {
                        mOnReadOptionsChangedListener.onColorSchemaChanged(newColorSchema);
                    }
                    Drawable colorSchemaDisplayDrawable = ColorSchemaBuilder
                            .with(getContext())
                            .textRes(R.string.color_schema_display_text)
                            .displayDrawableByIndex(
                                    index,
                                    UIUtils.dp2px(getContext(), 64f),
                                    UIUtils.dp2px(getContext(), 36f)
                            );
                    mColorSchemaValueImageView.setImageDrawable(colorSchemaDisplayDrawable);
                }));
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

        void onColorSchemaChanged(ColorSchema newColorSchema);

        void onTraditionalChanged();

        void onFontChanged();
    }
}
