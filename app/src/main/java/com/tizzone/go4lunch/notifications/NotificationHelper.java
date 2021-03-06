package com.tizzone.go4lunch.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;
import static com.tizzone.go4lunch.utils.Constants.ALARM_TYPE_RTC;
import static com.tizzone.go4lunch.utils.Constants.CHANNEL_ID;

public class NotificationHelper {
    private static final String TAG = "Notification";
    private static AlarmManager alarmManager;
    private static PendingIntent alarmIntent;

    public static void scheduleRepeatingRTCNotification(Context context) {
        // Set the alarm to start at approximately 12:00 p.m.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 12);
        calendar.set(Calendar.MINUTE, 0);
        Intent intent = new Intent(context, AlarmReceiver.class);
        alarmIntent = PendingIntent.getBroadcast(context, ALARM_TYPE_RTC, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager = (AlarmManager) context.getSystemService(ALARM_SERVICE);
        alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, alarmIntent);
        Log.e(TAG, "notification alarm set! ");
    }

    public static void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(alarmIntent);
        }
    }

    public static void enableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();
        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static void disableBootReceiver(Context context) {
        ComponentName receiver = new ComponentName(context, AlarmBootReceiver.class);
        PackageManager pm = context.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
    }

    public static NotificationManager getNotificationManager(Context context) {
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }
        return notificationManager;
    }
}
