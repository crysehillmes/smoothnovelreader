package org.cryse.utils.preference;

import android.content.SharedPreferences;

public abstract class PrefsItem<T> {
    protected String key;
    protected T defaultValue;
    protected SharedPreferences sharedPreferences;

    public PrefsItem(SharedPreferences preferences, String key, T defaultValue) {
        this.sharedPreferences = preferences;
        this.key = key;
        this.defaultValue = defaultValue;
    }

    public abstract T get();

    public abstract void set(T value);
}
