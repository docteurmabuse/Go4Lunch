package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;

import java.util.List;

public class UserViewModel extends ViewModel {
    private static final String TAG = "FirebaseAuthAppTag";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = rootRef.collection("USERS");
    public MutableLiveData<User> userMutableLiveData;
    private final UserRepository userRepository;

    public MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

    public UserViewModel(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public MutableLiveData<List<User>> addUserToLiveData(String restaurantId) {
        userMutableLiveData = new MutableLiveData<>();
        return userRepository.getFirebaseUsersLunch(restaurantId);
    }

    public MutableLiveData<List<User>> getUsersByIdLiveData(String uid) {
        return userRepository.getFirebaseUsersLunch(uid);
    }

    public MutableLiveData<List<User>> getUsersMutableLiveData() {
        return userRepository.getFirebaseUsers();
    }


    public static void logErrorMessage(String errorMessage) {
        Log.d(TAG, errorMessage);
    }
}
