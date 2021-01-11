package com.tizzone.go4lunch.ui.gallery;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class GalleryModel extends ViewModel {

    private final MutableLiveData<String> mText;

    public GalleryModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is workmates fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}