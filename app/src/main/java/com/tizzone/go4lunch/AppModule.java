package com.tizzone.go4lunch;

import android.app.Application;

import androidx.fragment.app.FragmentFactory;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.ui.MainFragmentFactory;
import com.tizzone.go4lunch.utils.FirebaseDataSource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
    public AppModule() {
    }

    @Singleton
    @Provides
    static FirebaseFirestore provideFirebaseInstance() {
        return FirebaseFirestore.getInstance();
    }

    @Singleton
    @Provides
    static RequestManager provideGlideInstance(Application application, RequestOptions requestOptions) {
        return Glide.with(application)
                .setDefaultRequestOptions(requestOptions);
    }

    @Singleton
    @Provides
    static RequestOptions provideRequestOptions() {
        return RequestOptions.placeholderOf(R.mipmap.avatar)
                .error(R.mipmap.avatar);
    }

    @Singleton
    @Provides
    static FirebaseDataSource provideFirebaseDataSource(FirebaseFirestore firebaseFirestore) {
        return new FirebaseDataSource(firebaseFirestore);
    }

    @Singleton
    @Provides
    static Boolean provideBoolean(boolean isWorkmates) {
        return isWorkmates;
    }

    @Singleton
    @Provides
    static UsersListAdapter.Listener provideListener(UsersListAdapter.Listener callback) {
        return callback;
    }

    @Singleton
    @Provides
    static FragmentFactory provideMainFragmentFactory(String randomString, LiveData<List<Restaurant>> restaurantList) {
        return new MainFragmentFactory(randomString, restaurantList);
    }

    @Singleton
    @Provides
    static LiveData<List<Restaurant>> provideLiveData() {
        return new MutableLiveData<>();
    }

    @Singleton
    @Provides
    static MutableLiveData<List<Restaurant>> provideMutableLiveData() {
        return new MutableLiveData<>();
    }

    @Provides
    static List<Restaurant> provideRestaurantList() {
        return new ArrayList<>();
    }

    @Provides
    static Restaurant provideRestaurant() {
        return new Restaurant();
    }
}
