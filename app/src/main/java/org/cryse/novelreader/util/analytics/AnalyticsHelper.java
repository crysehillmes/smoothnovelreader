package org.cryse.novelreader.util.analytics;

import android.app.Fragment;
import android.content.Context;

import com.umeng.analytics.MobclickAgent;

public class AnalyticsHelper {
    public static void init() {
        MobclickAgent.openActivityDurationTrack(false);
    }

    public static void trackActivityEnter(Context context, Object... args) {
        MobclickAgent.onResume(context);
    }

    public static void trackActivityExit(Context context, Object... args) {
        MobclickAgent.onPause(context);
    }

    public static void trackFragmentEnter(Fragment fragment, Object... args) {
        if(args.length > 0)
            MobclickAgent.onPageStart((String)args[0]);
    }

    public static void trackFragmentExit(Fragment fragment, Object... args) {
        if(args.length > 0)
            MobclickAgent.onPageEnd((String)args[0]);
    }
}
