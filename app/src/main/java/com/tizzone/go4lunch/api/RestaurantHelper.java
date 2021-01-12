package com.tizzone.go4lunch.api;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.models.Restaurant;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String uid, String name) {
        Restaurant restaurantToCreate = new Restaurant(uid, name);
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).delete();
    }

}
