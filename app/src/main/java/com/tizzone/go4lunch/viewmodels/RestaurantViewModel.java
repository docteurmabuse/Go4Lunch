package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;

import java.util.List;

public class RestaurantViewModel extends ViewModel {
    public MutableLiveData<List<Restaurant>> restaurantLiveData = new MutableLiveData<List<Restaurant>>();
    private List<Restaurant> restaurants;

    public RestaurantViewModel(List<Restaurant> restaurants) {
        this.restaurants = restaurants;
    }

    public RestaurantViewModel() {

    }

    public LiveData<List<Restaurant>> getRestaurants() {
        return restaurantLiveData;
    }

    public void setRestaurants(List<Restaurant> restaurants) {
        restaurantLiveData.setValue(restaurants);
    }

}
