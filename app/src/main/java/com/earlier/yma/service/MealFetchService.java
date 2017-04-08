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

package com.earlier.yma.service;

import android.app.IntentService;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.earlier.yma.R;
import com.earlier.yma.data.MealDataManager;
import com.earlier.yma.data.MealDataUtil;
import com.earlier.yma.data.model.RequestObject;
import com.earlier.yma.data.service.NeisService;
import com.earlier.yma.util.Prefs;
import com.earlier.yma.util.ToStringConverterFactory;
import com.earlier.yma.util.Util;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MealFetchService extends IntentService {
    public static final String ACTION_FETCH_SIGNAL = "com.earlier.yma.broadcast.fetch_data";
    public static final String EXTRA_FLAG = "com.earlier.yma.service.extra.flag";
    public static final int FLAG_STARTED = 0x0001;
    public static final int FLAG_PROGESS = 0x0002;
    public static final int FLAG_FINISH = 0x0003;

    private static final String ACTION_FETCH_DATA = "com.earlier.yma.service.action.fetch_data";
    private static final String EXTRA_REQUEST = "com.earlier.yma.service.extra.request";

    private MealDataManager dataManager;

    public MealFetchService() {
        super("MealFetchService");
    }

    public static void startAction(Context context, RequestObject requestObject) {
        Intent intent = new Intent(context, MealFetchService.class);
        intent.setAction(ACTION_FETCH_DATA);
        intent.putExtra(EXTRA_REQUEST, requestObject);
        context.startService(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v("MealFetchService", "Service starting");
        dataManager = MealDataManager.getInstance();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            if (intent != null) {
                final String action = intent.getAction();
                if (ACTION_FETCH_DATA.equals(action)) {
                    final RequestObject object = intent.getParcelableExtra(EXTRA_REQUEST);
                    handleAction(object);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleAction(RequestObject object) {
        try {
            SharedPreferences preferences =
                    PreferenceManager.getDefaultSharedPreferences(this);

            // Check latest update week (if -1 is not updated)
            int updateWeek = preferences.getInt(Prefs.UPDATE_WEEK, -1);

            // Get today week
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new Date());
            int todayWeek = calendar.get(Calendar.WEEK_OF_YEAR);

            if (updateWeek == todayWeek) {
                Log.d("MealFetchService", "Data is already updated");
                sendBroadcastFlag(FLAG_FINISH);
                return;
            }

            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            final int notifyId = 0x0001;
            for (int index = 1; index <= 3; index++) {
                // Check connected
                if (!Util.isConnected(this)) {
                    sendWarningNotification(mNotificationManager, notifyId);
                    throw new IOException("reason : not connected");
                }
                sendNotification(mNotificationManager, notifyId, index);

                final String url = String.format(NeisService.BASE_URL, object.path);
                Retrofit.Builder builder = new Retrofit.Builder()
                        .baseUrl(url)
                        .addConverterFactory(new ToStringConverterFactory());
                Retrofit retrofit = builder.client(new OkHttpClient()).build();
                NeisService service = retrofit.create(NeisService.class);
                Call<String> call = service.getResponse(
                        object.schulCode,
                        object.schulCrseScCode,
                        object.schulKndScCode,
                        String.valueOf(index));
                Response<String> response = call.execute();

                Uri responseUri = Uri.parse(url).buildUpon()
                        .appendPath("sts_sci_md01_001.do")
                        .appendQueryParameter("schulCode", object.schulCode)
                        .appendQueryParameter("schulCrseScCode", object.schulCrseScCode)
                        .appendQueryParameter("schulKndScCode", object.schulKndScCode)
                        .appendQueryParameter("schMmealScCode", String.valueOf(index))
                        .build();

                Log.d("ResponseUri", responseUri.toString());

                Headers headers = response.headers();
                Log.d("ResponseHeaders", headers.toString());

                String responseBody = response.body();
                dataManager.save(MealDataUtil.parseResponse(responseBody), index);
            }
            // Remove notification
            mNotificationManager.cancel(notifyId);

            // Save today week
            SharedPreferences.Editor editor = preferences.edit();
            editor.putInt(Prefs.UPDATE_WEEK, todayWeek);
            editor.apply();

            Log.d("MealFetchService", "Successful fetch data");
            sendBroadcastFlag(FLAG_FINISH);
        } catch (IOException e) {
            Log.e("MealFetchService", "Can't receive data");
            e.printStackTrace();
        }
    }

    private void sendBroadcastFlag(int flag) {
        Intent intent = new Intent();
        intent.setAction(ACTION_FETCH_SIGNAL);
        intent.putExtra(EXTRA_FLAG, flag);
        sendBroadcast(intent);
    }

    private void sendNotification(NotificationManager manager, int id, int index) {
        String notificationContent =
                String.format(getString(R.string.notification_meal_fetch_content), index, 3);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_download)
                        .setContentTitle(getString(R.string.notification_meal_fetch_title))
                        .setContentText(notificationContent)
                        .setWhen(System.currentTimeMillis())
                        .setOngoing(true);

        manager.notify(id, mBuilder.build());
    }

    private void sendWarningNotification(NotificationManager manager, int id) {
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_warning)
                        .setContentTitle(getString(R.string.notification_meal_fetch_warning_title))
                        .setContentText(getString(R.string.notification_meal_fetch_warning_content))
                        .setWhen(System.currentTimeMillis());

        manager.notify(id, mBuilder.build());
    }
}
