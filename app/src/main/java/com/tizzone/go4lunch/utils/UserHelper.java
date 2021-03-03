package com.tizzone.go4lunch.utils;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.models.User;

import java.util.List;

import static com.tizzone.go4lunch.utils.Constants.COLLECTION_USER_NAME;

public class UserHelper {
    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER_NAME);
    }

    // --- CREATE ---

    public static Task<Void> createUser(String uid, String userEmail, String userName, String photoUrl, List<String> favouriteRestaurants, String lunchSpot) {
        User userToCreate = new User(uid, userEmail, userName, photoUrl, favouriteRestaurants, lunchSpot);
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    public static Query getUsersLunchSpotWithoutCurrentUser(String lunchSpot, String uid) {
        return getUsersCollection().whereNotEqualTo("uid", uid).whereEqualTo("lunchSpot", lunchSpot);
    }

}


