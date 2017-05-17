package com.earlier.yma.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;

import com.earlier.yma.R;
import com.google.common.base.Strings;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Utils {
    public static boolean isConnected(@NonNull Context context) {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    public static String getFormatDateString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd", Locale.getDefault());
        return dateFormat.format(date);
    }

    public static Date editDate(Date date, int hour, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static String convertPathToName(Context context, String path) {
        if (Strings.isNullOrEmpty(path)) return null;

        String[] pathArrays = context.getResources().getStringArray(R.array.path_arrays);
        List<String> pathList = Arrays.asList(pathArrays);

        String[] nameArrays = context.getResources().getStringArray(R.array.path_name_arrays);
        return nameArrays[pathList.indexOf(path)];
    }

    public static String convertNameToPath(Context context, String pathName) {
        if (Strings.isNullOrEmpty(pathName)) return null;

        String[] nameArrays = context.getResources().getStringArray(R.array.path_name_arrays);
        List<String> nameList = Arrays.asList(nameArrays);

        String[] pathArrays = context.getResources().getStringArray(R.array.path_arrays);
        return pathArrays[nameList.indexOf(pathName)];
    }
}
