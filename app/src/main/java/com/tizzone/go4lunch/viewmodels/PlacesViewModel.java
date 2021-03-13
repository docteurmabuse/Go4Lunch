package com.tizzone.go4lunch.viewmodels;


import android.util.Log;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.detail.Result;
import com.tizzone.go4lunch.models.prediction.Prediction;
import com.tizzone.go4lunch.repositories.PlaceRepository;
import com.tizzone.go4lunch.repositories.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;


@HiltViewModel
public class PlacesViewModel extends ViewModel {
    private static final String TAG = "PlacesViewModel";
    public static int userCount = 0;
    private final PlaceRepository placeRepository;
    private final UserRepository userRepository;

    public MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();
    private final MutableLiveData<List<Restaurant>> filteredRestaurants = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantMutableLiveData = new MutableLiveData<>();

    public ObservableBoolean isLoading = new ObservableBoolean(false);

    @Inject
    public PlacesViewModel(PlaceRepository placeRepository, UserRepository userRepository) {
        this.placeRepository = placeRepository;
        this.userRepository = userRepository;
    }

    public LiveData<List<Restaurant>> getRestaurantsList() {
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurantMutableLiveData;
    }

    public MutableLiveData<List<Restaurant>> getFilteredRestaurantsList() {
        return filteredRestaurants;
    }

    public void setRestaurants(String location, int radius) {
        placeRepository.getNearByPlacesApi(location, radius)
                .subscribeOn(Schedulers.io())
                .map(placesResults -> {
                    isLoading.set(true);
                    List<com.tizzone.go4lunch.models.places.Result> placesResultsList = placesResults.getResults();
                    List<Restaurant> restaurants = new ArrayList<>();
                    for (com.tizzone.go4lunch.models.places.Result result : placesResultsList) {
                        Boolean isOpen = null;
                        if (result.getOpeningHours() != null) {
                            isOpen = result.getOpeningHours().getOpenNow();
                        }
                        Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), userCount,
                                isOpen, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), null, null);
                        restaurants.add(restaurant);
                        userRepository.getQueryUsersByLunchSpotId(result.getPlaceId()).addSnapshotListener((value, error) -> {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                            } else {
                                if (value != null) {
                                    userCount = value.size();
                                    restaurant.setRestaurant_counter(userCount);
                                    Log.e(TAG, "apply: " + restaurant.getRestaurant_counter());
                                } else {
                                    Log.d(TAG, "Current data: null");
                                    restaurant.setRestaurant_counter(0);
                                }
                            }
                        });
                    }
                    if (placesResultsList.size() > 0)
                        Log.e(TAG, "apply: " + placesResultsList.get(0).getName());
                    return restaurants;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                            restaurantsList.setValue(result);
                            isLoading.set(false);
                        },
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
    }

    public void setRestaurant(String uid) {
        placeRepository.getDetailByPlaceId(uid)
                .subscribeOn(Schedulers.io())
                .map(placeDetail -> {
                    Result result = placeDetail.getResult();
                    Restaurant restaurant = new Restaurant();
                    Float rating;
                    if (placeDetail.getResult() != null) {
                        Log.e(TAG, "apply: " + result.getName());
                        rating = result.getRating();
                        restaurant = new Restaurant(uid, result.getName(), result.getFormattedAddress(), result.getPhotoUrl(), rating, 0,
                                null, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), result.getWebsite(), result.getInternationalPhoneNumber());

                    }
                    return restaurant;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantMutableLiveData::setValue,
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
    }

    public void setPredictions(String input, String location, int radius, int sessionToken) {
        placeRepository.getPredictionsApi(input, location, radius, sessionToken)
                .subscribeOn(Schedulers.io())
                .map(apiPredictions -> {
                    Log.e(TAG, "apply: " + apiPredictions.getPredictions().get(0).getPlaceId());
                    return apiPredictions.getPredictions();
                })
                .map(predictions -> {
                    List<Restaurant> filtered = new ArrayList<>();
                    List<Restaurant> restaurants = new ArrayList<>(Objects.requireNonNull(restaurantsList.getValue()));
                    if (predictions.size() > 0) {
                        for (Prediction prediction : predictions) {
                            String idPrediction = prediction.getPlaceId();
                            for (Restaurant restaurant : restaurants) {
                                String idRestaurant = restaurant.getUid();
                                if (idPrediction.matches(idRestaurant)) {
                                    filtered.add(restaurant);
                                }
                            }
                        }
                    } else {
                        filtered.addAll(restaurants);
                    }
                    return filtered;
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filteredRestaurants::setValue,
                        error -> Log.e(TAG, "setPredictions:" + error.getMessage())
                );
    }
}