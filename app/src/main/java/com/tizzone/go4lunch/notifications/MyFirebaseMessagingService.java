package com.tizzone.go4lunch.notifications;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.common.base.Joiner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tizzone.go4lunch.MainActivity;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.RestaurantRepository;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.worker.MyWorker;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

import static com.tizzone.go4lunch.utils.Constants.CHANNEL_ID;
import static com.tizzone.go4lunch.utils.Constants.NOTIFICATION_ID;

@AndroidEntryPoint
public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String lunchSpotId = "lunchSpotId";
    public static final String myPreference = "mypref";
    private static final String TAG = "MyFirebaseMsgService";
    @Inject
    public UserRepository userRepository;
    @Inject
    public RestaurantRepository restaurantRepository;
    private String lunchingText;
    private String joiningMates;
    private PendingIntent pendingIntent;


    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);
    }

    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        Intent intentToRepeat = new Intent(this, MainActivity.class);

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());
            if (/* Check if data needs to be processed by long running job */ true) {
                // For long-running tasks (10 seconds or more) use WorkManager.
                scheduleJob();
            } else {
                // Handle message within 10 seconds
                handleNow();
            }
        }

        // Check if message contains a notification payload.
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
        pendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, intentToRepeat, PendingIntent.FLAG_UPDATE_CURRENT);

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        getWorkmatesLunchingText(this);
    }

    /**
     * Schedule async work using WorkManager.
     */
    private void scheduleJob() {
        // [START dispatch_job]
        OneTimeWorkRequest work = new OneTimeWorkRequest.Builder(MyWorker.class)
                .build();
        WorkManager.getInstance(getApplicationContext()).beginWith(work).enqueue();
        // [END dispatch_job]
    }

    /**
     * Handle time allotted to BroadcastReceivers.
     */
    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
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
            Notification dailyNotification = buildLocalNotification(context, pendingIntent).build();
            //Send local Notification
            NotificationHelper.getNotificationManager(context).notify(NOTIFICATION_ID, dailyNotification);
        });
    }
}
