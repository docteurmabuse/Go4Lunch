package com.tizzone.go4lunch.viewmodels;


import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.SavedStateHandle;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.detail.PlaceDetail;
import com.tizzone.go4lunch.models.detail.Result;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.prediction.Prediction;
import com.tizzone.go4lunch.models.prediction.Predictions;
import com.tizzone.go4lunch.repositories.PlaceRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


@HiltViewModel
public class PlacesViewModel extends ViewModel {
    private static final String TAG = "RestaurantViewModel";

    private final PlaceRepository placeRepository;
    private final SavedStateHandle savedStateHandle;

    public MutableLiveData<List<Restaurant>> matesRestaurantsList = new MutableLiveData<>();
    public String location;
    public String key;
    public MutableLiveData<List<Restaurant>> restaurantsList = new MutableLiveData<>();
    private UserViewModel userViewModel;

    private final MutableLiveData<List<Restaurant>> filteredRestaurants = new MutableLiveData<>();
    private final MutableLiveData<Restaurant> restaurantMutableLiveData = new MutableLiveData<>();

    @Inject
    public PlacesViewModel(SavedStateHandle savedStateHandle, PlaceRepository placeRepository) {
        this.savedStateHandle = savedStateHandle;
        this.placeRepository = placeRepository;
    }


    public MutableLiveData<List<Restaurant>> getRestaurantsList() {
        if (restaurantsList == null) {
            restaurantsList = new MutableLiveData<List<Restaurant>>();
        }
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> getRestaurant() {
        return restaurantMutableLiveData;
    }

    public MutableLiveData<List<Restaurant>> getMatesRestaurantsList() {

        return matesRestaurantsList;
    }


    public MutableLiveData<List<Restaurant>> getFilteredRestaurantsList() {
        return filteredRestaurants;
    }

    public MutableLiveData<List<Restaurant>> setRestaurants(String location, int radius) {
        List<com.tizzone.go4lunch.models.places.Result> placesResultsList;
        placeRepository.getNearByPlacesApi(location, radius)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlacesResults, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(PlacesResults placesResults) throws Throwable {
                        List<com.tizzone.go4lunch.models.places.Result> placesResultsList = placesResults.getResults();
                        List<Restaurant> restaurants = new ArrayList<>();

                        for (com.tizzone.go4lunch.models.places.Result result : placesResultsList) {
                            Boolean isOpen = null;
                            if (result.getOpeningHours() != null)
                                isOpen = result.getOpeningHours().getOpenNow();

                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), 0,
                                    isOpen, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), null, null);
                            restaurants.add(restaurant);
                        }
                        Log.e(TAG, "apply: " + placesResultsList.get(0).getName());
                        return restaurants;
                    }

                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                          //  restaurantsList.setValue(new ArrayList<>());
                            restaurantsList.setValue(result);
                        },
                        error -> Log.e(TAG, "setRestaurants:" + error.getMessage())
                );
        return restaurantsList;
    }

    public MutableLiveData<Restaurant> setRestaurant(String uid) {
        placeRepository.getDetailByPlaceId(uid)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlaceDetail, Restaurant>() {
                    @Override
                    public Restaurant apply(PlaceDetail placeDetail) throws Throwable {
                        Result result = placeDetail.getResult();
                        Restaurant restaurant = new Restaurant();
                        Float rating = null;
                        if (placeDetail.getResult() != null) {
                            Log.e(TAG, "apply: " + result.getName());
                            rating = result.getRating();
                            restaurant = new Restaurant(uid, result.getName(), result.getFormattedAddress(), result.getPhotoUrl(), rating, 0,
                                    null, result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng(), result.getWebsite(), result.getInternationalPhoneNumber());
                        }
                        return restaurant;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(restaurantMutableLiveData::setValue);
        return restaurantMutableLiveData;
    }

    public void setPredictions(String input, String location, int radius, int sessiontoken, String key) {
        placeRepository.getPredictionsApi(input, location, radius, sessiontoken)
                .subscribeOn(Schedulers.io())
                .map(new Function<Predictions, List<Prediction>>() {
                    @Override
                    public List<Prediction> apply(Predictions apiPredictions) throws Throwable {
                        Log.e(TAG, "apply: " + apiPredictions.getPredictions().get(0).getPlaceId());
                        return apiPredictions.getPredictions();
                    }
                })
                .map(new Function<List<Prediction>, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(List<Prediction> predictions) throws Throwable {
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
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(filteredRestaurants::setValue,
                        error -> Log.e(TAG, "setPredictions:" + error.getMessage())
                );
    }


}
