package org.cryse.utils.preference;

import android.content.SharedPreferences;

public class BooleanPrefs extends PrefsItem<Boolean> {
    public BooleanPrefs(SharedPreferences preferences, String key, Boolean defaultValue) {
        super(preferences, key, defaultValue);
    }

    @Override
    public Boolean get() {
        return sharedPreferences.getBoolean(key, defaultValue);
    }

    @Override
    public void set(Boolean value) {
        sharedPreferences.edit().putBoolean(key, value).apply();
    }
}
