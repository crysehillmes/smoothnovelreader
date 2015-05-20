package org.cryse.novelreader.application;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.crashlytics.android.Crashlytics;

import org.cryse.novelreader.BuildConfig;
import org.cryse.novelreader.event.RxEventBus;
import org.cryse.novelreader.modules.ModulesList;
import org.cryse.novelreader.service.ChapterContentsCacheService;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.store.HashTableRunTimeStore;
import org.cryse.novelreader.util.RunTimeStore;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import dagger.ObjectGraph;
import timber.log.Timber;

public class SmoothReaderApplication extends Application {
    private static final String TAG = SmoothReaderApplication.class.getCanonicalName();

    private static final long CACHE_MAX_SIZE = 20l * 1024l * 1024l;
    private ObjectGraph objectGraph;
    private RxEventBus mEventBus;
    private RunTimeStore mRunTimeStore;
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
        AnalyticsUtils.init("54117fcdfd98c5578c002940");
        Crashlytics.start(this);
        Timber.plant(new CrashReportingTree());
        mEventBus = new RxEventBus();
        buildObjectGraphAndInject();

        mRunTimeStore = new HashTableRunTimeStore();
        mAndroidNavigation = new AndroidNavigation(mRunTimeStore);

        Intent chapterContentCacheServiceIntent = new Intent(this, ChapterContentsCacheService.class);
        startService(chapterContentCacheServiceIntent);
    }


    /**
     * Used by Activities to create a scoped graph
     */
    public ObjectGraph createScopedGraph(Object... modules) {
        return objectGraph.plus(modules);
    }

    public ObjectGraph getObjectGraph() {
        return objectGraph;
    }

    public void buildObjectGraphAndInject() {
        objectGraph = ObjectGraph.create(ModulesList.list(this));
        inject(this);
    }


    public void inject(Object object) {

        objectGraph.inject(object);
    }


    public ObjectGraph getApplicationGraph() {

        return objectGraph;
    }

    public RunTimeStore getRunTimeStore() {
        return mRunTimeStore;
    }

    public AndroidNavigation getAndroidDisplay() {
        return mAndroidNavigation;
    }

    public RxEventBus getEventBus() {
        return mEventBus;
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
