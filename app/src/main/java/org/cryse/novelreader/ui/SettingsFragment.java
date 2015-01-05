package org.cryse.novelreader.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;

import org.cryse.novelreader.R;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.prefs.PreferenceConstant;

public class SettingsFragment extends PreferenceFragment {
    private OnConcisePreferenceChangedListener mOnConcisePreferenceChangedListener = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mOnConcisePreferenceChangedListener = new OnConcisePreferenceChangedListener();
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_settings);
        Preference isGrayScalePrefs = findPreference(PreferenceConstant.SHARED_PREFERENCE_GRAYSCALE_IN_NIGHT);

        Boolean isNightMode = getPreferenceManager().getSharedPreferences().getBoolean(PreferenceConstant.SHARED_PREFERENCE_IS_NIGHT_MODE, false);
        isGrayScalePrefs.setEnabled(isNightMode);
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
}
