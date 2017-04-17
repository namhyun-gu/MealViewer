package com.earlier.yma.utilities;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
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
                .setSmallIcon(R.drawable.ic_error_white_24dp)
                .setContentTitle(context.getString(R.string.network_error_notification_title))
                .setContentText(context.getString(R.string.network_error_notification_body))
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

    private static Bitmap warningLargeIcon(Context context) {
        Resources resources = context.getResources();
        return BitmapFactory.decodeResource(resources, R.drawable.ic_error_white_24dp);
    }

    private static NotificationCompat.Action retryDownload(Context context) {
        Intent dataServiceIntent = new Intent(context, MealDataService.class);

        PendingIntent dataServicePendingIntent = PendingIntent.getService(context,
                MEAL_DATA_PENDING_INTENT_ID,
                dataServiceIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        return new NotificationCompat.Action.Builder(R.drawable.ic_refresh_black_24dp,
                context.getString(R.string.action_retry),
                dataServicePendingIntent).build();
    }

    public static void clearAllNotification(Context context) {
        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
    }
}
