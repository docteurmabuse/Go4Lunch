package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.RestaurantRepository;
import com.tizzone.go4lunch.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class UserViewModel extends ViewModel {
    private static final String TAG_FIREBASE_USER = "FirebaseAuthAppTag";
    private static final String TAG_USER = "FIREBASE_CREATE_USER";
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<User>> firebaseUserLunchInThatSpotList = new MutableLiveData<>();
    public MutableLiveData<List<String>> favoriteLunchSpotListLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public MutableLiveData<String> userIdLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLunchSpotLiveData = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isFavoriteLunchSpotLiveData = new MutableLiveData<>(false);
    public MutableLiveData<Boolean> isAppBarCollapsed = new MutableLiveData<>();
    public MutableLiveData<Boolean> clickLunchSpotLiveData = new MutableLiveData<>();

    @Inject
    public UserViewModel(UserRepository userRepository, RestaurantRepository restaurantRepository) {
        this.userRepository = userRepository;
        this.restaurantRepository = restaurantRepository;
    }

    public void setAppBarIsCollapsed(Boolean isCollapsed) {
        isAppBarCollapsed.setValue(isCollapsed);
    }

    public LiveData<Boolean> getIsAppBarCollapsed() {
        return isAppBarCollapsed;
    }

    public void setUserId(String uid) {
        userIdLiveData.setValue(uid);
    }

    public LiveData<String> getCurrentUserId() {
        return userIdLiveData;
    }

    public LiveData<User> getCurrentUser() {
        return userLiveData;
    }


    public LiveData<Boolean> getFabClickResult() {
        return clickLunchSpotLiveData;
    }

    public void updateLunchSpotUser(Boolean isLunchSpot, Restaurant restaurant, String userId) {
        if (isLunchSpot) {
            userRepository.updateLunchSpot(null, null, userId);
            isLunchSpotLiveData.setValue(false);
            clickLunchSpotLiveData.setValue(false);
        } else {
            userRepository.updateLunchSpot(restaurant.getUid(), restaurant.getName(), userId);
            restaurantRepository.createRestaurant(restaurant);
            isLunchSpotLiveData.setValue(true);
            clickLunchSpotLiveData.setValue(true);
        }
    }

    public void updateFavoriteLunchSpotUser(Boolean isFavoriteLunchSpot, List<String> favoriteRestaurants, String restaurantId, String userId) {
        if (isFavoriteLunchSpot) {
            if (favoriteRestaurants == null) {
                favoriteRestaurants = new ArrayList<>();
            }
            favoriteRestaurants.remove(restaurantId);
            userRepository.updateFavoriteRestaurants(null, userId);
            isFavoriteLunchSpotLiveData.setValue(false);
            favoriteLunchSpotListLiveData.setValue(favoriteRestaurants);
        } else {
            if (favoriteRestaurants == null) {
                favoriteRestaurants = new ArrayList<>();
            }
            favoriteRestaurants.add(restaurantId);
            userRepository.updateFavoriteRestaurants(favoriteRestaurants, userId);
            isFavoriteLunchSpotLiveData.setValue(true);
            favoriteLunchSpotListLiveData.setValue(favoriteRestaurants);
        }
    }

    public LiveData<Boolean> getIsLunchSpot() {
        return isLunchSpotLiveData;
    }

    public LiveData<Boolean> getIsFavoriteLunchSpot() {
        return isFavoriteLunchSpotLiveData;
    }

    public LiveData<List<String>> getFavoriteListLunchSpot() {
        return favoriteLunchSpotListLiveData;
    }

    public void getUserLunchInfoFromFirestore(String userId, String lunchSpotId) {
        userMutableLiveData = new MutableLiveData<>();
        userRepository.getUser(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                boolean isLunchSpot;
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = Objects.requireNonNull(documentSnapshot.toObject(User.class));
                String lunchSpot = user.getLunchSpotId();
                if (lunchSpot != null) {
                    isLunchSpot = lunchSpot.equals(lunchSpotId);
                } else {
                    isLunchSpot = false;
                }
                isLunchSpotLiveData.setValue(isLunchSpot);
                boolean isFavoriteLunchSpot;
                List<String> favoriteRestaurantsList = user.getFavoriteRestaurants();
                if (favoriteRestaurantsList != null) {
                    isFavoriteLunchSpot = favoriteRestaurantsList.contains(lunchSpotId);
                } else {
                    isFavoriteLunchSpot = false;
                }
                isFavoriteLunchSpotLiveData.setValue(isFavoriteLunchSpot);
                favoriteLunchSpotListLiveData.setValue(favoriteRestaurantsList);
            }
        });
    }

    public MutableLiveData<User> getUserInfoFromFirestore(String userId) {
        userRepository.getUser(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = documentSnapshot.toObject(User.class);
                userLiveData.setValue(user);
            } else {
                Log.d(TAG_FIREBASE_USER, "Current user: null");
            }
        });
        return userLiveData;
    }

    public MutableLiveData<List<User>> getUserLunchInThatSpotList(String lunchSpotId) {
        userRepository.getQueryUsersByLunchSpotId(lunchSpotId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG_FIREBASE_USER, "Listen failed.", error);
            } else {
                if (value != null) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    Log.d(TAG_FIREBASE_USER, "Listen User list." + userList.size());

                    firebaseUserLunchInThatSpotList.setValue(userList);

                } else {
                    Log.d(TAG_FIREBASE_USER, "Current data: null");
                }
            }
        });
        return firebaseUserLunchInThatSpotList;
    }

    public LiveData<List<User>> getUserListLunchInThatSpot() {
        return firebaseUserLunchInThatSpotList;
    }

    public void getUserMutableLiveData() {
        userRepository.getQueryUsersByName().addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG_FIREBASE_USER, "Listen failed.", error);
            } else {
                if (value != null) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    userListMutableLiveData.setValue(userList);
                } else {
                    Log.d(TAG_FIREBASE_USER, "Current data: null");
                }
            }
        });
    }

    public LiveData<List<User>> getUsersList() {
        return userListMutableLiveData;
    }

    public void createUser(FirebaseUser currentUser) {
        String urlPicture = (currentUser.getPhotoUrl() != null ? currentUser.getPhotoUrl().toString() : null);
        String username = currentUser.getDisplayName();
        String uid = currentUser.getUid();
        String userEmail = currentUser.getEmail();
        if (getUserInfoFromFirestore(uid).getValue() == null) {
            Log.d(TAG_USER, "User don't exist in firebase");
            userRepository.createUser(uid, userEmail, username, urlPicture, null, null, null).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.w(TAG_USER, "Error:" + e);
                }
            });
        } else {
            Log.w(TAG_USER, "User already exist:" + Objects.requireNonNull(getUserInfoFromFirestore(uid).getValue()).getUid());
        }
    }
}
