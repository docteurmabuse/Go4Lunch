package com.tizzone.go4lunch.viewmodels;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;


public class RestaurantViewModel extends ViewModel {
    public MutableLiveData<Restaurant> restaurantLiveData = new MutableLiveData<>();


    public RestaurantViewModel() {

    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurantLiveData;
    }

    public void setRestaurant(Restaurant restaurant) {
        restaurantLiveData.setValue(restaurant);
    }

}
