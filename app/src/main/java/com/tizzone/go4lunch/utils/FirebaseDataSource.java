package com.tizzone.go4lunch.utils;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.tizzone.go4lunch.models.User;

import org.reactivestreams.Subscriber;

import javax.inject.Inject;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.functions.Cancellable;

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

    // --- READ ---
    public Flowable<Task<DocumentSnapshot>> getUser(String uid) {
        return new Flowable<DocumentSnapshot>() {
            /**
             * Operator implementations (both source and intermediate) should implement this method that
             * performs the necessary business logic and handles the incoming {@link Subscriber}s.
             * <p>There is no need to call any of the plugin hooks on the current {@code Flowable} instance or
             * the {@code Subscriber}; all hooks and basic safeguards have been
             * applied by {@link #subscribe(Subscriber)} before this method gets called.
             *
             * @param subscriber the incoming {@code Subscriber}, never {@code null}
             */
            @Override
            protected void subscribeActual(@NonNull Subscriber<? super DocumentSnapshot> subscriber) {
                DocumentReference reference = firebaseFirestore.collection(COLLECTION_NAME).document(uid);
                final ListenerRegistration registration = reference.addSnapshotListener((documentSnapshot, e) -> {
                    if ()
                });
                emitter.setCancellable(new Cancellable() {
                    @Override
                    public void cancel() throws Exception {
                        registration.remove();
                    }
                });

            }
        },BackpressureStrategy.BUFFER)
        //return firebaseFirestore.document(uid).get();
    }

}
