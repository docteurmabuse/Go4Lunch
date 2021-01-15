package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.User;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    public final MutableLiveData<User> userLocation = new MutableLiveData<User>();

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public void setUserLiveData(User user) {
        userLocation.setValue(user);
    }

    LiveData<User> userLiveData;

    public UserViewModel(LiveData<User> userLiveData) {
        this.userLiveData = userLiveData;
    }

    public void userLocation(double latitude, double longitude) {
    }
}
