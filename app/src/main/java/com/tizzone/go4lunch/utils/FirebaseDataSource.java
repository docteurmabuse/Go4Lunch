package com.tizzone.go4lunch.utils;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;

public class FirebaseDataSource {
    private static final String COLLECTION_NAME = "users";
    private final FirebaseFirestore firebaseFirestore;

    @Inject
    public FirebaseDataSource(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    //Firestore users List
    private Query getUsersQuery() {
        return firebaseFirestore.collection(COLLECTION_NAME);
    }

    public FirestoreRecyclerOptions<User> getUsersList() {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(getUsersQuery(), User.class)
                .build();
    }

    public Query getWorkmates(String uid) {
        return firebaseFirestore.collection(COLLECTION_NAME).whereNotEqualTo("uid", uid);
    }

    public Flowable<QuerySnapshot> getUsers() {
        return Flowable.create((emitter -> {
            CollectionReference reference = firebaseFirestore.collection(COLLECTION_NAME);
            final ListenerRegistration registration = reference.addSnapshotListener((documentSnapshot, e) -> {
                if (e != null) {
                    emitter.onError(e);
                }
                if (documentSnapshot != null) {
                    emitter.onNext(documentSnapshot);
                }
            });
            emitter.setCancellable(() -> {
                registration.remove();
            });

        }), BackpressureStrategy.BUFFER);
    }
}
