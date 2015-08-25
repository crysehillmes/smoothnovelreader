package org.cryse.novelreader.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.qualifier.PrefsFontSize;
import org.cryse.novelreader.qualifier.PrefsLineSpacing;
import org.cryse.novelreader.ui.common.AbstractFragment;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.prefs.StringPreference;

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


    @Bind(R.id.spinner_fragment_read_options_font_size)
    Spinner mFontSizeSpinner;

    @Bind(R.id.spinner_fragment_read_options_line_spacing)
    Spinner mLineSpacingSpinner;

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
        ArrayAdapter<CharSequence> fontSizeAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.readview_font_size_entries, android.R.layout.simple_spinner_item);
        fontSizeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mFontSizeSpinner.setAdapter(fontSizeAdapter);
        int fontSizePosition = fontSizeAdapter.getPosition(mFontSizePreference.get());
        mFontSizeSpinner.setSelection(fontSizePosition);

        // Line Spacing:
        ArrayAdapter<CharSequence> lineSpacingAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.readview_line_spacing_entries, android.R.layout.simple_spinner_item);
        lineSpacingAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mLineSpacingSpinner.setAdapter(lineSpacingAdapter);
        int lineSpacingPosition = lineSpacingAdapter.getPosition(mLineSpacingPreference.get());
        mLineSpacingSpinner.setSelection(lineSpacingPosition);
    }

    private void addListeners() {
        mFontSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String fontSizeString = (String) parent.getAdapter().getItem(position);
                mFontSizePreference.set(fontSizeString);
                if (mOnReadOptionsChangedListener != null) {
                    mOnReadOptionsChangedListener.onFontSizeChanged(fontSizeString);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mLineSpacingSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String lineSpacing = (String) parent.getAdapter().getItem(position);
                mLineSpacingPreference.set(lineSpacing);
                if (mOnReadOptionsChangedListener != null) {
                    mOnReadOptionsChangedListener.onLineSpacingChanged(lineSpacing);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
