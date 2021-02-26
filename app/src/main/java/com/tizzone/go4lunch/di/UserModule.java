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
import static com.tizzone.go4lunch.utils.Constants.COLLECTION_NAME;
import static com.tizzone.go4lunch.utils.Constants.NAME_PROPERTY;

@InstallIn(SingletonComponent.class)
@Module
public class UserModule {

    @Singleton
    @Provides
    static Query provideQueryUsers() {
        return FirebaseFirestore.getInstance()
                .collection(COLLECTION_NAME)
                .orderBy(NAME_PROPERTY, ASCENDING);
    }


    @Singleton
    @Provides
    static CollectionReference provideUsersCollectionReference(FirebaseFirestore rootRef) {
        return rootRef.collection(COLLECTION_NAME);
    }

}
