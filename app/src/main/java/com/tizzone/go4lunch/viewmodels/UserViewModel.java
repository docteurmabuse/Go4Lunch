package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.User;

public class UserViewModel extends ViewModel {
    private UserRepository userRepository;
    LiveData<User> isUserAuthenticatedLiveData;

    public LiveData<User> getUserLiveData() {
        return userLiveData;
    }

    public UserViewModel setUserLiveData(LiveData<User> userLiveData) {
        this.userLiveData = userLiveData;
        return this;
    }

    LiveData<User> userLiveData;



    public UserViewModel(LiveData<User> userLiveData) {
        this.userLiveData = userLiveData;
    }
}
