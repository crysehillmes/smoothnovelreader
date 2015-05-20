package org.cryse.changelog;

import java.io.IOException;
        import java.util.ArrayList;
        import java.util.Collections;
        import java.util.Comparator;
        import java.util.List;

        import org.xmlpull.v1.XmlPullParser;
        import org.xmlpull.v1.XmlPullParserException;

        import android.content.Context;
        import android.content.SharedPreferences;
        import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
        import android.content.res.XmlResourceParser;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
        import android.util.SparseArray;

/**
 * Display a dialog showing a full or partial (What's New) change log.
 */
@SuppressWarnings("UnusedDeclaration")
public class ChangelogUtils {
    /**
     * Tag that is used when sending error/debug messages to the log.
     */
    protected static final String LOG_TAG = "ckChangeLog";

    /**
     * This is the key used when storing the version code in SharedPreferences.
     */
    protected static final String VERSION_KEY = "ckChangeLog_last_version_code";

    /**
     * Constant that used when no version code is available.
     */
    protected static final int NO_VERSION = -1;

    /**
     * Context that is used to access the resources and to create the ChangeLog dialogs.
     */
    protected final Context mContext;

    /**
     * Contains the CSS rules used to format the change log.
     */
    protected final int mChangelogResId;

    /**
     * Last version code read from {@code SharedPreferences} or {@link #NO_VERSION}.
     */
    private int mLastVersionCode;

    /**
     * Version code of the current installation.
     */
    private int mCurrentVersionCode;

    /**
     * Version name of the current installation.
     */
    private String mCurrentVersionName;


    /**
     * Contains constants for the root element of {@code changelog.xml}.
     */
    protected interface ChangeLogTag {
        static final String NAME = "changelog";
    }

    /**
     * Contains constants for the release element of {@code changelog.xml}.
     */
    protected interface ReleaseTag {
        static final String NAME = "release";
        static final String ATTRIBUTE_VERSION = "version";
        static final String ATTRIBUTE_VERSION_CODE = "versioncode";
    }

    /**
     * Contains constants for the change element of {@code changelog.xml}.
     */
    protected interface ChangeTag {
        static final String NAME = "change";
    }

    /**
     * Create a {@code ChangeLog} instance using the default {@link SharedPreferences} file.
     *
     * @param context
     *         Context that is used to access the resources and to create the ChangeLog dialogs.
     * @param changeResId
     *         Change log resource id that contains the change log.
     */
    public ChangelogUtils(Context context, int changeResId) {
        this(context, PreferenceManager.getDefaultSharedPreferences(context), changeResId);
    }

    /**
     * Create a {@code ChangeLog} instance using the supplied {@code SharedPreferences} instance.
     *
     * @param context
     *         Context that is used to access the resources and to create the ChangeLog dialogs.
     * @param preferences
     *         {@code SharedPreferences} instance that is used to persist the last version code.
     * @param changeResId
     *         Change log resource id that contains the change log.
     *
     */
    public ChangelogUtils(Context context, SharedPreferences preferences, int changeResId) {
        mContext = context;
        mChangelogResId = changeResId;

        // Get last version code
        mLastVersionCode = preferences.getInt(VERSION_KEY, NO_VERSION);

        // Get current version code and version name
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), 0);

            mCurrentVersionCode = packageInfo.versionCode;
            mCurrentVersionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            mCurrentVersionCode = NO_VERSION;
            Log.e(LOG_TAG, "Could not get version information from manifest!", e);
        }
    }

    /**
     * Get version code of last installation.
     *
     * @return The version code of the last installation of this app (as described in the former
     *         manifest). This will be the same as returned by {@link #getCurrentVersionCode()} the
     *         second time this version of the app is launched (more precisely: the second time
     *         {@code ChangeLog} is instantiated).
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vcode">android:versionCode</a>
     */
    public int getLastVersionCode() {
        return mLastVersionCode;
    }

    /**
     * Get version code of current installation.
     *
     * @return The version code of this app as described in the manifest.
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vcode">android:versionCode</a>
     */
    public int getCurrentVersionCode() {
        return mCurrentVersionCode;
    }

    /**
     * Get version name of current installation.
     *
     * @return The version name of this app as described in the manifest.
     *
     * @see <a href="http://developer.android.com/guide/topics/manifest/manifest-element.html#vname">android:versionName</a>
     */
    public String getCurrentVersionName() {
        return mCurrentVersionName;
    }

    /**
     * Check if this is the first execution of this app version.
     *
     * @return {@code true} if this version of your app is started the first time.
     */
    public boolean isFirstRun() {
        return mLastVersionCode < mCurrentVersionCode;
    }

    /**
     * Check if this is a new installation.
     *
     * @return {@code true} if your app including {@code ChangeLog} is started the first time ever.
     *         Also {@code true} if your app was uninstalled and installed again.
     */
    public boolean isFirstRunEver() {
        return mLastVersionCode == NO_VERSION;
    }

    /**
     * Skip the "What's new" dialog for this app version.
     *
     * <p>
     * Future calls to {@link #isFirstRun()} and {@link #isFirstRunEver()} will return {@code false}
     * for the current app version.
     * </p>
     */
    public void skipLogDialog() {
        updateVersionInPreferences();
    }

    /**
     * Write current version code to the preferences.
     */
    protected void updateVersionInPreferences() {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(mContext);
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(VERSION_KEY, mCurrentVersionCode);

        // TODO: Update preferences from a background thread
        editor.commit();
    }

    /**
     * Convert the change log to Spannable String.
     *
     * @param fullLog
     *         If this is {@code true} the full change log is returned. Otherwise only changes for
     *         versions newer than the last version are returned.
     *
     * @return The (partial) change log.
     */
    public CharSequence toSpannable(boolean fullLog) {
        List<ReleaseItem> changelog = getChangeLog(fullLog);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (ReleaseItem release : changelog) {
            int versionStart = stringBuilder.length();
            int versionEnd = versionStart + release.versionName.length();
            stringBuilder.append(release.versionName);
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RelativeSizeSpan(1.2f), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append("\n");
            for (String change : release.changes) {
                int changeStart = stringBuilder.length();
                int changeEnd = changeStart + change.length();
                stringBuilder.append("    \u2022    " + change);
                //stringBuilder.setSpan(new BulletSpan(15), changeStart, changeEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }


    /**
     * Returns the merged change log.
     *
     * @param full
     *         If this is {@code true} the full change log is returned. Otherwise only changes for
     *         versions newer than the last version are returned.
     *
     * @return A sorted {@code List} containing {@link ReleaseItem}s representing the (partial)
     *         change log.
     *
     * @see #getChangeLogComparator()
     */
    public List<ReleaseItem> getChangeLog(boolean full) {
        SparseArray<ReleaseItem> changelog = getLocalizedChangeLog(full);

        List<ReleaseItem> mergedChangeLog =
                new ArrayList<ReleaseItem>(changelog.size());

        for (int i = 0, len = changelog.size(); i < len; i++) {
            ReleaseItem release = changelog.valueAt(i);
            mergedChangeLog.add(release);
        }

        Collections.sort(mergedChangeLog, getChangeLogComparator());

        return mergedChangeLog;
    }

    /**
     * Read localized change log from {@code xml[-lang]/changelog.xml}
     *
     * @see #readChangeLogFromResource(int, boolean)
     */
    protected SparseArray<ReleaseItem> getLocalizedChangeLog(boolean full) {
        return readChangeLogFromResource(mChangelogResId, full);
    }

    /**
     * Read change log from XML resource file.
     *
     * @param resId
     *         Resource ID of the XML file to read the change log from.
     * @param full
     *         If this is {@code true} the full change log is returned. Otherwise only changes for
     *         versions newer than the last version are returned.
     *
     * @return A {@code SparseArray} containing {@link ReleaseItem}s representing the (partial)
     *         change log.
     */
    protected final SparseArray<ReleaseItem> readChangeLogFromResource(int resId, boolean full) {
        XmlResourceParser xml = mContext.getResources().getXml(resId);
        try {
            return readChangeLog(xml, full);
        } finally {
            xml.close();
        }
    }

    /**
     * Read the change log from an XML file.
     *
     * @param xml
     *         The {@code XmlPullParser} instance used to read the change log.
     * @param full
     *         If {@code true} the full change log is read. Otherwise only the changes since the
     *         last (saved) version are read.
     *
     * @return A {@code SparseArray} mapping the version codes to release information.
     */
    protected SparseArray<ReleaseItem> readChangeLog(XmlPullParser xml, boolean full) {
        SparseArray<ReleaseItem> result = new SparseArray<ReleaseItem>();

        try {
            int eventType = xml.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && xml.getName().equals(ReleaseTag.NAME)) {
                    if (parseReleaseTag(xml, full, result)) {
                        // Stop reading more elements if this entry is not newer than the last
                        // version.
                        break;
                    }
                }
                eventType = xml.next();
            }
        } catch (XmlPullParserException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
        }

        return result;
    }

    /**
     * Parse the {@code release} tag of a change log XML file.
     *
     * @param xml
     *         The {@code XmlPullParser} instance used to read the change log.
     * @param full
     *         If {@code true} the contents of the {@code release} tag are always added to
     *         {@code changelog}. Otherwise only if the item's {@code versioncode} attribute is
     *         higher than the last version code.
     * @param changelog
     *         The {@code SparseArray} to add a new {@link ReleaseItem} instance to.
     *
     * @return {@code true} if the {@code release} element is describing changes of a version older
     *         or equal to the last version. In that case {@code changelog} won't be modified and
     *         {@link #readChangeLog(XmlPullParser, boolean)} will stop reading more elements from
     *         the change log file.
     *
     * @throws XmlPullParserException
     * @throws IOException
     */
    private boolean parseReleaseTag(XmlPullParser xml, boolean full,
                                    SparseArray<ReleaseItem> changelog) throws XmlPullParserException, IOException {

        String version = xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION);

        int versionCode;
        try {
            String versionCodeStr = xml.getAttributeValue(null, ReleaseTag.ATTRIBUTE_VERSION_CODE);
            versionCode = Integer.parseInt(versionCodeStr);
        } catch (NumberFormatException e) {
            versionCode = NO_VERSION;
        }

        if (!full && versionCode <= mLastVersionCode) {
            return true;
        }

        int eventType = xml.getEventType();
        List<String> changes = new ArrayList<String>();
        while (eventType != XmlPullParser.END_TAG || xml.getName().equals(ChangeTag.NAME)) {
            if (eventType == XmlPullParser.START_TAG && xml.getName().equals(ChangeTag.NAME)) {
                eventType = xml.next();

                changes.add(xml.getText());
            }
            eventType = xml.next();
        }

        ReleaseItem release = new ReleaseItem(versionCode, version, changes);
        changelog.put(versionCode, release);

        return false;
    }

    /**
     * Returns a {@link Comparator} that specifies the sort order of the {@link ReleaseItem}s.
     *
     * <p>
     * The default implementation returns the items in reverse order (latest version first).
     * </p>
     */
    protected Comparator<ReleaseItem> getChangeLogComparator() {
        return new Comparator<ReleaseItem>() {
            @Override
            public int compare(ReleaseItem lhs, ReleaseItem rhs) {
                if (lhs.versionCode < rhs.versionCode) {
                    return 1;
                } else if (lhs.versionCode > rhs.versionCode) {
                    return -1;
                } else {
                    return 0;
                }
            }
        };
    }

    /**
     * Container used to store information about a release/version.
     */
    public static class ReleaseItem {
        /**
         * Version code of the release.
         */
        public final int versionCode;

        /**
         * Version name of the release.
         */
        public final String versionName;

        /**
         * List of changes introduced with that release.
         */
        public final List<String> changes;

        ReleaseItem(int versionCode, String versionName, List<String> changes) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.changes = changes;
        }
    }
}