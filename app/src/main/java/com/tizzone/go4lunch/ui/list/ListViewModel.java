package com.tizzone.go4lunch.ui.list;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.tizzone.go4lunch.models.places.PlacesResults;

public class ListViewModel extends ViewModel {

    private MutableLiveData<PlacesResults> mutableLiveDataPlaces = new MutableLiveData<>();

//    public ListViewModel() {
//        mText = new MutableLiveData<>();
//        mText.setValue("This is list fragment");
//    }

   // public LiveData<String> getText() {
     //   return mText;
    //}

    public void setMutableLiveDataPlaces(PlacesResults placesResults){
        mutableLiveDataPlaces.setValue(placesResults);
    }
}