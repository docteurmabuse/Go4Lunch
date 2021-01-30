package com.tizzone.go4lunch.viewmodels;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.models.places.PlacesResults;
import com.tizzone.go4lunch.models.places.Resource;
import com.tizzone.go4lunch.models.places.Result;
import com.tizzone.go4lunch.repositories.PlacesRepository;
import com.tizzone.go4lunch.repositories.Repository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Function;
import io.reactivex.rxjava3.schedulers.Schedulers;


@HiltViewModel
public class PlacesViewModel extends ViewModel {
    private static final String TAG = "RestaurantViewModel";
    private PlacesRepository placesRepository;
    private MutableLiveData<PlacesResults> mutableLiveDataPlaces;
    private MutableLiveData<PlacesResults> mutableLiveDataSearchPlaces;

    private final Repository repository;
    public String location;
    public int radius;
    public String type;
    public String key;
    private MutableLiveData<List<Restaurant>> restaurantsList;
    private MediatorLiveData<Resource<List<Result>>> resourceMediatorLiveData;


    @Inject
    public PlacesViewModel(Repository repository) {
        this.repository = repository;
        this.restaurantsList = restaurantsList;
    }


    public MutableLiveData<List<Restaurant>> getRestaurantsList() {
        if (restaurantsList == null) {
            restaurantsList = new MutableLiveData<List<Restaurant>>();
        }
        return restaurantsList;
    }

    /*public LiveData<Resource<List<Result>>> observeResults(String location, int radius, String type, String key){
        if (resourceMediatorLiveData == null){
            resourceMediatorLiveData = new MediatorLiveData<>();
            resourceMediatorLiveData.setValue(Resource.loading((List<Result>)null));

            final LiveData<Resource<List<Result>>> source = LiveDataReactiveStreams.fromPublisher(
                    repository.getNearByPlacesApi(location, radius, type, key)
                    .onErrorReturn(new Function<Throwable,  List<Result>>() {
                        @Override
                        public List<Result> apply(Throwable throwable) throws Throwable {
                            Log.e(TAG, "apply: ", throwable);
                            Result result = new Result();
                            List<Result> placesResults = new ArrayList<>();
                            result.setId("error");
                            placesResults.add(result);
                            return placesResults;
                        }
                    })
                    .map(new Function <List<Result>, Resource<List<Result>>>() {
                        @Override
                        public Resource<List<Result>> apply(List<Result> results) throws Throwable {
                            if (results.size()>0){
                                if (results.get(0).getId() == "error"){
                                    return Resource.error("Something went wrong", null);
                                }
                            }
                            return Resource.success(results);
                        }
                    })
                    .subscribeOn(Schedulers.io())
            );


            resourceMediatorLiveData.addSource(source, new Observer<Resource<List<Result>>>(){

                */

//
//                @Override
//                public void onChanged(Resource<List<Result>> listResource) {
//                    resourceMediatorLiveData.setValue(listResource);
//                    resourceMediatorLiveData.removeSource(source);
//                }
//            });
//        }
//        return resourceMediatorLiveData;
//    }*/

//    public MutableLiveData<List<Restaurant>> getRestaurants(){
//        return restaurantsList;
//    }

    public void getRestaurants(String location, int radius, String type, String key) {
        repository.getNearByPlacesApi(location, radius, type, key)
                .subscribeOn(Schedulers.io())
                .map(new Function<PlacesResults, List<Restaurant>>() {
                    @Override
                    public List<Restaurant> apply(PlacesResults placesResults) throws Throwable {
                        List<Result> placesResultsList = placesResults.getResults();
                        List<Restaurant> restaurants = new ArrayList<>();

                        for (Result result : placesResultsList) {
                            Boolean isOpen = null;
                            if (result.getOpeningHours() != null)
                                isOpen = result.getOpeningHours().getOpenNow();

                            Restaurant restaurant = new Restaurant(result.getPlaceId(), result.getName(), result.getVicinity(), result.getPhotoUrl(), result.getRating(), 0,
                                    isOpen, new LatLng(result.getGeometry().getLocation().getLat(), result.getGeometry().getLocation().getLng()));
                            restaurants.add(restaurant);
                        }
                        // Log.e(TAG, "apply: " + placesResultsList.get(0).getName());

                        return restaurants;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((result) -> {
                            restaurantsList.setValue(result);
                        }
                        //   error -> Log.e(TAG, "getRestaurants:" + error.getMessage())
                );
    }

    public void getNearByPlaces(String location, int radius, String type, String key) {
        placesRepository.getNearByPlaces(location, radius, type, key, new PlacesRepository.PlacesResultsInterface() {
            @Override
            public void onResponse(PlacesResults placesResults) {
                mutableLiveDataPlaces.setValue(placesResults);
            }
        });
    }

    public void getDetailByPlaceId(String placeId, String fields, String key) {
        placesRepository.getDetailByPlaceId(placeId, fields, key, new PlacesRepository.PlacesResultsInterface() {
            @Override
            public void onResponse(PlacesResults placesResults) {
                mutableLiveDataSearchPlaces.setValue(placesResults);
            }
        });
    }

    public LiveData<PlacesResults> getPlacesResultsLiveData() {
        if (mutableLiveDataPlaces == null) {
            mutableLiveDataPlaces = new MutableLiveData<PlacesResults>();
        }
        return mutableLiveDataPlaces;
    }


    public LiveData<PlacesResults> getPlacesResultsSearchPlaces() {
        if (mutableLiveDataSearchPlaces == null) {
            mutableLiveDataSearchPlaces = new MutableLiveData<PlacesResults>();
        }
        return mutableLiveDataSearchPlaces;
    }

    public LiveData<List<Restaurant>> getRestaurantsLiveData() {
        if (restaurantsList == null) {
            restaurantsList = new MutableLiveData<List<Restaurant>>();
        }
        return restaurantsList;
    }

}
