package com.tizzone.go4lunch.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.common.base.Joiner;
import com.google.firebase.auth.FirebaseAuth;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.RestaurantRepository;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.ui.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.ALARM_TYPE_RTC;
import static com.tizzone.go4lunch.utils.Constants.CHANNEL_ID;

@AndroidEntryPoint
public class AlarmReceiver extends BroadcastReceiver {
    public static final String lunchSpotId = "lunchSpotId";
    public static final String myPreference = "mypref";
    private static final String TAG = "MyFirebaseMsgService";
    @Inject
    public UserRepository userRepository;
    @Inject
    public RestaurantRepository restaurantRepository;
    private String lunchingText;
    private String joiningMates;
    private Notification dailyNotification;
    private PendingIntent pendingIntent;

    public AlarmReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        //Launch app on click
        Intent intentToRepeat = new Intent(context, MainActivity.class);
        Log.e(TAG, "notification alarm received! ");
        //Get workmates who lunch with the user and restaurants
        getWorkmatesLunchingText(context);
        //Set flag to relaunch the app
        intentToRepeat.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        //Pending intent to launch activity above
        pendingIntent = PendingIntent.getActivity(context, ALARM_TYPE_RTC, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private NotificationCompat.Builder buildLocalNotification(Context context, PendingIntent pendingIntent) {
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Log.e(TAG, "notification alarm build! ");

        return new NotificationCompat.Builder(context, CHANNEL_ID)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_logogo4lunch_white)
                .setContentTitle(context.getString(R.string.notification_title))
                .setContentText(lunchingText)
                .setAutoCancel(true)
                .setColor(context.getResources().getColor(R.color.colorAccent))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(lunchingText))
                .setSound(defaultSoundUri)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
    }

    private void getWorkmatesLunchingText(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        if (sharedPreferences.contains(lunchSpotId)) {
            String spotId = (sharedPreferences.getString(lunchSpotId, ""));
            String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
            if (spotId != null) {
                userRepository.getWorkmatesLunchInThatSpot(spotId, uid).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<User> users = task.getResult().toObjects(User.class);
                        List<String> usersList = new ArrayList<>();
                        for (User user : users) {
                            if (user.getUserName() != null) usersList.add(user.getUserName());
                        }
                        joiningMates = Joiner.on(", ").join(usersList);
                        getRestaurantLunchingText(context, spotId, joiningMates);
                    }
                });
            }
        }
    }

    private void getRestaurantLunchingText(Context context, String lunchSpotId, String joiningMates) {
        restaurantRepository.getRestaurantsById(lunchSpotId).addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            assert restaurant != null;
            if (joiningMates != null) {
                lunchingText = String.format(context.getResources().getString(R.string.notification_lunching_text), restaurant.getName(), restaurant.getAddress(), joiningMates);
            } else {
                lunchingText = String.format(context.getResources().getString(R.string.notification_lunching_alone_text), restaurant.getName(), restaurant.getAddress());
            }
            //Build notification
            dailyNotification = buildLocalNotification(context, pendingIntent).build();
            //Send local Notification
            NotificationHelper.getNotificationManager(context).notify(ALARM_TYPE_RTC, dailyNotification);
        });
    }
}