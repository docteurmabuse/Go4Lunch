package com.tizzone.go4lunch.services;

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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.common.base.Joiner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.tizzone.go4lunch.MainActivity;
import com.tizzone.go4lunch.R;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.utils.RestaurantHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    public static final String lunchSpotId = "lunchSpotId";
    public static final String myPreference = "mypref";
    private static final String TAG = "MyFirebaseMsgService";
    private SharedPreferences sharedPreferences;
    private LocalBroadcastManager broadcastManager;
    private String lunchingText;
    private String joiningMates;
    public UserRepository userRepository;

    @Inject
    public MyFirebaseMessagingService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Called by the system when the service is first created.  Do not call this method directly.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        broadcastManager = LocalBroadcastManager.getInstance(this);
    }

    @Override
    public void onNewToken(@NonNull String token) {
        super.onNewToken(token);
        Log.d(TAG, "Refreshed token: " + token);

        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // FCM registration token to your app server.
        sendRegistrationToServer(token);
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        getUserRestaurant(remoteMessage);
        //sendNotification(remoteMessage);
    }

    private void getUserRestaurant(RemoteMessage remoteMessage) {
        sharedPreferences = getSharedPreferences(myPreference,
                Context.MODE_PRIVATE);
        if (sharedPreferences.contains(lunchSpotId)) {
            String spotId = (sharedPreferences.getString(lunchSpotId, ""));
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            userRepository.getWorkmatesLunchInThatSpot(spotId, uid).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    List<User> users = task.getResult().toObjects(User.class);
                    List<String> usersList = new ArrayList();
                    for (User user : users) {
                        if (user.getUserName() != null) usersList.add(user.getUserName());
                    }
                    joiningMates = Joiner.on(", ").join(usersList);
                }
            });

            RestaurantHelper.getRestaurantsById(spotId).addOnSuccessListener(documentSnapshot -> {
                Restaurant restaurant = documentSnapshot.toObject(Restaurant.class);
                lunchingText = String.format(getApplicationContext().getResources().getString(R.string.notification_lunching_text), restaurant.getName(), restaurant.getAddress(), joiningMates);
                sendNotification(remoteMessage, lunchingText);
            });
        }
    }

    private void sendNotification(RemoteMessage remoteMessage, String lunchingText) {
        Intent intent = new Intent(this, MainActivity.class);
        int m = 115;
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                m, intent, PendingIntent.FLAG_ONE_SHOT);
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder;
        notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.ic_logo_go4lunch)
                        .setContentTitle(remoteMessage.getNotification().getTitle())
                        .setContentText(lunchingText)
                        .setAutoCancel(true)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(lunchingText))
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
        notificationManager.notify(m, notificationBuilder.build());
    }

    @Override
    public void onMessageSent(@NonNull String s) {
        super.onMessageSent(s);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}