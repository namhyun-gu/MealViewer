/*
 * Copyright 2016 Namhyun, Gu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.earlier.yma.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.BuildConfig;
import com.earlier.yma.R;
import com.earlier.yma.data.MealDataManager;
import com.earlier.yma.data.model.RequestObject;
import com.earlier.yma.data.model.preference.Time;
import com.earlier.yma.service.MealFetchService;
import com.earlier.yma.ui.fragment.MainFragment;
import com.earlier.yma.util.Prefs;
import com.earlier.yma.util.RxBus;
import com.earlier.yma.util.Util;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final String TAG = getClass().getSimpleName();
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.drawer_layout) DrawerLayout mDrawerLayout;
    @BindView(R.id.view_pager) ViewPager mViewPager;
    @BindView(R.id.tab_layout) TabLayout mTabLayout;
    @BindView(R.id.nav_view) NavigationView mNavigationView;
    @BindView(R.id.shadow_view) View mShadowView;
    @BindView(R.id.coordinator_layout) CoordinatorLayout mCoordinatorLayout;
    private MealFetchReceiver mBroadcastReceiver = new MealFetchReceiver();
    private Snackbar mSnackbar;
    private int dateIndex = Util.getDayIndexFromCalendar(Util.getTodayCalender());
    private FirebaseAnalytics mFirebaseAnalytics;

    @OnClick(R.id.fab)
    public void onClickFab() {
        List<String> itemsList = new ArrayList<>();
        for (int index = 0; index <= 6; index++) {
            itemsList.add(Util.getDateString(this, index));
        }

        new MaterialDialog.Builder(this)
                .title(R.string.dialog_change_date)
                .items(itemsList)
                .itemsCallbackSingleChoice(dateIndex, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        dateIndex = which;
                        sendEvent();
                        updateTitle();
                        dialog.dismiss();
                        return true;
                    }
                })
                .negativeText(android.R.string.cancel)
                .show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Bind Butterknife
        ButterKnife.bind(this);

        // Initial Toolbar
        setSupportActionBar(mToolbar);

        // Toolbar Shadow (Pre lollipop)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mShadowView.setVisibility(View.VISIBLE);
        }

        // Initial Drawer
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,
                mDrawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Initial ViewPager
        final String[] typeTitles = {
                getString(R.string.action_breakfast),
                getString(R.string.action_lunch),
                getString(R.string.action_dinner)
        };

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        for (int typeIndex = 1; typeIndex <= 3; typeIndex++) {
            adapter.addFragment(MainFragment.newInstance(typeIndex), typeTitles[typeIndex - 1]);
        }

        mViewPager.setOffscreenPageLimit(3); // Pre-loading page limit
        mViewPager.setAdapter(adapter);

        // Initial Tab Layout
        mTabLayout.setupWithViewPager(mViewPager);

        // Initial NavigationView
        mNavigationView.setNavigationItemSelectedListener(this);
        initNavigationViewHeader();
    }

    @Override
    protected void onStart() {
        super.onStart();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean isInitialized = pref.getBoolean(Prefs.IS_INITIALIZED, false);
        if (!isInitialized) {
            Log.d(TAG, "Not initialized");
            initFirstRun();
        } else {
            boolean isUpdatePrevVersion = pref.getBoolean(Prefs.IS_UPDATE_PREV_VERSION, false);
            if (!isUpdatePrevVersion) {
                Log.d(TAG, "Update prev version");
                Toast.makeText(this, getString(R.string.toast_update_prev_version), Toast.LENGTH_SHORT).show();
                initFirstRun();
            } else {
                Log.d(TAG, "Already initialized");
                String path = pref.getString(Prefs.SCHOOL_INFO_PATH, null);
                String schulCode = pref.getString(Prefs.SCHOOL_INFO_SCHUL_CODE, null);
                String schulCrseScCode = pref.getString(Prefs.SCHOOL_INFO_SCHUL_CRSE_SC_CODE, null);
                String schulKndScCode = pref.getString(Prefs.SCHOOL_INFO_SCHUL_KND_SC_CODE, null);

                Bundle bundle = new Bundle();
                bundle.putString("country_code", path);
                bundle.putString("school_code", schulCode);
                bundle.putString("school_classified", schulCrseScCode);
                bundle.putString("school_category", schulKndScCode);
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.APP_OPEN, bundle);

                if (BuildConfig.DEBUG) {
                    Log.d(TAG, "debug: current user pref state");
                    Log.d(TAG, String.format("debug: path = %s", path));
                    Log.d(TAG, String.format("debug: schulCode = %s", schulCode));
                    Log.d(TAG, String.format("debug: schulCrseScCode = %s", schulCrseScCode));
                }

                if (path != null && schulCode != null
                        && schulCrseScCode != null && schulKndScCode != null) {
                    RequestObject request = new RequestObject.Builder()
                            .path(path)
                            .schulCode(schulCode)
                            .schulCrseScCode(schulCrseScCode)
                            .schulKndScCode(schulKndScCode)
                            .build();
                    MealFetchService.startAction(this, request);
                }
            }
        }

        if (pref.getBoolean(getString(R.string.pref_time_based_menu_change_key), false)) {
            Gson gson = new Gson();
            boolean enableBreakfastChanged =
                    pref.getBoolean(getString(R.string.pref_breakfast_enable_key), false);

            Calendar c = Calendar.getInstance();
            int currentHour = c.get(Calendar.HOUR_OF_DAY);
            int currentMinute = c.get(Calendar.MINUTE);
            Time currentTime = new Time(currentHour, currentMinute);
            Time lunchTime =
                    gson.fromJson(pref.getString(getString(R.string.pref_time_lunch_key), null), Time.class);
            Time dinnerTime =
                    gson.fromJson(pref.getString(getString(R.string.pref_time_dinner_key), null), Time.class);
            if (enableBreakfastChanged) {
                Time breakfastTime =
                        gson.fromJson(pref.getString(getString(R.string.pref_time_breakfast_key), null), Time.class);

                if (currentTime.compareTo(breakfastTime) >= 0 && currentTime.compareTo(lunchTime) < 0) {
                    Log.d(TAG, "Time based changed - breakfast");
                    mViewPager.setCurrentItem(0);
                }
            }
            if (currentTime.compareTo(lunchTime) >= 0 && currentTime.compareTo(dinnerTime) < 0) {
                Log.d(TAG, "Time based changed - lunch");
                mViewPager.setCurrentItem(1);
            } else if (currentTime.compareTo(dinnerTime) >= 0) {
                Log.d(TAG, "Time based changed - dinner");
                mViewPager.setCurrentItem(2);
            }
        } else {
            String value = pref.getString(getString(R.string.pref_default_menu_key), "0");
            mViewPager.setCurrentItem(Integer.parseInt(value));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "Register receiver");
        if (mBroadcastReceiver != null) {
            IntentFilter intentFilter = new IntentFilter(MealFetchService.ACTION_FETCH_SIGNAL);
            registerReceiver(mBroadcastReceiver, intentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "Unregister receiver");
        if (mBroadcastReceiver != null) {
            unregisterReceiver(mBroadcastReceiver);
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
            return;
        }
        if (mSnackbar == null)
            mSnackbar = Snackbar.make(mCoordinatorLayout,
                    R.string.snackbar_backbutton_pressed_content, Snackbar.LENGTH_SHORT);
        if (!mSnackbar.isShown()) {
            mSnackbar.show();
        } else {
            mSnackbar.dismiss();
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Intent prefIntent = new Intent(this, PrefActivity.class);
        switch (item.getItemId()) {
            case R.id.nav_settings:
                prefIntent.putExtra(PrefActivity.BUNDLE_TYPE, PrefActivity.TYPE_SETTINGS);
                startActivity(prefIntent);
                break;
            case R.id.nav_info:
                prefIntent.putExtra(PrefActivity.BUNDLE_TYPE, PrefActivity.TYPE_INFORMATION);
                startActivity(prefIntent);
                break;
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Update title
     */
    private void updateTitle() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setTitle(Util.getDateString(this, dateIndex));
    }

    /**
     * Send event by {@link RxBus}
     */
    private void sendEvent() {
        RxBus bus = RxBus.getInstance();
        if (bus.hasObservers()) {
            bus.send(new MainActivity.DataUpdateEvent(dateIndex));
        }
    }

    /**
     * Initialize first run
     * <p/>
     * - Clear legacy file
     * - Reset preference
     * - Start school search activity
     */
    private void initFirstRun() {
        // Clear legacy data
        MealDataManager.getInstance().clearLegacyFiles();
        PreferenceManager.getDefaultSharedPreferences(this)
                .edit()
                .clear()
                .apply();

        new MaterialDialog.Builder(this)
                .content(R.string.dialog_not_initalized_content)
                .cancelable(false)
                .canceledOnTouchOutside(false)
                .autoDismiss(false)
                .positiveText(android.R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        startSearchActivity();
                        dialog.dismiss();
                    }
                })
                .show();
    }

    /**
     * Start school searchSchool activity
     */
    private void startSearchActivity() {
        this.startActivity(new Intent(this, SchoolSearchActivity.class));
        this.finish();
    }

    /**
     * Initialize navigation view header
     */
    private void initNavigationViewHeader() {
        View headerView = mNavigationView.getHeaderView(0);
        TextView headerSchoolName = (TextView) headerView.findViewById(R.id.header_schoolname);
        TextView headerPath = (TextView) headerView.findViewById(R.id.header_path);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String schoolName = preferences.getString(Prefs.SCHOOL_INFO_SCHOOL_NAME, "School Name");
        String path = preferences.getString(Prefs.SCHOOL_INFO_PATH, null);

        String[] paths = getResources().getStringArray(R.array.path_arrays);
        String[] pathNames = getResources().getStringArray(R.array.path_name_arrays);

        String pathName = "Path";
        for (int index = 0; index < paths.length; index++) {
            if (path == null)
                break;

            if (path.equals(paths[index])) {
                pathName = pathNames[index];
            }
        }
        headerSchoolName.setText(schoolName);
        headerPath.setText(pathName);
    }

    public static class DataUpdateEvent {
        public int dateIndex = -1;

        public DataUpdateEvent(int dateIndex) {
            this.dateIndex = dateIndex;
        }
    }

    public class MealFetchReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(MealFetchService.ACTION_FETCH_SIGNAL)) {
                int flag = intent.getIntExtra(MealFetchService.EXTRA_FLAG, 0);
                switch (flag) {
                    case MealFetchService.FLAG_FINISH:
                        Log.d("MealFetchReceiver", "Receive signal 'fetch finished'");
                        sendEvent();
                        updateTitle();
                        break;
                    default:
                        throw new UnsupportedOperationException();
                }
            }
        }
    }
}
