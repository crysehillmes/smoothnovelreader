package org.cryse.utils.preference;

import android.content.SharedPreferences;

public class IntegerPrefs extends PrefsItem<Integer> {
    public IntegerPrefs(SharedPreferences preferences, String key, Integer defaultValue) {
        super(preferences, key, defaultValue);
    }

    @Override
    public Integer get() {
        return sharedPreferences.getInt(key, defaultValue);
    }

    @Override
    public void set(Integer value) {
        sharedPreferences.edit().putInt(key, value).apply();
    }
}
