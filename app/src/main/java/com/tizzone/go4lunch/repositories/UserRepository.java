package com.tizzone.go4lunch.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class UserRepository {

    public MutableLiveData<List<User>> getFirebaseUsersLunch(String uid) {
        MutableLiveData<List<User>> firebaseUsers = new MutableLiveData<>();
        UserHelper.getUsersLunchSpot(uid).
                addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException
                            error) {
                        if (error != null) {
                            Log.w(TAG, "Listener failed.", error);
                            List<User> wRestaurants = new ArrayList<>();
                            firebaseUsers.setValue(wRestaurants);

                        } else {
                            int usersCount = value.size();
                            firebaseUsers.setValue(value.toObjects(User.class));
                        }
                    }
                });
        return firebaseUsers;
    }


}
