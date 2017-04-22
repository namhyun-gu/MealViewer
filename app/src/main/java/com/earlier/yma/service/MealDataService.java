package com.earlier.yma.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.earlier.yma.data.Meal;
import com.earlier.yma.data.MealDataUtil;
import com.earlier.yma.data.MealPreferences;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.utilities.NotificationUtils;
import com.earlier.yma.utilities.ToStringConverterFactory;
import com.earlier.yma.utilities.Util;
import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MealDataService extends IntentService {

    private static final String LOG_TAG = "MealDataService";

    public MealDataService() {
        super("MealDataService");
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        NotificationUtils.clearAllNotification(this);

        if (!Util.isConnected(this)) {
            NotificationUtils.networkErrorOccurred(this);
            return;
        }

        MealPreferences.SchoolInfo schoolInfo = MealPreferences.getSchoolInfo(this);

        String baseUrl = String.format(NeisService.BASE_URL, schoolInfo.getPath());

        Retrofit retrofit = buildRetrofit(baseUrl);

        NeisService service = retrofit.create(NeisService.class);

        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        realm.deleteAll();
        for (int mealKindCode = 1; mealKindCode <= 3; mealKindCode++) {
            NotificationUtils.downloadProgress(this, mealKindCode);

            Call<String> call = service.weeklyMeal(
                    schoolInfo.getSchulCode(),
                    schoolInfo.getSchulCrseScCode(),
                    schoolInfo.getSchulKindCode(),
                    mealKindCode,
                    Util.getFormatDateString(new Date()));

            String result;
            try {
                Response<String> response = call.execute();
                result = response.body();
            } catch (IOException e) {
                Log.e(LOG_TAG, "Can't receive data", e);
                NotificationUtils.clearAllNotification(this);
                if (!Util.isConnected(this)) {
                    NotificationUtils.networkErrorOccurred(this);
                } else {
                    NotificationUtils.downloadErrorOccurred(this);
                }
                return;
            }

            List<Meal> mealList = MealDataUtil.parseResponse(realm, result, mealKindCode);
            realm.copyFromRealm(mealList);
        }
        realm.commitTransaction();
        realm.close();

        NotificationUtils.clearAllNotification(this);
    }

    private Retrofit buildRetrofit(String baseUrl) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();

        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(new ToStringConverterFactory())
                .client(client)
                .build();
    }
}
