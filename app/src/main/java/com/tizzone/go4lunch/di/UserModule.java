package com.tizzone.go4lunch.di;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.tizzone.go4lunch.models.User;
import com.tizzone.go4lunch.repositories.UserRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@InstallIn(SingletonComponent.class)
@Module
public class UserModule {

    @Singleton
    @Provides
    static FirestoreRecyclerOptions<User> provideOption(UserRepository userRepository) {
        return userRepository.getUserList();
    }
}
