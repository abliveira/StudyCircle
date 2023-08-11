package com.abliveira.studycircle.ui;

import androidx.annotation.Nullable;

import android.content.Intent;
import android.os.Bundle;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.material.snackbar.Snackbar;
import com.abliveira.studycircle.R;
import com.abliveira.studycircle.databinding.ActivityMainBinding;
import com.abliveira.studycircle.manager.UserManager;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends BaseActivity<ActivityMainBinding> {

    private static final int RC_SIGN_IN = 123;

    private UserManager userManager = UserManager.getInstance();

    @Override
    ActivityMainBinding getViewBinding() {
        return ActivityMainBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(userManager.isCurrentUserLogged()){
            startRecentChatsActivity();}
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponseAfterSignIn(requestCode, resultCode, data);
    }

    private void handleResponseAfterSignIn(int requestCode, int resultCode, Intent data){

        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                userManager.createUser();
                showSnackBar(getString(R.string.connection_succeed));
                startRecentChatsActivity();
            } else {
                if (response == null) {
                    showSnackBar(getString(R.string.error_authentication_canceled));
                } else if (response.getError()!= null) {
                    if(response.getError().getErrorCode() == ErrorCodes.NO_NETWORK){
                        showSnackBar(getString(R.string.error_no_internet));
                    } else if (response.getError().getErrorCode() == ErrorCodes.UNKNOWN_ERROR) {
                        showSnackBar(getString(R.string.error_unknown_error));
                    }
                }
            }
        }
    }

    private void showSnackBar( String message){
        Snackbar.make(binding.mainLayout, message, Snackbar.LENGTH_SHORT).show();
    }
    private void setupListeners(){
        binding.loginButton.setOnClickListener(view -> {
                startSignInActivity();
        });
    }

    private void startSignInActivity(){

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setTheme(R.style.LoginTheme)
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false, false)
                        .setLogo(R.drawable.ic_logo_margin)
                        .setLockOrientation(true)
                        .build(),
                RC_SIGN_IN);
    }

    private void startRecentChatsActivity(){
        Intent intent = new Intent(this, RecentChatsActivity.class);
        startActivity(intent);
    }
}