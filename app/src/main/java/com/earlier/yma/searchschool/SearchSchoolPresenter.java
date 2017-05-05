package com.earlier.yma.searchschool;

import android.content.Context;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.data.SearchResult;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.utilities.SearchResultDeserializer;
import com.earlier.yma.utilities.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchSchoolPresenter implements SearchSchoolContract.Presenter {

    private Context mContext;

    private SearchSchoolContract.View mView;

    private Realm mRealm;

    @Inject
    public SearchSchoolPresenter(Context context, SearchSchoolContract.View view) {
        mContext = context;
        mView = view;
    }

    @Inject
    void setupListeners() {
        mView.setPresenter(this);
    }

    @Override
    public void start() {
        if (mRealm == null) {
            mRealm = Realm.getDefaultInstance();
        }
    }

    @Override
    public void destroy() {
        mRealm.close();
    }

    @Override
    public void searchSchool(String query) {
        mView.updateTitle(query);

        if (TextUtils.isEmpty(query)) {
            mView.showEmptyError();
            return;
        }

        if (!Utils.isConnected(mContext)) {
            mView.showNetworkError();
        }

        new SearchSchoolTask().execute(query);
    }

    private class SearchSchoolTask extends AsyncTask<String, Integer, List<SearchResult>> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mView.setupProgress();
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            mView.updateProgress(values[0] + 1); // Starts '1'
        }

        @Override
        protected List<SearchResult> doInBackground(String... params) {
            String query = params[0];
            final String[] pathArray =
                    mContext.getResources().getStringArray(R.array.path_arrays);

            List<SearchResult> results = new ArrayList<>();
            for (int index = 0; index < pathArray.length; index++) {
                String path = pathArray[index];
                publishProgress(index);

                if (!Utils.isConnected(mContext)) {
                    continue;
                }

                String baseUrl = String.format(NeisService.BASE_URL, path);

                Retrofit retrofit = buildRetrofit(baseUrl);

                NeisService service = retrofit.create(NeisService.class);

                Call<SearchResult> call = service.searchSchool(query);
                try {
                    Response<SearchResult> response = call.execute();
                    SearchResult searchResult = response.body();
                    searchResult.setPath(path);

                    if (searchResult.getResults().length == 0) continue;

                    results.add(searchResult);
                } catch (IOException e) {
                    Log.e("SearchSchoolTask", "Can't receive result (" + path + ")", e);
                    continue;
                }
            }
            return results;
        }

        @Override
        protected void onPostExecute(List<SearchResult> searchResults) {
            super.onPostExecute(searchResults);
            mView.finishProgress();
            mView.showResults(searchResults);
        }

        private Gson buildGson() {
            return new GsonBuilder()
                    .registerTypeAdapter(SearchResult.class, new SearchResultDeserializer())
                    .create();
        }

        private Retrofit buildRetrofit(String baseUrl) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .addNetworkInterceptor(new StethoInterceptor())
                    .build();

            Gson gson = buildGson();

            return new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(client)
                    .build();
        }

    }
}
