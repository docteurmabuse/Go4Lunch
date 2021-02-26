package com.tizzone.go4lunch.repositories;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.Query;

import javax.inject.Inject;

import static com.google.firebase.firestore.Query.Direction.ASCENDING;

public class UserRepository {

    private final CollectionReference usersRef;

    @Inject
    public UserRepository(Query queryUsersByName, CollectionReference usersRef) {
        this.usersRef = usersRef;
    }

    public Query getQueryUsersByName() {
        return usersRef.orderBy("userName", ASCENDING);
    }


}
