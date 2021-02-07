package com.tizzone.go4lunch.utils;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.models.Restaurant;

public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---

    public static CollectionReference getRestaurantsCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- READ ---
    public static Query getRestaurants() {
        return RestaurantHelper.getRestaurantsCollection();
    }

    // --- READ ---
    public static Task<DocumentSnapshot> getRestaurantsById(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).get();
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String uid, String name, String address, String photoUrl, Float rating, int restaurant_counter,
                                              Boolean openNow, LatLng location, String websiteUrl, String phone) {
        Restaurant restaurantToCreate = new Restaurant(uid, name, address, photoUrl, rating, restaurant_counter, null, location.latitude, location.longitude, websiteUrl, phone);
        return RestaurantHelper.getRestaurantsCollection().document(uid).set(restaurantToCreate);
    }

    public static Task<Void> incrementCounter(String uid, int i) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).update("restaurant_counter", FieldValue.increment(i));
    }


    // --- DELETE ---
    public static Task<Void> deleteRestaurant(String uid) {
        return RestaurantHelper.getRestaurantsCollection().document(uid).delete();
    }

}
