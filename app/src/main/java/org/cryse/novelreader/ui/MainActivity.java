package org.cryse.novelreader.ui;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;

import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.accountswitcher.AccountHeader;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.squareup.picasso.Picasso;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.event.AbstractEvent;
import org.cryse.novelreader.event.ThemeColorChangedEvent;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.analytics.AnalyticsUtils;
import org.cryse.novelreader.util.navidrawer.AndroidNavigation;

import java.util.concurrent.Executors;

import javax.inject.Inject;

public class MainActivity extends AbstractThemeableActivity {
    private static final String LOG_TAG = MainActivity.class.getName();
    @Inject
    AndroidNavigation mNavigation;

    AccountHeader.Result mAccountHeader;
    Drawer.Result mNaviagtionDrawer;

    Picasso mPicasso;

    int mCurrentSelection = 0;
    boolean mIsRestorePosition = false;
    /**
     * Used to post delay navigation action to improve UX
     */
    private Handler mHandler = new Handler();
    private Runnable mPendingRunnable = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        injectThis();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpToolbar(R.id.my_awesome_toolbar, R.id.toolbar_shadow);
        mPicasso = new Picasso.Builder(this).executor(Executors.newSingleThreadExecutor()).build();
        setIsOverrideStatusBarColor(false);
        mNavigation.attachMainActivity(this);
        /*setDrawerLayoutBackground(isNightMode());
        getDrawerLayout().setStatusBarBackgroundColor(getThemeEngine().getPrimaryDarkColor(this));*/
        if(savedInstanceState!=null && savedInstanceState.containsKey("selection_item_position")) {
            mCurrentSelection = savedInstanceState.getInt("selection_item_position");
            mIsRestorePosition = true;
        } else {
            mCurrentSelection = 1001;
            mIsRestorePosition = false;
        }
        initDrawer();

    }

    private void initDrawer() {
        AccountHeader accountHeader = new AccountHeader()
                .withActivity(this)
                .withHeaderBackground(isNightMode() ? R.drawable.drawer_top_image_dark : R.drawable.drawer_top_image_light);
        //Now create your drawer and pass the AccountHeader.Result
        mAccountHeader = accountHeader.build();
        mNaviagtionDrawer = new Drawer()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(mAccountHeader)
                .withStatusBarColor(getThemeEngine().getPrimaryDarkColor(this))
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_bookshelf).withIcon(R.drawable.ic_drawer_novel).withIdentifier(1001),
                        new PrimaryDrawerItem().withName(R.string.drawer_rank).withIcon(R.drawable.ic_drawer_rank).withIdentifier(1002),
                        new PrimaryDrawerItem().withName(R.string.drawer_category).withIcon(R.drawable.ic_drawer_category).withIdentifier(1003),
                        new DividerDrawerItem(),
                        new SecondaryDrawerItem().withName(R.string.drawer_settings).withIdentifier(1101).withIcon(R.drawable.ic_drawer_settings).withCheckable(false)

                )
                .withOnDrawerNavigationListener(view -> getSupportFragmentManager().popBackStackImmediate())
                .withOnDrawerListener(new Drawer.OnDrawerListener() {
                    @Override
                    public void onDrawerOpened(View view) {

                    }

                    @Override
                    public void onDrawerClosed(View view) {
                        supportInvalidateOptionsMenu();
                        // If mPendingRunnable is not null, then add to the message queue
                        if (mPendingRunnable != null) {
                            mHandler.post(mPendingRunnable);
                            mPendingRunnable = null;
                        }
                    }

                    @Override
                    public void onDrawerSlide(View view, float v) {

                    }
                })
                .withOnDrawerItemClickListener((parent, view, position, id, drawerItem) -> {
                    // do something with the clicked item :D
                    if (drawerItem.getType().equalsIgnoreCase("PRIMARY_ITEM"))
                        mCurrentSelection = drawerItem.getIdentifier();
                    mPendingRunnable = () ->  onNavigationSelected(drawerItem);
                })
                .build();
        if(mCurrentSelection == 1001 && !mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(1001, false);
            mNavigation.navigateToBookShelfFragment();
        } else if(mIsRestorePosition) {
            mNaviagtionDrawer.setSelectionByIdentifier(mCurrentSelection, false);
        }

    }

    private void onNavigationSelected(IDrawerItem drawerItem) {
        switch (drawerItem.getIdentifier()) {
            case 1001:
                mNavigation.navigateToBookShelfFragment();
                break;
            case 1002:
                mNavigation.navigateToRankFragment();
                break;
            case 1003:
                mNavigation.navigateToCategoryListFragment();
                break;
            case 1101:
                mNavigation.navigateToSettingsActivity(MainActivity.this);
                break;
            default:
                throw new IllegalArgumentException("Unknown NavigationDrawerItem position.");
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("selection_item_position", mCurrentSelection);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPicasso.shutdown();
    }

    @Override
    protected void injectThis() {
        SmoothReaderApplication.get(this).inject(this);
    }

    @Override
    protected void analyticsTrackEnter() {
        AnalyticsUtils.trackFragmentActivityEnter(this, LOG_TAG);
    }

    @Override
    protected void analyticsTrackExit() {
        AnalyticsUtils.trackFragmentActivityExit(this, LOG_TAG);
    }

    public void onSectionAttached(String title) {
        setTitle(title);
    }

    @Override
    protected void onEvent(AbstractEvent event) {
        super.onEvent(event);
        if (event instanceof ThemeColorChangedEvent) {
            mNaviagtionDrawer.setStatusBarColor(((ThemeColorChangedEvent) event).getNewPrimaryDarkColor());
            mNaviagtionDrawer.getContent().invalidate();
            // setDrawerSelectedItemColor(((ThemeColorChangedEvent) event).getNewPrimaryColorResId());
        }
    }

    public void showDrawToggleAsUp(boolean showAsUp) {
        mNaviagtionDrawer.getActionBarDrawerToggle().setDrawerIndicatorEnabled(!showAsUp);
    }

    @Override
    public void onBackPressed() {

        if (getSupportActionBar() != null && getSupportActionBar().collapseActionView()) {
            return;
        }

        if (getActionMode() != null) {
            getActionMode().finish();
            return;
        }

        if(mNaviagtionDrawer != null && mNaviagtionDrawer.isDrawerOpen()) {
            mNaviagtionDrawer.closeDrawer();
            return;
        }

        if (getSupportFragmentManager().popBackStackImmediate()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .content(getString(R.string.dialog_exit_title, getString(R.string.app_name)))
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText(R.string.dialog_exit_confirm)  // the default is 'OK'
                .negativeText(R.string.dialog_exit_cancel)  // leaving this line out will remove the negative button
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        dialog.dismiss();
                        MainActivity.this.finish();
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {

                        dialog.dismiss();
                    }
                })
                .build()
                .show();
    }
}
