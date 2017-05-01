package com.earlier.yma.searchschool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.earlier.yma.R;
import com.earlier.yma.utilities.ActivityUtils;

import javax.inject.Inject;

public class SearchSchoolActivity extends AppCompatActivity {

    @Inject SearchSchoolPresenter mPresenter;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_search);

        setupToolbar();

        SearchSchoolFragment searchSchoolFragment =
                (SearchSchoolFragment) getSupportFragmentManager().findFragmentById(R.id.container);

        if (searchSchoolFragment == null) {
            searchSchoolFragment = SearchSchoolFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), searchSchoolFragment, R.id.container);
        }

        DaggerSearchSchoolComponent.builder()
                .searchSchoolPresenterModule(new SearchSchoolPresenterModule(this, searchSchoolFragment))
                .build()
                .inject(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_school_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                setTitle(String.format(getString(R.string.activity_search_result_title), query));
                mPresenter.searchSchool(query);
                MenuItemCompat.collapseActionView(searchItem);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        MenuItemCompat.expandActionView(searchItem);
        return super.onCreateOptionsMenu(menu);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }
}
