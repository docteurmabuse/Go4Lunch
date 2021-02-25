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
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.utils.UserHelper;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private static final String TAG = "FirebaseAuthAppTag";
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore rootRef = FirebaseFirestore.getInstance();
    private final CollectionReference usersRef = rootRef.collection("USERS");
    private final SavedStateHandle savedStateHandle;
    private final UserRepository userRepository;

    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    public MutableLiveData<List<User>> usersList = new MutableLiveData<>();
    MutableLiveData<List<User>> firebaseUsers = new MutableLiveData<>();
    MutableLiveData<List<String>> firebaseUsersSpotList = new MutableLiveData<>();

    public MutableLiveData<String> userIdLiveData = new MutableLiveData<>();

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

    public MutableLiveData<List<User>> getUsersByIdLiveData(String uid) {
        return userRepository.getFirebaseUsersLunch(uid);
    }

//    public FirestoreRecyclerOptions<User> getUsersMutableLiveData() {
//        return userRepository.getUserList();
//    }


    public MutableLiveData<List<User>> getUsersLiveData() {
        userRepository.getUsers()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribe(new Observer<QuerySnapshot>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        disposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull QuerySnapshot queryDocumentSnapshots) {
                        List<User> users = queryDocumentSnapshots.getQuery().get().getResult().toObjects(User.class);
                        System.out.println("Repository ViewModel is working" + users.toString());
                        usersList.setValue(users);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        logErrorMessage(e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        System.out.println("Repository ViewModel on Complete is working");
                    }
                });
        return usersList;
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
                    List<String> usersSpot = new ArrayList();
                    for (User user : task.getResult().toObjects(User.class)) {
                        usersSpot.add(user.getUid());
                    }
                    firebaseUsersSpotList.setValue(usersSpot);
                }
        );
        return firebaseUsers;
    }

    public LiveData<List<User>> getFirebaseUsersLiveData() {
        return firebaseUsers;
    }

    public LiveData<List<String>> getUserSpotList() {
        return firebaseUsersSpotList;
    }

}
