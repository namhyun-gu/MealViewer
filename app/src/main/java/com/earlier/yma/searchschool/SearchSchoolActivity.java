package com.earlier.yma.searchschool;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.earlier.yma.R;
import com.earlier.yma.utilities.ActivityUtils;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import javax.inject.Inject;

public class SearchSchoolActivity extends AppCompatActivity implements
        MaterialSearchView.OnQueryTextListener {

    @Inject SearchSchoolPresenter mPresenter;

    private Toolbar mToolbar;

    private MaterialSearchView mSearchView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_search);

        setupToolbar();
        setupSearchView();

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

        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView.setMenuItem(searchItem);
        return true;
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    private void setupSearchView() {
        mSearchView = (MaterialSearchView) findViewById(R.id.search_view);
        mSearchView.setOnQueryTextListener(this);
        mSearchView.showSearch(false);
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        mPresenter.searchSchool(query);
        mSearchView.closeSearch();
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        return false;
    }

    @Override
    public void onBackPressed() {
        if (mSearchView.isSearchOpen()) {
            mSearchView.closeSearch();
        } else {
            super.onBackPressed();
        }
    }
}