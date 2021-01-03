package com.tizzone.go4lunch.viewmodels;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.LiveData;

import com.tizzone.go4lunch.models.user.User;

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
