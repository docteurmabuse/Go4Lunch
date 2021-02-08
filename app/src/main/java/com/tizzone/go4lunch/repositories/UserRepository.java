package com.tizzone.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.utils.FirebaseDataSource;
import com.tizzone.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Flowable;

import static android.content.ContentValues.TAG;

public class UserRepository {

    private final FirebaseDataSource firebaseDataSource;
    private UserHelper userHelper;

    @Inject
    public UserRepository(FirebaseDataSource firebaseDataSource) {
        this.firebaseDataSource = firebaseDataSource;
    }


    public MutableLiveData<List<User>> getFirebaseUsersLunch(String uid) {
        MutableLiveData<List<User>> firebaseUsersLunch = new MutableLiveData<>();
        UserHelper.getUsersLunchSpot(uid).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException
                            error) {
                        if (error != null) {
                            Log.w(TAG, "Listener failed.", error);
                            List<User> wRestaurants = new ArrayList<>();
                            firebaseUsersLunch.setValue(wRestaurants);

                        } else {
                            int usersCount = value.size();
                            firebaseUsersLunch.setValue(value.toObjects(User.class));
                        }
                    }
                });
        return firebaseUsersLunch;
    }


    public MutableLiveData<List<User>> getFirebaseUsers() {
        MutableLiveData<List<User>> firebaseUsers = new MutableLiveData<>();
        UserHelper.getUsers().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                firebaseUsers.setValue(task.getResult().toObjects(User.class));
            }
        });
        return firebaseUsers;
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


    public String doAThing() {
        return "Injection work";
    }
}
