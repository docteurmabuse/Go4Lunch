package com.tizzone.go4lunch;

import android.app.Application;

import androidx.lifecycle.MutableLiveData;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.models.Restaurant;
import com.tizzone.go4lunch.utils.FirebaseDataSource;

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

//    @Singleton
//    @Provides
//    static UserViewModel provideUserViewModel( SavedStateHandle savedStateHandle,UserRepository userRepository) {
//        return new UserViewModel(savedStateHandle, userRepository);
//    }
//
//    @Singleton
//    @Provides
//    static PlacesViewModel providePlaceViewModel(PlaceRepository placeRepository, SavedStateHandle savedStateHandle) {
//        return new PlacesViewModel(placeRepository,savedStateHandle);
//    }


//    @Singleton
//    @Provides
//    String provideString() {
//        return "Helloo this inject working";
//    }

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
    static MutableLiveData<List<Restaurant>> provideRestaurantList() {
        return new MutableLiveData<List<Restaurant>>();
    }


}
