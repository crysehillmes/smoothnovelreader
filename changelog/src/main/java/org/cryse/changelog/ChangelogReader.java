package org.cryse.changelog;

import android.content.Context;
import android.graphics.Typeface;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.BulletSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ChangelogReader {
    List<Version> mVersions;

    public ChangelogReader() {

    }

    public void fromAsset(Context context, String filePath) throws IOException, JSONException {
        String jsonContent = readFromAssets(context, filePath);
        JSONArray jsonArray = new JSONArray(jsonContent);
        int versionCount = jsonArray.length();
        mVersions = new ArrayList<>(versionCount);
        for(int i = 0; i < versionCount; i++) {
            JSONObject jsonVersion = jsonArray.getJSONObject(i);
            Version version = new Version();
            version.setVersionCode(Integer.valueOf(jsonVersion.getString("versionCode")));
            version.setVersionName(jsonVersion.getString("versionName"));
            JSONArray jsonDescriptions = jsonVersion.getJSONArray("versionDescriptions");
            int descriptionCount = jsonDescriptions.length();
            for(int j = 0; j < descriptionCount; j++) {
                JSONObject jsonDescription = jsonDescriptions.getJSONObject(j);
                VersionDescription description = new VersionDescription();
                description.setDescription(jsonDescription.getString("description"));
                version.getVersionDescriptions().add(description);
            }
            JSONArray jsonChanges = jsonVersion.getJSONArray("versionChanges");
            int changeCount = jsonChanges.length();
            for(int k = 0; k < changeCount; k++) {
                JSONObject jsonChange = jsonChanges.getJSONObject(k);
                VersionChange change = new VersionChange();
                change.setChange(jsonChange.getString("change"));
                version.getVersionChanges().add(change);
            }
            mVersions.add(version);
        }
    }

    private String readFromAssets(Context context, String filePath) throws IOException {
        StringBuilder buffer = new StringBuilder();
        InputStream inputStream = context.getAssets().open(filePath);
        BufferedReader textReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        String str;
        while ((str = textReader.readLine()) != null) {
            buffer.append(str);
        }
        textReader.close();
        return buffer.toString();
    }

    public CharSequence toSpannable() {
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder();
        for(Version version : mVersions) {
            int versionStart = stringBuilder.length();
            int versionEnd = versionStart + version.getVersionName().length();
            stringBuilder.append(version.getVersionName());
            stringBuilder.setSpan(new StyleSpan(Typeface.BOLD), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.setSpan(new RelativeSizeSpan(1.2f), versionStart, versionEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            stringBuilder.append("\n");
            for (VersionDescription versionDescription : version.getVersionDescriptions()) {
                int descriptionStart = stringBuilder.length();
                int descriptionEnd = descriptionStart + versionDescription.getDescription().length();
                stringBuilder.append(versionDescription.getDescription());
                stringBuilder.append("\n");
            }
            for (VersionChange versionChange : version.getVersionChanges()) {
                int changeStart = stringBuilder.length();
                int changeEnd = changeStart + versionChange.getChange().length();
                stringBuilder.append("    \u2022    " + versionChange.getChange());
                //stringBuilder.setSpan(new BulletSpan(15), changeStart, changeEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                stringBuilder.append("\n");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder;
    }
}
