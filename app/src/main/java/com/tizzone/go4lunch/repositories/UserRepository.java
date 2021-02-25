package com.tizzone.go4lunch.repositories;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.utils.FirebaseDataSource;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private final Query queryUsersByName;
    private final FirebaseDataSource firebaseDataSource;


    @Inject
    public UserRepository(Query queryUsersByName, FirebaseDataSource firebaseDataSource) {
        this.queryUsersByName = queryUsersByName;
        this.firebaseDataSource = firebaseDataSource;
    }

    public FirestoreRecyclerOptions<User> getUserList() {
        return firebaseDataSource.getUsersList();
    }

    public Flowable<QuerySnapshot> getUsers() {
        return firebaseDataSource.getUsers();
    }

    public List<User> getWorkmates(String currentUserId) {
        Log.e("Repo si working", firebaseDataSource.getWorkmates(currentUserId).toString());
        return firebaseDataSource.getWorkmates(currentUserId);
    }

    public MutableLiveData<List<User>> getFirebaseUsersLunch(String uid) {
        MutableLiveData<List<User>> firebaseUsersLunch = new MutableLiveData<>();
        queryUsersByName.whereNotEqualTo("uid", uid).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                int usersCount = task.getResult().size();
                firebaseUsersLunch.setValue(task.getResult().toObjects(User.class));
            } else {
                Log.w(TAG, "Listener failed.", task.getException());

            }
        });
        return firebaseUsersLunch;
    }


    public MutableLiveData<List<User>> getFirebaseUsers() {
        MutableLiveData<List<User>> firebaseUsers = new MutableLiveData<>();
        queryUsersByName.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                firebaseUsers.setValue(task.getResult().toObjects(User.class));
            } else {
                Log.w(TAG, "Listener failed.", task.getException());
            }
        });
        return firebaseUsers;
    }
}
