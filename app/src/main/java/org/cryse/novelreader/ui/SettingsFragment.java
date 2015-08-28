package org.cryse.novelreader.ui;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.cryse.changelog.ChangeLogUtils;
import org.cryse.novelreader.R;
import org.cryse.novelreader.constant.PreferenceConstant;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.event.ThemeColorChangedEvent;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ThemeEngine;
import org.cryse.novelreader.util.prefs.IntegerPreference;

import timber.log.Timber;

public class SettingsFragment extends PreferenceFragment {
    private static final String LOG_TAG = SettingsFragment.class.getName();
    RxEventBus mEventBus = RxEventBus.getInstance();
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
        setUpThemeColorPreference();
        Preference versionPrefs = findPreference("prefs_about_version");
        try {
            versionPrefs
                    .setSummary(getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            Timber.d(e, e.getMessage(), LOG_TAG);
        }
        versionPrefs.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            private long exitTime = 0;
            private int times = 0;

            @Override
            public boolean onPreferenceClick(Preference preference) {
                if ((System.currentTimeMillis() - exitTime) > 2000) {
                    exitTime = System.currentTimeMillis();
                    times = 0;
                } else {
                    times++;
                    if (times >= 4) {
                        try {
                            int versionCode = getActivity().getPackageManager().getPackageInfo(getActivity().getPackageName(), 0).versionCode;
                            Toast.makeText(getActivity(), String.format("versionCode: %d", versionCode), Toast.LENGTH_SHORT).show();
                        } catch (PackageManager.NameNotFoundException e) {
                            Timber.d(e, e.getMessage(), LOG_TAG);
                        } finally {
                            times = 0;
                        }
                    }
                }
                return true;
            }
        });

        Preference changelogPref = findPreference("prefs_about_changelog");
        changelogPref.setOnPreferenceClickListener(preference -> {
            ChangeLogUtils reader = new ChangeLogUtils(getActivity(), R.xml.changelog);

            MaterialDialog materialDialog = new MaterialDialog.Builder(getActivity())
                    .title(R.string.settings_item_change_log_title)
                    .theme(((AbstractThemeableActivity) getActivity()).isNightMode() ? Theme.DARK : Theme.LIGHT)
                    .content(reader.toSpannable())
                    .show();

            return true;
        });
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

    private void setUpThemeColorPreference() {
        Preference themeColorPreference = findPreference("prefs_theme_color");
        IntegerPreference themeColorPrefsValue = new IntegerPreference(getPreferenceManager().getSharedPreferences(), PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR, PreferenceConstant.SHARED_PREFERENCE_THEME_COLOR_VALUE);

        themeColorPreference.setOnPreferenceClickListener(preference -> {
            new ColorChooserDialog()
                    .setColors(getActivity(), R.array.primaryColors)
                    .show((AppCompatActivity)getActivity(), themeColorPrefsValue.get(), (index, color, darker) -> {
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
