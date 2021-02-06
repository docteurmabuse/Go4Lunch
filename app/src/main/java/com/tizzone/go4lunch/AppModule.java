package com.tizzone.go4lunch;

import android.app.Application;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.tizzone.go4lunch.repositories.UserRepository;
import com.tizzone.go4lunch.utils.FirebaseDataSource;
import com.tizzone.go4lunch.viewmodels.UserViewModel;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class AppModule {
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

    @Provides
    static FirebaseDataSource provideFirebaseDataSource(FirebaseFirestore firebaseFirestore) {
        return new FirebaseDataSource(firebaseFirestore);
    }

    @Provides
    static UserViewModel provideViewModel(UserRepository userRepository) {
        return new UserViewModel(userRepository);
    }
}
