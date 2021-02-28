package com.tizzone.go4lunch.di;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;
import static com.tizzone.go4lunch.utils.Constants.COLLECTION_RESTAURANT_NAME;
import static com.tizzone.go4lunch.utils.Constants.USER_NAME_PROPERTY;

@InstallIn(SingletonComponent.class)
@Module
public class RestaurantModule {
    @Singleton
    @Provides
    public static Query provideQueryRestaurants() {
        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_RESTAURANT_NAME)
                .orderBy(USER_NAME_PROPERTY, ASCENDING);
    }


    @Singleton
    @CollectionRestaurants
    @Provides
    public static CollectionReference provideRestaurantsCollectionReference(FirebaseFirestore rootRef) {
        return rootRef.collection(COLLECTION_RESTAURANT_NAME);
    }
}

