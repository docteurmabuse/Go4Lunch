package com.tizzone.go4lunch.notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
        // [START_EXCLUDE]
        // There are two types of messages data messages and notification messages. Data messages
        // are handled
        // here in onMessageReceived whether the app is in the foreground or background. Data
        // messages are the type
        // traditionally used with GCM. Notification messages are only received here in
        // onMessageReceived when the app
        // is in the foreground. When the app is in the background an automatically generated
        // notification is displayed.
        // When the user taps on the notification they are returned to the app. Messages
        // containing both notification
        // and data payloads are treated as notification messages. The Firebase console always
        // sends notification
        // messages. For more see: https://firebase.google.com/docs/cloud-messaging/concept-options
        // [END_EXCLUDE]

        // TODO(developer): Handle FCM messages here.
        // Not getting messages here? See why this may be: https://goo.gl/39bRNJ
        Log.d(TAG, "From: " + remoteMessage.getFrom());

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
            getWorkmatesLunchingText();
        }

        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.
        getWorkmatesLunchingText();
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

    /**
     * Create and show a simple notification containing the received FCM message.
     *
     * @param messageBody FCM message body received.
     */
    private void sendNotification(String messageBody) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logogo4lunch)
                        .setContentTitle(getString(R.string.go4lunch))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(lunchingText))
                        .setContentText(messageBody)
                        .setColor(getResources().getColor(R.color.colorAccent))
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    private void getWorkmatesLunchingText() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        getRestaurantLunchingText(getApplicationContext(), "spotId", joiningMates);

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
                        getRestaurantLunchingText(getApplicationContext(), spotId, joiningMates);
                    }
                });
            }
        }
    }

    private void getRestaurantLunchingText(Context context, String lunchSpotId, String joiningMates) {
        restaurantRepository.getRestaurantsById("ChIJS_utyx0U70cR6925q9GkD0I").addOnSuccessListener(documentSnapshot -> {
            Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
            assert restaurant != null;
            if (joiningMates != null) {
                lunchingText = String.format(context.getResources().getString(R.string.notification_lunching_text), restaurant.getName(), restaurant.getAddress(), joiningMates);
            } else {
                lunchingText = String.format(context.getResources().getString(R.string.notification_lunching_alone_text), restaurant.getName(), restaurant.getAddress());
            }
            Log.d(TAG, "Message Notification lunchingText: " + lunchingText);

            //Send local Notification
            sendNotification(lunchingText);
        });
    }
}
