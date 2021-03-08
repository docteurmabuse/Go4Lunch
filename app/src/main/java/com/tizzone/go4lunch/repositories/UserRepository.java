package com.tizzone.go4lunch.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.di.CollectionUsers;
import com.tizzone.go4lunch.models.User;

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

    // Users list
    public Query getQueryUsersByName() {
        return usersRef.orderBy("userName", ASCENDING);
    }

    //Workmates list
    public Query getWorkmatesLunchInThatSpot(String lunchSpotId, String uid) {
        return usersRef.whereNotEqualTo("uid", uid).whereEqualTo("lunchSpotId", lunchSpotId);
    }

    public Query getQueryUsersByLunchSpotId(String lunchSpotId) {
        return usersRef.whereEqualTo("lunchSpotId", lunchSpotId);
    }

    public void updateLunchSpot(String lunchSpotId, String lunchSpotName, String userId) {
        usersRef.document(userId).update(
                "lunchSpotId", lunchSpotId,
                "lunchSpotName", lunchSpotName);
    }

    public void updateFavoriteRestaurants(List<String> favoriteRestaurants, String userId) {
        usersRef.document(userId).update("favoriteRestaurants", favoriteRestaurants);
    }

    //Create user
    public Task<Void> createUser(String uid, String userEmail, String userName, String photoUrl, List<String> favouriteRestaurants, String lunchSpotId, String lunchSpotName) {
        User userToCreate = new User(uid, userEmail, userName, photoUrl, favouriteRestaurants, lunchSpotId, lunchSpotName);
        return usersRef.document(uid).set(userToCreate);
    }


}
