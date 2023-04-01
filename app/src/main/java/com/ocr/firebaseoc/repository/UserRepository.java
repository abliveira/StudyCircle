package com.ocr.firebaseoc.repository;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/*
The FirebaseUI library provides us with a very   FirebaseAuth.getInstance() practical Singleton object,
        containing a method allowing us to retrieve the user currently connected to our application
        (getCurrentUser() ) which returns an object of type FirebaseUser .
 */

public final class UserRepository {

    private static volatile UserRepository instance;

    private UserRepository() { }

    public static UserRepository getInstance() {
        UserRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserRepository();
            }
            return instance;
        }
    }

    @Nullable
    public FirebaseUser getCurrentUser(){
        return FirebaseAuth.getInstance().getCurrentUser();
    }
}
