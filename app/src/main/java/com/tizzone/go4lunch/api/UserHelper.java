package com.tizzone.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.models.user.User;

import java.lang.reflect.Array;

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, boolean isAuthenticated, String username, String photoUrl, Array favoriteRestaurants, int lunchSpot) {
        User userToCreate = new User(uid, isAuthenticated, username, photoUrl, favoriteRestaurants, lunchSpot);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }


    // --- READ ---
    public static Task<DocumentSnapshot> getUser(String uid) {
    return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    public static Task<Void> updatePhotoUrl(String photoUrl, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("photoUrl", photoUrl);
    }

    public static Task<Void> updateLunchSpot(int lunchSpot, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("lunchSpot", lunchSpot);
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }


}

