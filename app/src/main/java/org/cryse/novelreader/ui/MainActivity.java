package org.cryse.novelreader.ui;

import android.app.Fragment;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;

import org.cryse.novelreader.R;
import org.cryse.novelreader.application.SmoothReaderApplication;
import org.cryse.novelreader.ui.common.AbstractThemeableActivity;
import org.cryse.novelreader.util.ColorUtils;
import org.cryse.novelreader.util.UIUtils;
import org.cryse.novelreader.util.navidrawer.AndroidDisplay;
import org.cryse.novelreader.util.navidrawer.NavigationDrawerItem;
import org.cryse.novelreader.util.navidrawer.NavigationDrawerView;
import org.cryse.novelreader.util.navidrawer.NavigationType;
import org.cryse.widget.ScrimInsetsFrameLayout;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;


public class MainActivity extends AbstractThemeableActivity {
    private static final String STATE_SELECTED_POSITION = "selected_navigation_drawer_position";
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @InjectView(R.id.navigationDrawerListViewWrapper)
    NavigationDrawerView mNavigationDrawerListViewWrapper;

    @InjectView(R.id.linearDrawer)
    ScrimInsetsFrameLayout mDrawerLayoutContainer;

    @InjectView(R.id.drawerLayout)
    DrawerLayout mDrawerLayout;

    @InjectView(R.id.leftDrawerListView)
    ListView leftDrawerListView;

    private ActionBarDrawerToggle mDrawerToggle;

    private List<NavigationDrawerItem> navigationItems;

    @Inject
    AndroidDisplay mDisplay;

    private int mCurrentSelectedPosition;

    private Handler mHandler = new Handler();
    private Runnable mPendingRunnable = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        SmoothReaderApplication.get(this).inject(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);

        //初始化Drawer导航数据
        initDrawerMenu();

        //Prepare the drawerToggle in order to be able to open/close the drawer
        mDrawerToggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                getToolbar(),
                R.string.app_name,
                R.string.app_name) {
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                super.onDrawerSlide(drawerView, slideOffset);
                if (getActionMode() != null) {
                    getActionMode().finish();
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                /*if (!isAdded()) {
                    return;
                }*/

                invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
                // If mPendingRunnable is not null, then add to the message queue
                if (mPendingRunnable != null) {
                    mHandler.post(mPendingRunnable);
                    mPendingRunnable = null;
                }
            }
        };
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getFragmentManager().getBackStackEntryCount() > 0)
                    getFragmentManager().popBackStack();
            }
        });
        //Attach the DrawerListener
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerLayout.setStatusBarBackgroundColor(ColorUtils.getColorFromAttr(this, R.attr.colorPrimaryDark));
        mDisplay.attach(this, mDrawerToggle);

        if(savedInstanceState != null) {
            mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
            onNavigationDrawerItemSelected(mCurrentSelectedPosition, true);
        } else {
            mCurrentSelectedPosition = 0;
            onNavigationDrawerItemSelected(mCurrentSelectedPosition, false);

        }

        selectItem(mCurrentSelectedPosition);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(mTitle);
    }

    public boolean isDrawerOpened() {
        if(mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mDrawerLayoutContainer)) {
            return true;
        }
        return false;
    }

    public void closeDrawer() {
        if (mDrawerLayoutContainer != null) {
            mDrawerLayout.closeDrawer(mDrawerLayoutContainer);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean isDrawerOpened = isDrawerOpened();
        for(int i = 0; i < menu.size(); i++){
            menu.getItem(i).setVisible(!isDrawerOpened);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (mDrawerLayout != null && !mDrawerLayout.isDrawerOpen(mDrawerLayoutContainer)) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            //getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                if (mDrawerLayout != null) {
                    if (mDrawerLayout.isDrawerOpen(mDrawerLayoutContainer)) {
                        mDrawerLayout.closeDrawer(mDrawerLayoutContainer);
                    } else {
                        mDrawerLayout.openDrawer(mDrawerLayoutContainer);
                    }
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {

        if (leftDrawerListView != null) {
            leftDrawerListView.setItemChecked(position, true);

            if(navigationItems.get(position).isMainItem()) {
                navigationItems.get(mCurrentSelectedPosition).setSelected(false);
                navigationItems.get(position).setSelected(true);

                mCurrentSelectedPosition = position;
            }
        }
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

        if(isDrawerOpened()) {
            closeDrawer();
            return;
        }

        if (getFragmentManager().popBackStackImmediate()) {
            return;
        }

        new MaterialDialog.Builder(this)
                .content(getString(R.string.dialog_exit_title, getString(R.string.app_name)))
                .theme(isNightMode() ? Theme.DARK : Theme.LIGHT)  // the default is light, so you don't need this line
                .positiveText(R.string.dialog_exit_confirm)  // the default is 'OK'
                .negativeText(R.string.dialog_exit_cancel)  // leaving this line out will remove the negative button
                .callback(new MaterialDialog.Callback() {
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

    @OnItemClick(R.id.leftDrawerListView)
    public void onItemClick(int position, long id) {
        if (mDrawerLayout.isDrawerOpen(mDrawerLayoutContainer)) {
            //mHandler.postDelayed(() -> mDrawerLayout.closeDrawer(mLinearDrawerLayout), 300);
            if(position != mCurrentSelectedPosition) {
                selectItem(position);
                mPendingRunnable = new Runnable() {
                    @Override
                    public void run() {
                        onNavigationDrawerItemSelected(position, false);
                    }
                };
                closeDrawer();
            }
        }
    }

    public void setToolbarTitleFromFragment(String title) {
        mTitle = title;
        setTitle(mTitle);
    }

    private void onNavigationDrawerItemSelected(int position, boolean fromSavedInstanceState) {
        if(!fromSavedInstanceState) {
            mDisplay.handleNaviDrawerSelection(navigationItems.get(position));
        }
    }

    private void initDrawerMenu() {
        navigationItems = new ArrayList<NavigationDrawerItem>();
        navigationItems.add(
                new NavigationDrawerItem(
                        getString(R.string.drawer_bookshelf),
                        NavigationType.NOVEL_BOOKSHELF_FRAGMENT,
                        R.drawable.ic_drawer_novel,
                        true,
                        true
                ));
        navigationItems.add(
                new NavigationDrawerItem(
                        getString(R.string.drawer_rank),
                        NavigationType.NOVEL_RANK_FRAGMENT,
                        R.drawable.ic_drawer_rank,
                        true,
                        false
                ));
        navigationItems.add(
                new NavigationDrawerItem(
                        getString(R.string.drawer_category),
                        NavigationType.NOVEL_CATEGORY_FRAGMENT,
                        R.drawable.ic_drawer_category,
                        true,
                        true
                ));
        navigationItems.add(
                new NavigationDrawerItem(
                        getString(R.string.drawer_settings),
                        NavigationType.NOVEL_SETTINGS_ACTIVITY,
                        R.drawable.ic_drawer_settings,
                        false,
                        false
                ));
        mNavigationDrawerListViewWrapper.replaceWith(navigationItems);
    }

    public void showDrawToggleAsUp(boolean showAsUp) {
        mDrawerToggle.setDrawerIndicatorEnabled(!showAsUp);
    }
}
