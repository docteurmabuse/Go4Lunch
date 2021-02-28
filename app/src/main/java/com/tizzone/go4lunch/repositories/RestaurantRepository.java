package com.tizzone.go4lunch.repositories;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.tizzone.go4lunch.di.CollectionRestaurants;
import com.tizzone.go4lunch.models.Restaurant;

import javax.inject.Inject;

public class RestaurantRepository {

    private final CollectionReference restaurantsRef;

    @Inject
    public RestaurantRepository(@CollectionRestaurants CollectionReference restaurantsRef) {
        this.restaurantsRef = restaurantsRef;
    }

    // --- READ ---
    public Task<DocumentSnapshot> getRestaurantsById(String uid) {
        return restaurantsRef.document(uid).get();
    }

    // --- CREATE ---
    public Task<Void> createRestaurant(Restaurant restaurant) {
        return restaurantsRef.document(restaurant.getUid()).set(restaurant);
    }

}
