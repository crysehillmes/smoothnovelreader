package org.cryse.utils.preference;

import android.content.SharedPreferences;

public class LongPrefs extends PrefsItem<Long> {
    public LongPrefs(SharedPreferences preferences, String key, Long defaultValue) {
        super(preferences, key, defaultValue);
    }

    @Override
    public Long get() {
        return sharedPreferences.getLong(key, defaultValue);
    }

    @Override
    public void set(Long value) {
        sharedPreferences.edit().putLong(key, value).apply();
    }
}
