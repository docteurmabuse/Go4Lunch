package com.tizzone.go4lunch.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.di.CollectionUsers;

import java.util.List;

import javax.inject.Inject;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;

public class UserRepository {

    private final CollectionReference usersRef;

    @Inject
    public UserRepository(@CollectionUsers CollectionReference usersRef) {
        this.usersRef = usersRef;
    }

    // --- READ ---
    public Task<DocumentSnapshot> getUser(String uid) {
        return usersRef.document(uid).get();
    }

    public Query getQueryUsersByName() {
        return usersRef.orderBy("userName", ASCENDING);
    }

    public Query getQueryUsersByLunchSpotId(String lunchSpoId) {
        return usersRef.whereEqualTo("lunchSpot", lunchSpoId);
    }

    public Task<Void> updateLunchSpot(String lunchSpot, String userId) {
        return usersRef.document(userId).update("lunchSpot", lunchSpot);
    }

    public Task<Void> updateFavoriteRestaurants(List<String> favoriteRestaurants, String userId) {
        return usersRef.document(userId).update("favoriteRestaurants", favoriteRestaurants);
    }

}
