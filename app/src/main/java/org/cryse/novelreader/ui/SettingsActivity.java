package org.cryse.novelreader.ui;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;

public class SettingsActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = SettingsActivity.class.getName();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction =
                fragmentManager.beginTransaction();
        Fragment fragment = null;
        if(getIntent().hasExtra("type")) {
            if(getIntent().getStringExtra("type").compareTo("about") == 0) {
                fragment = new AboutFragment();
                this.setTitle(getString(R.string.settings_about_activity_title));
            }
        }
        if(fragment == null) {
            fragment = new SettingsFragment();
            this.setTitle(getString(R.string.drawer_settings));
        }
        fragmentTransaction.replace(R.id.content_frame, fragment);
        fragmentTransaction.commit();

    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(this).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackActivityExit(this, LOG_TAG);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
