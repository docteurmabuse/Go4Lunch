package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

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
    private static final String TAG = "FirebaseAuthAppTag";
    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final MutableLiveData<List<User>> userListMutableLiveData = new MutableLiveData<>();

    public MutableLiveData<User> userMutableLiveData = new MutableLiveData<>();
    public MutableLiveData<List<User>> firebaseUserLunchInThatSpotList = new MutableLiveData<>();
    public MutableLiveData<List<String>> favoriteLunchSpotListLiveData = new MutableLiveData<>();
    public MutableLiveData<User> userLiveData = new MutableLiveData<>();
    public MutableLiveData<String> userIdLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isLunchSpotLiveData = new MutableLiveData<>();
    public MutableLiveData<Boolean> isFavoriteLunchSpotLiveData = new MutableLiveData<>();
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
            userRepository.updateLunchSpot(null, userId);
            isLunchSpotLiveData.setValue(false);
            clickLunchSpotLiveData.setValue(false);
        } else {
            userRepository.updateLunchSpot(restaurant.getUid(), userId);
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
                String lunchSpot = user.getLunchSpot();
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

    public void getUserInfoFromFirestore(String userId) {
        userMutableLiveData = new MutableLiveData<>();
        userRepository.getUser(userId).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot documentSnapshot = task.getResult();
                User user = Objects.requireNonNull(documentSnapshot.toObject(User.class));
                userLiveData.setValue(user);
            }
        });
    }

    public void getUserLunchInThatSpotList(String lunchSpotId) {
        userRepository.getQueryUsersByLunchSpotId(lunchSpotId).addSnapshotListener((value, error) -> {
            if (error != null) {
                Log.w(TAG, "Listen failed.", error);
            } else {
                if (value != null) {
                    List<User> userList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : value) {
                        User user = document.toObject(User.class);
                        userList.add(user);
                    }
                    firebaseUserLunchInThatSpotList.setValue(userList);
                } else {
                    Log.d(TAG, "Current data: null");
                }
            }
        });
    }

    public LiveData<List<User>> getUserListLunchInThatSpot() {
        return firebaseUserLunchInThatSpotList;
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
    }

    public LiveData<List<User>> getUsersList() {
        return userListMutableLiveData;
    }

}
