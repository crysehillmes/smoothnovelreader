package org.cryse.novelreader.application;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.LeakCanary;
import com.umeng.update.UmengUpdateAgent;

import org.cryse.novelreader.BuildConfig;
import org.cryse.novelreader.R;
import org.cryse.novelreader.application.component.AppComponent;
import org.cryse.novelreader.application.component.DaggerAppComponent;
import org.cryse.novelreader.application.module.AppModule;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;
import org.cryse.utils.preference.Prefs;

import io.fabric.sdk.android.Fabric;
import me.drakeet.library.CrashWoodpecker;
import timber.log.Timber;

public class SmoothReaderApplication extends Application {
    private static final String TAG = SmoothReaderApplication.class.getCanonicalName();

    private static final long CACHE_MAX_SIZE = 20l * 1024l * 1024l;
    private AppComponent appComponent;
    private AndroidNavigation mAndroidNavigation;
    public static SmoothReaderApplication get(Context context) {
        return (SmoothReaderApplication) context.getApplicationContext();
    }


    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    /**
     * Build object graph on creation so that objects are available
     */
    @Override
    public void onCreate() {
        super.onCreate();
        LeakCanary.install(this);
        CrashWoodpecker.fly().to(this);
        AnalyticsUtils.init(this, getString(R.string.UMENG_APPKEY_VALUE));
        Fabric.with(this, new Crashlytics());
        Timber.plant(new CrashReportingTree());
        Prefs.with(this).useDefault().init();
        UmengUpdateAgent.setAppkey(getString(R.string.UMENG_APPKEY_VALUE));
        UmengUpdateAgent.update(this);

        mAndroidNavigation = new AndroidNavigation();
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this, mAndroidNavigation))
                .build();

        /*Intent chapterContentCacheServiceIntent = new Intent(this, ChapterContentsCacheService.class);
        startService(chapterContentCacheServiceIntent);
        Intent loadLocalTextServiceIntent = new Intent(this, LocalFileImportService.class);
        startService(loadLocalTextServiceIntent);*/
    }

    public AppComponent getAppComponent() {
        return appComponent;
    }

    public AndroidNavigation getAndroidNavigation() {
        return mAndroidNavigation;
    }

    /** A tree which logs important information for crash reporting. */
    private static class CrashReportingTree extends Timber.DebugTree {
        @Override public void i(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Crashlytics.log(String.format(message, args));
                Log.i((String) args[0], message);
            }
        }

        @Override
        public void i(Throwable t, String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.i((String) args[0], message, t);
            }
        }

        @Override
        public void e(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.e((String) args[0], message);
            }
        }

        @Override
        public void e(Throwable t, String message, Object... args) {
            e(message, args);
            if (BuildConfig.DEBUG) {
                Log.e((String) args[0], message, t);
                Crashlytics.logException(t);
            }
        }

        @Override
        public void d(String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.d((String) args[0], message);
            }
        }

        @Override
        public void d(Throwable t, String message, Object... args) {
            if (BuildConfig.DEBUG) {
                Log.d((String) args[0], message, t);
            }
        }
    }

}
