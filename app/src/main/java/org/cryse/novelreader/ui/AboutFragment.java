package org.cryse.novelreader.ui;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import org.cryse.novelreader.R;

public class AboutFragment extends PreferenceFragment {
    private static final String LOG_TAG = AboutFragment.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_about);

    }
}
