package com.tizzone.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.models.User;

import java.util.List;

public class UserHelper {
    private static final String COLLECTION_NAME = "users";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, boolean isAuthenticated, String userName, String photoUrl, List<String> favouriteRestaurants, String lunchSpot) {
        User userToCreate = new User(uid, isAuthenticated, userName, photoUrl, favouriteRestaurants, lunchSpot);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }


    // --- READ ---
    public static Task<DocumentSnapshot> getUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).get();
    }

    public static Query getUserFavouriteRestaurants(String userId) {
        return getUsersCollection().document(userId).collection("favouriteRestaurants");
    }

    public static Query getWorkmates(String uid) {
        return getUsersCollection().whereNotEqualTo("uid", uid);
    }

    public static Query getUsersLunchSpot(String lunchSpot) {
        return getUsersCollection().whereEqualTo("lunchSpot", lunchSpot);
    }


    public static Query getUsersLunchSpotWithoutCurrentUser(String lunchSpot, String uid) {
        return getUsersCollection().whereNotEqualTo("uid", uid).whereEqualTo("lunchSpot", lunchSpot);
    }

    // --- UPDATE ---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("userName", username);
    }

    public static Task<Void> updatePhotoUrl(String photoUrl, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("photoUrl", photoUrl);
    }

    public static Task<Void> updateFavoriteRestaurants(List<String> favoriteRestaurants, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("favoriteRestaurants", favoriteRestaurants);
    }

    public static Task<Void> updateIsAuthenticated(boolean isAuthenticated, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("isAuthenticated", isAuthenticated);
    }

    public static Task<Void> updateLunchSpot(String lunchSpot, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("lunchSpot", lunchSpot);
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }


}


