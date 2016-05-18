package com.earlier.yma.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.earlier.yma.R;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by namhyun on 2015-12-02.
 */
public class Util {
    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    public static String getDateString(Context context, int dayIndex) {
        final String[] monthStringArray = context.getResources().getStringArray(R.array.month_name);
        final String[] dayStringArray = context.getResources().getStringArray(R.array.day_name);

        int monthNumber = Util.getCalendarByDayIndex(dayIndex).get(Calendar.MONTH);
        int dayNumber = Util.getCalendarByDayIndex(dayIndex).get(Calendar.DAY_OF_MONTH);

        String result;
        if (Locale.getDefault().getLanguage().equals(Locale.KOREAN.getLanguage())) {
            result = context.getString(R.string.activity_main_title,
                    String.valueOf(monthNumber + 1),
                    dayNumber,
                    dayStringArray[dayIndex]);
        } else {
            result = context.getString(R.string.activity_main_title,
                    monthStringArray[monthNumber],
                    dayNumber,
                    dayStringArray[dayIndex]);
        }
        return result;
    }

    public static Calendar getCalendarByDayIndex(int dayIndex) {
        Calendar todayCalendar = getTodayCalender();
        int todayIndex = getDayIndexFromCalendar(todayCalendar);
        int dimIndex = Math.abs(todayIndex - dayIndex);

        Calendar calculatedCalendar = getTodayCalender();
        if (todayIndex < dayIndex) {
            calculatedCalendar.add(Calendar.DATE, dimIndex);
        } else {
            calculatedCalendar.add(Calendar.DATE, -dimIndex);
        }
        return calculatedCalendar;
    }

    public static Calendar getTodayCalender() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    public static int getDayIndexFromCalendar(Calendar calendar) {
        return calendar.get(Calendar.DAY_OF_WEEK) - 1;
    }
}
