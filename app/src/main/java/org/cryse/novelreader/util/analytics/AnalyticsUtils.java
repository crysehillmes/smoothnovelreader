package org.cryse.novelreader.util.analytics;

import android.content.Context;
import android.support.v4.app.Fragment;

/*import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;*/
import com.umeng.analytics.AnalyticsConfig;
import com.umeng.analytics.MobclickAgent;

import org.cryse.novelreader.R;

public class AnalyticsUtils {
    /*private static Tracker sTracker;*/

    public static void init(Context context, String appKey) {
        AnalyticsConfig.setAppkey(appKey);
        MobclickAgent.openActivityDurationTrack(false);
        AnalyticsConfig.enableEncrypt(true);
        /*if (sTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(context);
            // To enable debug logging use: adb shell setprop log.tag.GAv4 DEBUG
            sTracker = analytics.newTracker(R.xml.global_tracker);
        }*/
    }

    public static void trackActivityEnter(Context context, Object... args) {
        String name = (String)args[0];
        MobclickAgent.onPageStart(name); //统计页面
        MobclickAgent.onResume(context);
        /*sTracker.setScreenName("Activity: " + name);
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
    }

    public static void trackActivityExit(Context context, Object... args) {
        String name = (String)args[0];
        MobclickAgent.onPageEnd((String)args[0]);
        MobclickAgent.onPause(context);
        /*sTracker.setScreenName("Activity: " + name);
        sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
    }

    public static void trackFragmentActivityEnter(Context context, Object... args) {
        if(args.length > 0) {
            String name = (String)args[0];
            MobclickAgent.onResume(context);
            /*sTracker.setScreenName("FragmentActivity: " + name);
            sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        }
    }

    public static void trackFragmentActivityExit(Context context, Object... args) {
        if(args.length > 0) {
            String name = (String)args[0];
            MobclickAgent.onPause(context);
            /*sTracker.setScreenName("FragmentActivity: " + name);
            sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        }
    }

    public static void trackFragmentEnter(Fragment fragment, Object... args) {
        if(args.length > 0) {
            String name = (String)args[0];
            MobclickAgent.onPageStart(name);
            /*sTracker.setScreenName("Fragment: " + name);
            sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        }
    }

    public static void trackFragmentExit(Fragment fragment, Object... args) {
        if(args.length > 0) {
            String name = (String)args[0];
            MobclickAgent.onPageEnd(name);
            /*sTracker.setScreenName("Fragment: " + name);
            sTracker.send(new HitBuilders.ScreenViewBuilder().build());*/
        }
    }
}