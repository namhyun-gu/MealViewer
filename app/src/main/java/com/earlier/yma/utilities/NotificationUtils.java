package com.earlier.yma.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.earlier.yma.R;
import com.earlier.yma.service.MealDataService;

public class NotificationUtils {

    private static final int MEAL_DATA_NOTIFICATION_ID = 1001;

    private static final int MEAL_DATA_PENDING_INTENT_ID = 2001;

    public static void networkErrorOccurred(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle(context.getString(R.string.network_error_notification_title))
                .setContentText(context.getString(R.string.network_error_notification_body))
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .addAction(retryDownload(context))
                .addAction(goSettings(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MEAL_DATA_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void downloadErrorOccurred(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_error)
                .setContentTitle(context.getString(R.string.network_error_notification_title))
                .setContentText(context.getString(R.string.download_error_notification_body))
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                .addAction(retryDownload(context))
                .setAutoCancel(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MEAL_DATA_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void downloadProgress(Context context, int progress) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(context.getString(R.string.download_progress_notification_title))
                .setContentText(context.getString(R.string.download_progress_notification_text))
                .setProgress(3, progress, false)
                .setOngoing(true)
                .setAutoCancel(false);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MEAL_DATA_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void downloadComplete(Context context) {
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setSmallIcon(R.drawable.ic_download)
                .setContentTitle(context.getString(R.string.download_progress_notification_title))
                .setContentText(context.getString(R.string.download_complete_notification_text))
                .setAutoCancel(true);

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(MEAL_DATA_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void clearAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }

    private static NotificationCompat.Action retryDownload(Context context) {
        Intent dataServiceIntent = new Intent(context, MealDataService.class);

        PendingIntent dataServicePendingIntent = PendingIntent.getService(context,
                MEAL_DATA_PENDING_INTENT_ID,
                dataServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(R.drawable.ic_retry,
                context.getString(R.string.action_retry),
                dataServicePendingIntent).build();
    }

    private static NotificationCompat.Action goSettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_SETTINGS);

        PendingIntent settingsPendingIntent = PendingIntent.getActivity(context,
                0,
                settingsIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(R.drawable.ic_settings,
                context.getString(R.string.action_go_settings),
                settingsPendingIntent).build();
    }
}
