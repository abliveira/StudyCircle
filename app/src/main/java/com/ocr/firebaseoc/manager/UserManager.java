package com.ocr.firebaseoc.manager;

/* Manager
        The manager will take care of processing and formatting the data, if necessary.
        It will therefore call the Repository to retrieve the data and then process it
         so that it is in a format our Views can use.
        This is one way of doing things among many others, I invite you to go deeper
         into the subject of patterns and the architecture of a mobile application if you wish.

         For simplicity, these classes will be singletons.

 */

import android.content.Context;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.ocr.firebaseoc.repository.UserRepository;

public class UserManager {

    private static volatile UserManager instance;
    private UserRepository userRepository;

    private UserManager() {
        userRepository = UserRepository.getInstance();
    }

    public static UserManager getInstance() {
        UserManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(UserRepository.class) {
            if (instance == null) {
                instance = new UserManager();
            }
            return instance;
        }
    }

    public FirebaseUser getCurrentUser(){
        return userRepository.getCurrentUser();
    }

    public Boolean isCurrentUserLogged(){
        return (this.getCurrentUser() != null);
    }

    public Task<Void> signOut(Context context){
        return userRepository.signOut(context);
    }

    public Task<Void> deleteUser(Context context){
        return userRepository.deleteUser(context);
    }

}