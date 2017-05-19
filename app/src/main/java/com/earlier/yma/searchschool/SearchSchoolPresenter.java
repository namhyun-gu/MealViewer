package com.earlier.yma.searchschool;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.data.Meal;
import com.earlier.yma.data.MealPreferences;
import com.earlier.yma.data.SearchResult;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.utilities.RealmString;
import com.earlier.yma.utilities.SearchResultDeserializer;
import com.earlier.yma.utilities.Utils;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchSchoolPresenter implements SearchSchoolContract.Presenter {

    private static final String TAG = SearchSchoolPresenter.class.getSimpleName();

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
            mView.showEmptyError();
        }

        final String[] pathArray =
                mContext.getResources().getStringArray(R.array.path_arrays);

        Observable.fromArray(pathArray)
                .map(path -> String.format(NeisService.SEARCH_BASE_URL, path))
                .subscribeOn(Schedulers.io())
                .flatMap(path -> {
                    NeisService service = buildRetrofit(path).create(NeisService.class);
                    return service.searchSchool(query);
                })
                .onErrorReturn(throwable -> SearchResult.empty())
                .retry(1)
                .buffer(pathArray.length)
                .observeOn(AndroidSchedulers.mainThread())
                .doOnSubscribe(disposable -> mView.setupProgress())
                .doFinally(() -> mView.finishProgress())
                .subscribe(results -> mView.showResults(results),
                        throwable -> {
                            mView.showEmptyError();
                            Log.e(TAG, "searchSchool: Error occurred", throwable);
                        });
    }

    @Override
    public void saveSchool(SearchResult.Detail schoolDetail) {
        String path = Utils.convertNameToPath(mContext, schoolDetail.getPathName());

        MealPreferences.setSchoolInfo(mContext,
                path,
                schoolDetail.getSchoolName(),
                schoolDetail.getSchulCode(),
                schoolDetail.getSchulCrseScCode(),
                schoolDetail.getSchulKndScCode());
    }

    @Override
    public void clearDatabase() {
        mRealm.executeTransaction(realm -> {
            mRealm.where(Meal.class).findAll().deleteAllFromRealm();
            mRealm.where(RealmString.class).findAll().deleteAllFromRealm();
        });
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
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .client(client)
                .build();
    }
}
