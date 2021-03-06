package com.tizzone.go4lunch.utils;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import static com.tizzone.go4lunch.utils.Constants.COLLECTION_USER_NAME;

public class UserHelper {
    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_USER_NAME);
    }

    public static Query getUsersLunchSpotWithoutCurrentUser(String lunchSpot, String uid) {
        return getUsersCollection().whereNotEqualTo("uid", uid).whereEqualTo("lunchSpot", lunchSpot);
    }
}


