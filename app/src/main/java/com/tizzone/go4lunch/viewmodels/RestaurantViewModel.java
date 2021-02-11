package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;

import java.util.List;


public class RestaurantViewModel extends ViewModel {
    public MutableLiveData<List<Restaurant>> restaurantLiveData = new MutableLiveData<>();


    public RestaurantViewModel() {

    }

    public MutableLiveData<List<Restaurant>> getRestaurants() {
        return restaurantLiveData;
    }

    public void setRestaurantsList(List<Restaurant> restaurants) {
        restaurantLiveData.setValue(restaurants);
    }

}
