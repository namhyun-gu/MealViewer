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
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.earlier.yma.R;
import com.earlier.yma.data.model.SearchResultObject;
import com.earlier.yma.data.model.item.DividerItem;
import com.earlier.yma.data.model.item.Item;
import com.earlier.yma.data.model.item.school.DefaultItem;
import com.earlier.yma.data.model.item.school.SubHeaderItem;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.util.Prefs;
import com.earlier.yma.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SchoolSearchActivity extends AppCompatActivity
        implements SchoolResultAdapter.OnItemSelectedListener, SearchView.OnQueryTextListener {
    @BindView(R.id.toolbar) Toolbar mToolbar;
    @BindView(R.id.shadow_view) View mShadowView;
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;
    @BindView(R.id.stub_no_results) ViewStub mViewStubNoResults;
    @BindView(R.id.layout) View mLayoutView;
    private SchoolResultAdapter adapter;
    private NetworkChangeReceiver receiver = new NetworkChangeReceiver();
    private Context mContext;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_school_search);

        // Initial Context (Using realm)
        mContext = this;

        // Bind Butterknife
        ButterKnife.bind(this);

        // Initial Toolbar
        setSupportActionBar(mToolbar);

        // Toolbar Shadow (Pre lollipop)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            mShadowView.setVisibility(View.VISIBLE);
        }

        // Initial RecyclerView
        adapter = new SchoolResultAdapter(this);
        adapter.setOnItemSelectedListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_school_search, menu);
        MenuItem searchMenuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchMenuItem.getActionView();
        searchView.setQueryHint(getString(R.string.search_hint));
        searchView.setOnQueryTextListener(this);
        MenuItemCompat.expandActionView(searchMenuItem);
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onItemSelected(Item item) {
        final DefaultItem defaultItem = (DefaultItem) item;
        final SearchResultObject.SchoolInfo info = defaultItem.getInfo();

        new MaterialDialog.Builder(this)
                .title(info.getSchoolName())
                .content(R.string.dialog_school_set)
                .positiveText(android.R.string.ok)
                .negativeText(android.R.string.cancel)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        String[] paths = getResources().getStringArray(R.array.path_arrays);
                        String path = defaultItem.getPath();

                        String schoolName = info.getSchoolName();
                        String schulCode = info.getSchulCode();
                        String schulCrseScCode = info.getSchulCrseScCode();
                        String schulKndScCode = info.getSchulKndScCode();

                        SharedPreferences preferences =
                                PreferenceManager.getDefaultSharedPreferences(mContext);

                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putBoolean(Prefs.IS_INITIALIZED, true);
                        editor.putBoolean(Prefs.IS_UPDATE_PREV_VERSION, true);
                        editor.putString(Prefs.SCHOOL_INFO_PATH, path);
                        editor.putString(Prefs.SCHOOL_INFO_SCHOOL_NAME, schoolName);
                        editor.putString(Prefs.SCHOOL_INFO_SCHUL_CODE, schulCode);
                        editor.putString(Prefs.SCHOOL_INFO_SCHUL_CRSE_SC_CODE, schulCrseScCode);
                        editor.putString(Prefs.SCHOOL_INFO_SCHUL_KND_SC_CODE, schulKndScCode);
                        editor.apply();

                        showMainActivity();
                        dialog.dismiss();
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        MenuItemCompat.collapseActionView(mToolbar.getMenu().findItem(R.id.action_search));
        mToolbar.setTitle(String.format(getString(R.string.activity_search_result_title), query));
        new SchoolSearchTask(this).execute(query);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }

    private void showMainActivity() {
        this.startActivity(new Intent(this, MainActivity.class));
        this.finish();
    }

    private void showDisconnectedMessage() {
        Snackbar.make(mLayoutView, R.string.not_connected, Snackbar.LENGTH_LONG).show();
    }

    public class SchoolSearchTask extends AsyncTask<String, Integer, List<SearchResultObject>> {
        private Context mContext;
        private String[] paths;
        private String[] pathNames;
        private MaterialDialog dialog;

        public SchoolSearchTask(Context context) {
            this.mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            paths = getResources().getStringArray(R.array.path_arrays);
            pathNames = getResources().getStringArray(R.array.path_name_arrays);
            dialog = new MaterialDialog.Builder(mContext)
                    .content(String.format(getString(R.string.dialog_search), 0, paths.length))
                    .cancelable(false)
                    .canceledOnTouchOutside(false)
                    .autoDismiss(false)
                    .progress(true, 0)
                    .show();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            dialog.setContent(String.format(getString(R.string.dialog_search), values[0], paths.length));
        }

        @Override
        protected List<SearchResultObject> doInBackground(String... params) {
            List<SearchResultObject> resultObjects = new ArrayList<>();
            for (int index = 0; index < paths.length; index++) {
                publishProgress(index + 1);
                String path = paths[index];
                String pathName = pathNames[index];
                try {
                    if (!Util.isConnected(mContext)) {
                        throw new IOException("reason : not connected");
                    }

                    final String url = String.format(NeisService.BASE_URL, path);
                    Retrofit.Builder builder = new Retrofit.Builder()
                            .baseUrl(url)
                            .addConverterFactory(GsonConverterFactory.create());
                    Retrofit retrofit = builder.client(new OkHttpClient()).build();
                    NeisService service = retrofit.create(NeisService.class);

                    Call<SearchResultObject> call = service.searchSchool(params[0]);
                    Response<SearchResultObject> response = call.execute();
                    SearchResultObject resultObject = response.body();

                    if (resultObject != null & !resultObject.getResult().getSchoolList().isEmpty()) {
                        resultObject.setPath(path);
                        resultObject.setPathName(pathName);
                        resultObjects.add(resultObject);
                    }
                } catch (IOException e) {
                    Log.e("SchoolSearchTask", "Can't receive data (paths : " + path + ")");
                    e.printStackTrace();
                }
            }
            if (!resultObjects.isEmpty())
                return resultObjects;
            return null;
        }

        @Override
        protected void onPostExecute(List<SearchResultObject> searchResultObjects) {
            super.onPostExecute(searchResultObjects);
            if (searchResultObjects != null) {
                adapter.clearItem();
                for (int index = 0; index < searchResultObjects.size(); index++) {
                    SearchResultObject object = searchResultObjects.get(index);
                    List<SearchResultObject.SchoolInfo> infoList
                            = object.getResult().getSchoolList();

                    String itemCapacity = String.format(Locale.getDefault(), " (%d)", infoList.size());
                    List<Item> items = new ArrayList<>();
                    items.add(new SubHeaderItem(object.getPathName() + itemCapacity));
                    for (SearchResultObject.SchoolInfo info : infoList) {
                        String schoolName = info.getSchoolName();
                        items.add(new DefaultItem(schoolName, object.getPath(), info));
                    }
                    items.add(new DividerItem());
                    adapter.addItems(items);
                }
                mRecyclerView.setVisibility(View.VISIBLE);
                mViewStubNoResults.setVisibility(View.GONE);
            } else {
                mRecyclerView.setVisibility(View.GONE);
                mViewStubNoResults.setVisibility(View.VISIBLE);
            }

            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    public class NetworkChangeReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
                if (!Util.isConnected(context)) {
                    showDisconnectedMessage();
                }
            }
        }
    }
}