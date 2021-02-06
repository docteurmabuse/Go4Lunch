package com.tizzone.go4lunch.di;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.network.PlacesApiService;
import com.tizzone.go4lunch.repositories.UserRepository;

import org.jetbrains.annotations.Nullable;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
@InstallIn(SingletonComponent.class)
public class NetworkModule {

    @Provides
    @Singleton
    public PlacesApiService provideGooglePlacesApiService() {
        return new Retrofit.Builder()
                .baseUrl("https://maps.googleapis.com/maps/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
                .create(PlacesApiService.class);
    }

    @Provides
    static FirestoreRecyclerOptions<User> provideOption(UserRepository userRepository) {
        return userRepository.getUserList();
    }

    @Provides
    FirebaseFirestore provideFirebaseFirestore() {
        return FirebaseFirestore.getInstance();
    }

    @Nullable
    @Provides
    FirebaseUser provideUser() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }


}
