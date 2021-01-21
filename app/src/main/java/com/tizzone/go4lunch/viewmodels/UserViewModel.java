package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.User;

public class UserViewModel extends ViewModel {
    public MutableLiveData<User> userLiveData = new MutableLiveData<User>();
    private User user;

    public UserViewModel(User user) {
        this.user = user;
    }


    public UserViewModel() {

    }

    public LiveData<User> getUser() {
        return userLiveData;
    }

    public void setUser(User user) {
        userLiveData.setValue(user);
    }

}
