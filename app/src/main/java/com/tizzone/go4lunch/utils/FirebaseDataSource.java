package com.tizzone.go4lunch.utils;

import android.util.Log;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tizzone.go4lunch.models.User;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.tizzone.go4lunch.utils.Constants.COLLECTION_USER_NAME;

public class FirebaseDataSource {
    private final FirebaseFirestore firebaseFirestore;
    private static final String TAG = "FirebaseAuthAppTag";

    @Inject
    public FirebaseDataSource(FirebaseFirestore firebaseFirestore) {
        this.firebaseFirestore = firebaseFirestore;
    }

    //Firestore users List
    private Query getUsersQuery() {
        return firebaseFirestore.collection(COLLECTION_USER_NAME);
    }

    public FirestoreRecyclerOptions<User> getUsersList() {
        return new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(getUsersQuery(), User.class)
                .build();
    }

    public List<User> getWorkmates(String uid) {
        ArrayList<User> workmatesList = new ArrayList<>();
        firebaseFirestore.collection(COLLECTION_USER_NAME).whereNotEqualTo("uid", uid)
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


    //Firestore users List
    private List<User> getUsersGetQuery() {
        ArrayList<User> workmatesList = new ArrayList<>();

        firebaseFirestore.collection(COLLECTION_USER_NAME)
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

}
