package org.cryse.novelreader.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.*;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.TextView;

import org.cryse.novelreader.R;

import java.lang.reflect.Method;
import java.util.regex.Pattern;

public class UIUtils {
    private static String sNavBarOverride;
    public static final float SESSION_BG_COLOR_SCALE_FACTOR = 0.65f;
    private static final int[] RES_IDS_ACTION_BAR_SIZE = { android.R.attr.actionBarSize };
    /**
     * Regex to search for HTML escape sequences.
     *
     * <p></p>Searches for any continuous string of characters starting with an ampersand and ending with a
     * semicolon. (Example: &amp;amp;)
     */
    private static final Pattern REGEX_HTML_ESCAPE = Pattern.compile(".*&\\S;.*");

    static {
        if(Build.VERSION.SDK_INT >= 19) {
            try {
                Class e = Class.forName("android.os.SystemProperties");
                Method m = e.getDeclaredMethod("get", new Class[]{String.class});
                m.setAccessible(true);
                sNavBarOverride = (String)m.invoke((Object)null, new Object[]{"qemu.hw.mainkeys"});
            } catch (Throwable var2) {
                sNavBarOverride = null;
            }
        }

    }

    public static float getDisplayDensity(Activity activity) {
        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
        return mDisplayMetrics.density;
    }

    /** Calculates the Action Bar height in pixels. */
    public static int calculateActionBarSize(Context context) {
        if (context == null) {
            return 0;
        }

        Resources.Theme curTheme = context.getTheme();
        if (curTheme == null) {
            return 0;
        }

        TypedArray att = curTheme.obtainStyledAttributes(RES_IDS_ACTION_BAR_SIZE);
        if (att == null) {
            return 0;
        }

        float size = att.getDimension(0, 0);
        att.recycle();
        return (int) size;
    }

    public static float getProgress(int value, int min, int max) {
        if (min == max) {
            throw new IllegalArgumentException("Max (" + max + ") cannot equal min (" + min + ")");
        }

        return (value - min) / (float) (max - min);
    }

    /**
     * Populate the given {@link android.widget.TextView} with the requested text, formatting
     * through {@link android.text.Html#fromHtml(String)} when applicable. Also sets
     * {@link android.widget.TextView#setMovementMethod} so inline links are handled.
     */
    public static void setTextMaybeHtml(TextView view, String text) {
        if (android.text.TextUtils.isEmpty(text)) {
            view.setText("");
            return;
        }
        if ((text.contains("<") && text.contains(">")) || REGEX_HTML_ESCAPE.matcher(text).find()) {
            view.setText(Html.fromHtml(text));
            view.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            view.setText(text);
        }
    }

    public static int setColorAlpha(int color, float alpha) {
        int alpha_int = Math.min(Math.max((int)(alpha * 255.0f), 0), 255);
        return Color.argb(alpha_int, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int scaleColor(int color, float factor, boolean scaleAlpha) {
        return Color.argb(scaleAlpha ? (Math.round(Color.alpha(color) * factor)) : Color.alpha(color),
                Math.round(Color.red(color) * factor), Math.round(Color.green(color) * factor),
                Math.round(Color.blue(color) * factor));
    }

    public static int scaleSessionColorToDefaultBG(int color) {
        return scaleColor(color, SESSION_BG_COLOR_SCALE_FACTOR, false);
    }

    public static int calculateStatusBarSize(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String addIndentToStart(String text) {
        if(text.substring(0,2).equals("\u3000\u3000"))
            return text;
        else
            return "\u3000\u3000" + text;
    }

    public static void setInsets(Activity context, View view, boolean translucentStatusBar, boolean traslucentNavBar, boolean withToolBar, boolean withCustomShadow) {
        SystemBarConfig systemBarConfig = new SystemBarConfig(context, translucentStatusBar, traslucentNavBar);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            // Pre-Kitkat
            int paddingTop = withToolBar ? calculateActionBarSize(context) : 0;
            if(withCustomShadow) paddingTop = paddingTop + context.getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height);
            view.setPadding(0, paddingTop, 0, 0);
        } else {
            // Kitkat
            int paddingTop = systemBarConfig.getPixelInsetTop(withToolBar);
            if(withCustomShadow) paddingTop = paddingTop + context.getResources().getDimensionPixelSize(R.dimen.toolbar_shadow_height);
            view.setPadding(0, paddingTop, systemBarConfig.getPixelInsetRight(), systemBarConfig.getPixelInsetBottom());
        }

    }

    public static int dp2px(Context context, float dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dp * scale + 0.5f);
    }

    public static class SystemBarConfig {
        private static final String STATUS_BAR_HEIGHT_RES_NAME = "status_bar_height";
        private static final String NAV_BAR_HEIGHT_RES_NAME = "navigation_bar_height";
        private static final String NAV_BAR_HEIGHT_LANDSCAPE_RES_NAME = "navigation_bar_height_landscape";
        private static final String NAV_BAR_WIDTH_RES_NAME = "navigation_bar_width";
        private static final String SHOW_NAV_BAR_RES_NAME = "config_showNavigationBar";
        private final boolean mTranslucentStatusBar;
        private final boolean mTranslucentNavBar;
        private final int mStatusBarHeight;
        private final int mActionBarHeight;
        private final boolean mHasNavigationBar;
        private final int mNavigationBarHeight;
        private final int mNavigationBarWidth;
        private final boolean mInPortrait;
        private final float mSmallestWidthDp;

        private SystemBarConfig(Activity activity, boolean translucentStatusBar, boolean traslucentNavBar) {
            Resources res = activity.getResources();
            this.mInPortrait = res.getConfiguration().orientation == 1;
            this.mSmallestWidthDp = this.getSmallestWidthDp(activity);
            this.mStatusBarHeight = this.getInternalDimensionSize(res, "status_bar_height");
            this.mActionBarHeight = this.getActionBarHeight(activity);
            this.mNavigationBarHeight = this.getNavigationBarHeight(activity);
            this.mNavigationBarWidth = this.getNavigationBarWidth(activity);
            this.mHasNavigationBar = this.mNavigationBarHeight > 0;
            this.mTranslucentStatusBar = translucentStatusBar;
            this.mTranslucentNavBar = traslucentNavBar;
        }

        @TargetApi(14)
        private int getActionBarHeight(Context context) {
            int result = 0;
            if(Build.VERSION.SDK_INT >= 14) {
                TypedValue tv = new TypedValue();
                context.getTheme().resolveAttribute(16843499, tv, true);
                result = TypedValue.complexToDimensionPixelSize(tv.data, context.getResources().getDisplayMetrics());
            }

            return result;
        }

        @TargetApi(14)
        private int getNavigationBarHeight(Context context) {
            Resources res = context.getResources();
            byte result = 0;
            if(Build.VERSION.SDK_INT >= 14 && this.hasNavBar(context)) {
                String key;
                if(this.mInPortrait) {
                    key = "navigation_bar_height";
                } else {
                    key = "navigation_bar_height_landscape";
                }

                return this.getInternalDimensionSize(res, key);
            } else {
                return result;
            }
        }

        @TargetApi(14)
        private int getNavigationBarWidth(Context context) {
            Resources res = context.getResources();
            byte result = 0;
            return Build.VERSION.SDK_INT >= 14 && this.hasNavBar(context)?this.getInternalDimensionSize(res, "navigation_bar_width"):result;
        }

        @TargetApi(14)
        private boolean hasNavBar(Context context) {
            Resources res = context.getResources();
            int resourceId = res.getIdentifier("config_showNavigationBar", "bool", "android");
            if(resourceId != 0) {
                boolean hasNav = res.getBoolean(resourceId);
                if("1".equals(UIUtils.sNavBarOverride)) {
                    hasNav = false;
                } else if("0".equals(UIUtils.sNavBarOverride)) {
                    hasNav = true;
                }

                return hasNav;
            } else {
                return !ViewConfiguration.get(context).hasPermanentMenuKey();
            }
        }

        private int getInternalDimensionSize(Resources res, String key) {
            int result = 0;
            int resourceId = res.getIdentifier(key, "dimen", "android");
            if(resourceId > 0) {
                result = res.getDimensionPixelSize(resourceId);
            }

            return result;
        }

        @SuppressLint({"NewApi"})
        private float getSmallestWidthDp(Activity activity) {
            DisplayMetrics metrics = new DisplayMetrics();
            if(Build.VERSION.SDK_INT >= 16) {
                activity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
            } else {
                activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
            }

            float widthDp = (float)metrics.widthPixels / metrics.density;
            float heightDp = (float)metrics.heightPixels / metrics.density;
            return Math.min(widthDp, heightDp);
        }

        public boolean isNavigationAtBottom() {
            return this.mSmallestWidthDp >= 600.0F || this.mInPortrait;
        }

        public int getStatusBarHeight() {
            return this.mStatusBarHeight;
        }

        public int getActionBarHeight() {
            return this.mActionBarHeight;
        }

        public boolean hasNavigtionBar() {
            return this.mHasNavigationBar;
        }

        public int getNavigationBarHeight() {
            return this.mNavigationBarHeight;
        }

        public int getNavigationBarWidth() {
            return this.mNavigationBarWidth;
        }

        public int getPixelInsetTop(boolean withActionBar) {
            return (this.mTranslucentStatusBar?this.mStatusBarHeight:0) + (withActionBar?this.mActionBarHeight:0);
        }

        public int getPixelInsetBottom() {
            return this.mTranslucentNavBar && this.isNavigationAtBottom()?this.mNavigationBarHeight:0;
        }

        public int getPixelInsetRight() {
            return this.mTranslucentNavBar && !this.isNavigationAtBottom()?this.mNavigationBarWidth:0;
        }
    }
}
