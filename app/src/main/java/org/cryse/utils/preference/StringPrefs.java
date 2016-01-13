package org.cryse.utils.preference;

import android.content.SharedPreferences;

public class StringPrefs extends PrefsItem<String> {
    public StringPrefs(SharedPreferences preferences, String key, String defaultValue) {
        super(preferences, key, defaultValue);
    }

    @Override
    public String get() {
        return sharedPreferences.getString(key, defaultValue);
    }

    @Override
    public void set(String value) {
        sharedPreferences.edit().putString(key, value).apply();
    }
}
