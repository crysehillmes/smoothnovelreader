package org.cryse.changelog;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import java.util.LinkedList;

public class ChangeLogUtils {
    ChangeLog mChangeLog;
    public ChangeLogUtils(Context context, int changeLogResId) {
        XmlParser xmlParser = new XmlParser(context, changeLogResId);
        try {
            this.mChangeLog = xmlParser.readChangeLogFile();
        } catch (Exception e) {
            mChangeLog = new ChangeLog();
        }
    }

    public CharSequence toSpannable() {
        LinkedList<ChangeLogRow> rows = mChangeLog.getRows();
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for (ChangeLogRow row : rows) {
            if(row.isHeader()) {
                int versionStart = stringBuilder.length();
                int versionEnd = versionStart + row.getVersionName().length();
                stringBuilder.append(row.getVersionName());
                stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.setSpan(new RelativeSizeSpan(1.5f), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                CharSequence displayChange = Html.fromHtml(row.getChangeText());
                int changeStart = stringBuilder.length();
                int changeEnd = changeStart + displayChange.length();
                stringBuilder.append("    \u2022  ").append(displayChange);
                //stringBuilder.setSpan(new BulletSpan(15), changeStart, changeEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                //stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }
}
