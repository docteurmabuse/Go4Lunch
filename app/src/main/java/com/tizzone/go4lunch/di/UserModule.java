package com.tizzone.go4lunch.di;

import android.app.Application;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.adapters.UsersListAdapter;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class UserModule {

    @Provides
    static FirestoreRecyclerOptions<User> provideOption(UserRepository userRepository) {
        return userRepository.getUserList();
    }

    @Provides
    static UsersListAdapter provideAdapter(FirestoreRecyclerOptions<User> options, RequestManager requestManager, Application application) {
        return new UsersListAdapter(options, requestManager);
    }


}
