package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;

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


    public MutableLiveData<List<User>> usersMutableLiveData = new MutableLiveData<>();

    @Inject
    public UserViewModel(SavedStateHandle savedStateHandle, UserRepository userRepository) {
        this.savedStateHandle = savedStateHandle;
        this.userRepository = userRepository;
    }

    public MutableLiveData<List<User>> addUserToLiveData(String restaurantId) {
        return userRepository.getFirebaseUsersLunch(restaurantId);
    }

    public MutableLiveData<List<User>> getUsersByIdLiveData(String uid) {
        return userRepository.getFirebaseUsersLunch(uid);
    }

    public FirestoreRecyclerOptions<User> getUsersMutableLiveData() {
        return userRepository.getUserList();
    }


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
                        System.out.println("ViewModel is working" + users.toString());
                        usersList.setValue(users);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return usersList;
    }


    public static void logErrorMessage(String errorMessage) {
        Log.d(TAG, errorMessage);
    }
}
