package com.tizzone.go4lunch.viewmodels;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.repositories.PlaceRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

@HiltViewModel
public class SharedViewModel extends ViewModel {

    private static final String TAG = "SharedViewModel";
    private final String randomString;
    private final PlaceRepository placeRepository;
    private final SavedStateHandle savedStateHandle;
    public MutableLiveData<List<Restaurant>> restaurantsList;

    @Inject
    public SharedViewModel(String randomString, PlaceRepository placeRepository, SavedStateHandle savedStateHandle, MutableLiveData<List<Restaurant>> restaurantsList) {
        this.randomString = randomString;
        this.placeRepository = placeRepository;
        this.savedStateHandle = savedStateHandle;
        this.restaurantsList = restaurantsList;
        init();
    }


    public void init() {
        System.out.println("ViewModel:" + randomString);
    }

    public MutableLiveData<List<Restaurant>> setRestaurants(String location, int radius) {
        placeRepository.getNearByPlacesApi(location, radius)
                .subscribeOn(Schedulers.io())
                .map(placesResults -> {
                    List<com.tizzone.go4lunch.models.places.Result> placesResultsList1 = placesResults.getResults();
                    List<Restaurant> restaurants = new ArrayList<>();

                    for (com.tizzone.go4lunch.models.places.Result result : placesResultsList1) {
                        Boolean isOpen = null;
                        if (result.getOpeningHours() != null)
                            isOpen = result.getOpeningHours().getOpenNow();

                        Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), 0,
                                isOpen, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), null, null);
                        restaurants.add(restaurant);
                    }
                    Log.e(TAG, "apply restaurant list: " + placesResultsList1.get(1).getName());
                    return restaurants;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            //  restaurantsList.setValue(new ArrayList<>());
                            Log.e(TAG, "apply restaurant list2: " + result.get(2).getName());

                            restaurantsList.setValue(result);
                        },
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
        return restaurantsList;
    }

    public LiveData<List<Restaurant>> getRestaurantsList() {
//        if (restaurantsList == null) {
//            restaurantsList = new MutableLiveData<>();
//        }
        if (restaurantsList.getValue() != null)
            Log.e(TAG, "apply restaurant list0: " + restaurantsList.getValue().get(0).getName());

        return restaurantsList;
    }

}
