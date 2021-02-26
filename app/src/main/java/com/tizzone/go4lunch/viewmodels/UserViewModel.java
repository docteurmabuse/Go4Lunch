package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private static final String TAG = "FirebaseAuthAppTag";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = rootRef.collection("USERS");
    private final SavedStateHandle savedStateHandle;
    private final UserRepository userRepository;
    private final MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    public LiveData<List<User>> usersList;
    MutableLiveData<List<User>> firebaseUsers = new MutableLiveData<>();
    MutableLiveData<List<String>> firebaseUsersSpotList = new MutableLiveData<>();

    public MutableLiveData<String> userIdLiveData = new MutableLiveData<>();
    public LiveData<List<User>> userListLiveData;


    @Inject
    public UserViewModel(SavedStateHandle savedStateHandle, UserRepository userRepository) {
        this.savedStateHandle = savedStateHandle;
        this.userRepository = userRepository;
    }

    public void setUserId(String uid) {
        userIdLiveData.setValue(uid);
    }

    public LiveData<String> getCurrentUserId() {
        return userIdLiveData;
    }

    public MutableLiveData<User> addUserToLiveData(String uid) {
        userMutableLiveData = new MutableLiveData<>();
        usersRef.document(uid).get().addOnCompleteListener(userTask -> {
            if (userTask.isSuccessful()) {
                DocumentSnapshot document = userTask.getResult();
                if (document.exists()) {
                    User user = document.toObject(User.class);
                    userMutableLiveData.setValue(user);
                }
            } else {
                logErrorMessage(userTask.getException().getMessage());
            }
        });
        return userMutableLiveData;
    }

    public static void logErrorMessage(String errorMessage) {
        Log.d(TAG, errorMessage);
    }

    public MutableLiveData<List<User>> getFirebaseUsers() {
        UserHelper.getUsers().get().addOnCompleteListener(task -> {
                    firebaseUsers.setValue(task.getResult().toObjects(User.class));
                    Log.e(TAG, "size: " + (task.getResult().toObjects(User.class).size()));

                    List<String> usersSpot = new ArrayList();
                    for (User user : task.getResult().toObjects(User.class)) {
                        usersSpot.add(user.getUid());
                    }
                    firebaseUsersSpotList.setValue(usersSpot);
                }
        );
        return firebaseUsers;
    }


    public LiveData<List<User>> getUsersList() {
        return userListMutableLiveData;
    }

    public LiveData<List<String>> getUserSpotList() {
        return firebaseUsersSpotList;
    }

    public void getUserMutableLiveData() {
        userRepository.getQueryUsersByName().addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
            } else {
                if (value != null) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    userListMutableLiveData.setValue(userList);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
//        }
//        }).get()
//        .addOnCompleteListener(userListTask -> {
//            if (userListTask.isSuccessful()) {
//                List<User> userList = new ArrayList<>();
//                for (QueryDocumentSnapshot document : userListTask.getResult()) {
//                    User user = document.toObject(User.class);
//                    userList.add(user);
//                }
//                userListMutableLiveData.setValue(userList);
//            } else {
//                Log.d(TAG, userListTask.getException().getMessage());
//            }
//        });
    }
}
