package com.tizzone.go4lunch.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ReminderBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        // NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifyGo4Lunch")
//                .setContentTitle(getCurrentUserId().getDisplayName() + " just choose a lunch spot")
//                .setContentText("He's lunching at " + restaurant.getName())
//                .setContentIntent(notificationPendingIntent)
//                .setAutoCancel(true)
//                .setPriority(NotificationCompat.PRIORITY_HIGH)
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setSmallIcon(R.drawable.ic_logo_go4lunch);
//
//        NotificationCompat.Builder notifyBuilder = getNotificationBuilder();
        //  mNotifyManager.notify(NOTIFICATION_ID, notifyBuilder.build());

        throw new UnsupportedOperationException("Not yet implemented");
    }
}