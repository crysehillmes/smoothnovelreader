package org.cryse.novelreader.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBarActivity;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.event.ThemeColorChangedEvent;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ThemeEngine;
import org.cryse.novelreader.util.prefs.IntegerPreference;
import org.cryse.novelreader.util.prefs.PreferenceConstant;

import javax.inject.Inject;

public class SettingsFragment extends PreferenceFragment {
    @Inject
    RxEventBus mEventBus;
    private OnConcisePreferenceChangedListener mOnConcisePreferenceChangedListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectThis();
        mOnConcisePreferenceChangedListener = new OnConcisePreferenceChangedListener();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);
        Preference isGrayScalePrefs = findPreference(PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT);

        Boolean isNightMode = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, false);
        isGrayScalePrefs.setEnabled(isNightMode);
        setUpThemeColorPreference();
    }

    private void injectThis() {
        SmoothReaderApplication.get(getActivity()).inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mOnConcisePreferenceChangedListener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mOnConcisePreferenceChangedListener);
    }

    public class OnConcisePreferenceChangedListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            AbstractThemeableActivity parentActivity = (AbstractThemeableActivity) getActivity();
            if (parentActivity != null) {
                if (key.compareTo(PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE) == 0) {
                    parentActivity.reloadTheme();
                }
            }
        }
    }

    private void setUpThemeColorPreference() {
        Preference themeColorPreference = findPreference("prefs_theme_color");
        IntegerPreference themeColorPrefsValue = new IntegerPreference(getPreferenceManager().getSharedPreferences(), PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);

        themeColorPreference.setOnPreferenceClickListener(preference -> {
            new ColorChooserDialog()
                    .setColors(getActivity(), R.array.primaryColors)
                    .show((ActionBarActivity)getActivity(), themeColorPrefsValue.get(), (index, color, darker) -> {
                themeColorPrefsValue.set(index);
                ThemeEngine themeEngine = ((AbstractThemeableActivity)getActivity()).getThemeEngine();
                mEventBus.sendEvent(new ThemeColorChangedEvent(
                        themeEngine.getPrimaryColor(getActivity()),
                        themeEngine.getPrimaryDarkColor(getActivity()),
                        themeEngine.getPrimaryColorResId(),
                        themeEngine.getPrimaryDarkColorResId()));
            });
            return true;
        });
    }
}
