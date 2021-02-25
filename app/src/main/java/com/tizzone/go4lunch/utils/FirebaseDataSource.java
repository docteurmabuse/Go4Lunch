package com.tizzone.go4lunch.utils;

import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.BackpressureStrategy;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.FlowableEmitter;

import static com.tizzone.go4lunch.utils.Constants.COLLECTION_NAME;

public class FirebaseDataSource {
    private final FirebaseFirestore firebaseFirestore;
    private static final String TAG = "FirebaseAuthAppTag";

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

    public List<User> getWorkmates(String uid) {
        ArrayList<User> workmatesList = new ArrayList<>();
        firebaseFirestore.collection(COLLECTION_NAME).whereNotEqualTo("uid", uid)
                .get()
                .addOnSuccessListener(documentSnapshots -> {
                    for (QueryDocumentSnapshot queryDocumentSnapshot : documentSnapshots) {
                        User user = queryDocumentSnapshot.toObject(User.class);
                        Log.e("Workmates List", user.getUserName());
                        workmatesList.add(user);
                    }
                });
        return workmatesList;
    }

    public Flowable<QuerySnapshot> getUsers() {
        return Flowable.create((this::subscribe), BackpressureStrategy.BUFFER);
    }

    //Firestore users List
    private List<User> getUsersGetQuery() {
        ArrayList<User> workmatesList = new ArrayList<>();

        firebaseFirestore.collection(COLLECTION_NAME)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Log.d(TAG, document.getId() + " => " + document.getData());
                        }
                        workmatesList.addAll(task.getResult().toObjects(User.class));
                    } else {
                        Log.w(TAG, "Error getting documents.", task.getException());
                    }
                });
        return workmatesList;
    }

    private void subscribe(FlowableEmitter<QuerySnapshot> emitter) {
        CollectionReference reference = firebaseFirestore.collection(COLLECTION_NAME);
        final ListenerRegistration registration = (ListenerRegistration) reference.get()
                .addOnCompleteListener(task -> {
                    emitter.onNext(task.getResult());
                })
                .addOnFailureListener(e -> {
                    if (e != null) {
                        emitter.onError(e);
                    }
                });
        emitter.setCancellable(registration::remove);

    }
}
