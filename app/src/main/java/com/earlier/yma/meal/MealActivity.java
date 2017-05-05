package com.earlier.yma.meal;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.earlier.yma.R;
import com.earlier.yma.data.MealPreferences;
import com.earlier.yma.service.MealDataService;
import com.earlier.yma.ui.PrefActivity;
import com.earlier.yma.utilities.ActivityUtils;
import com.earlier.yma.utilities.Utils;

import java.util.Date;

import javax.inject.Inject;

public class MealActivity extends AppCompatActivity
        implements SharedPreferences.OnSharedPreferenceChangeListener,
        TabLayout.OnTabSelectedListener {

    private static final String CURRENT_FILTERING_KEY = "CURRENT_FILTERING_KEY";

    private static final String CURRENT_DATE_KEY = "CURRENT_DATE_KEY";

    @Inject MealPresenter mPresenter;

    private ActionBarDrawerToggle mDrawerToggle;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal);

        setupToolbar();
        setupDrawerContent();
        setupTabLayout();
        setupFab();

        MealFragment mealFragment =
                (MealFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (mealFragment == null) {
            mealFragment = MealFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), mealFragment, R.id.container);
        }

        PreferenceManager.getDefaultSharedPreferences(this)
                .registerOnSharedPreferenceChangeListener(this);

        DaggerMealComponent.builder()
                .mealPresenterModule(new MealPresenterModule(mealFragment))
                .build()
                .inject(this);

        if (savedInstanceState != null) {
            MealFilterType currentFiltering =
                    (MealFilterType) savedInstanceState.getSerializable(CURRENT_FILTERING_KEY);
            Date currentDate =
                    (Date) savedInstanceState.getSerializable(CURRENT_DATE_KEY);

            mPresenter.setFiltering(currentFiltering);
            mPresenter.setDate(currentDate);
        }
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putSerializable(CURRENT_FILTERING_KEY, mPresenter.getFiltering());
        outState.putSerializable(CURRENT_DATE_KEY, mPresenter.getDate());
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void setupDrawerContent() {
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerToggle = new ActionBarDrawerToggle(this,
                drawerLayout,
                mToolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close);

        drawerLayout.addDrawerListener(mDrawerToggle);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(item -> {
                    Intent intent = new Intent(MealActivity.this, PrefActivity.class);
                    switch (item.getItemId()) {
                        case R.id.nav_settings:
                            intent.putExtra(PrefActivity.BUNDLE_TYPE,
                                    PrefActivity.TYPE_SETTINGS);
                            startActivity(intent);
                            break;
                        case R.id.nav_info:
                            intent.putExtra(PrefActivity.BUNDLE_TYPE,
                                    PrefActivity.TYPE_INFORMATION);
                            startActivity(intent);
                            break;
                    }
                    return true;
                });

        View headerView = navigationView.getHeaderView(0);

        TextView headerNameView = (TextView) headerView.findViewById(R.id.header_schoolname);
        TextView headerPathView = (TextView) headerView.findViewById(R.id.header_path);

        MealPreferences.SchoolInfo schoolInfo = MealPreferences.getSchoolInfo(this);

        headerNameView.setText(schoolInfo.getSchoolName());
        headerPathView.setText(Utils.convertPathToName(this, schoolInfo.getPath()));
    }

    private void setupTabLayout() {
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.addOnTabSelectedListener(this);
    }

    private void setupFab() {
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(v -> {
            Intent intent = new Intent(MealActivity.this, MealDataService.class);
            startService(intent);
        });
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key) {

    }

    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        int position = tab.getPosition();
        mPresenter.setFiltering(MealFilterType.values()[position]);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // no-op
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }
}
