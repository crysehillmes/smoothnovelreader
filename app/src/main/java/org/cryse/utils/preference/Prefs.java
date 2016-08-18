package org.cryse.utils.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

@SuppressWarnings("unused")
public final class Prefs {
    private Context mContext;
    private SharedPreferences mSharedPreference;

    private static Prefs sInstance;

    private Prefs(Context context, String prefsName) {
        sInstance = this;
        mContext = context;
        if(prefsName.equalsIgnoreCase("")) {
            mSharedPreference = PreferenceManager.getDefaultSharedPreferences(context);
        } else {
            mSharedPreference = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE);
        }
    }

    public static Builder with(Context context) {
        return new Builder(context);
    }

    public static String getString(String key, String defaultValue) {
        return sInstance.mSharedPreference.getString(key, defaultValue);
    }

    public static boolean getBoolean(String key, boolean defaultValue) {
        return sInstance.mSharedPreference.getBoolean(key, defaultValue);
    }

    public static int getInt(String key, int defaultValue) {
        return sInstance.mSharedPreference.getInt(key, defaultValue);
    }

    public static Long getLong(String key, Long defaultValue) {
        return sInstance.mSharedPreference.getLong(key, defaultValue);
    }

    public static StringPrefs getStringPrefs(String key, String defaultValue) {
        return new StringPrefs(sInstance.mSharedPreference, key, defaultValue);
    }

    public static BooleanPrefs getBooleanPrefs(String key, boolean defaultValue) {
        return new BooleanPrefs(sInstance.mSharedPreference, key, defaultValue);
    }

    public static IntegerPrefs getIntPrefs(String key, int defaultValue) {
        return new IntegerPrefs(sInstance.mSharedPreference, key, defaultValue);
    }

    public static LongPrefs getLongPrefs(String key, Long defaultValue) {
        return new LongPrefs(sInstance.mSharedPreference, key, defaultValue);
    }

    public static class Builder {
        private Context context;
        private String prefsName;
        public Builder(Context context) {
            this.context = context;
        }

        public Builder useName(String name) {
            prefsName = name;
            return this;
        }

        public Builder useDefault() {
            prefsName = "";
            return this;
        }

        public Prefs init() {
            if(this.context != null && prefsName != null) {
                return new Prefs(context, prefsName);
            } else {
                throw new IllegalStateException();
            }
        }
    }
}