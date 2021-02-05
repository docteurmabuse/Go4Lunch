package com.tizzone.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.utils.RestaurantHelper;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class RestaurantRepository {
    public MutableLiveData<List<Restaurant>> getFirebaseRestaurants() {
        MutableLiveData<List<Restaurant>> firebaseRestaurants = new MutableLiveData<>();
        RestaurantHelper.getRestaurants().
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException
                            error) {
                        if (error != null) {
                            Log.w(TAG, "Listener failed.", error);
                            List<Restaurant> mRestaurants = new ArrayList<>();
                            firebaseRestaurants.setValue(mRestaurants);
                        } else {
                            List<Restaurant> mRestaurants = new ArrayList<>();
                            firebaseRestaurants.setValue(value.toObjects(Restaurant.class));
                        }
                    }
                });
        return firebaseRestaurants;
    }

}
