package com.tizzone.go4lunch.api;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.models.Restaurant;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String uid, String name, String address, String photoUrl, float rating, int restaurant_counter, boolean openNow, LatLng location) {
        Restaurant restaurantToCreate = new Restaurant(uid, name, address, photoUrl, rating, restaurant_counter, openNow, location);
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    public static Task<Void> incrementCounter(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).update("restaurant_counter", FieldValue.increment(1));
    }

    // --- READ ---
    public static Task<DocumentSnapshot> getRestaurants(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).get();
    }

    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).delete();
    }

}
