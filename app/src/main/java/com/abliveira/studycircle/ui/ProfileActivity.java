package com.abliveira.studycircle.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.abliveira.studycircle.R;
import com.abliveira.studycircle.databinding.ActivityProfileBinding;
import com.abliveira.studycircle.manager.UserManager;
import com.google.firebase.auth.UserProfileChangeRequest;

public class ProfileActivity extends BaseActivity<ActivityProfileBinding> {

    private UserManager userManager = UserManager.getInstance();
    private boolean currentUserType;

    @Override
    ActivityProfileBinding getViewBinding() {
        return ActivityProfileBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        setupListeners();
        updateUIWithUserData();
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0, 0);
    }

    /* These methods use the Singleton object  AuthUI.getInstance() from the FirebaseUI library.
    The latter has several methods, including  signOut() and  delete(), which both return an object
    of type Task, which allows these calls to be made asynchronously.

    These Tasks have several Callback methods to check if and when they end correctly. */

    private void setupListeners(){

        binding.groupChatsButton.setOnClickListener(view -> {
            finish();
            overridePendingTransition(0, 0);
        });

        binding.userTypeRequestButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.userType_dialog_profile_activity)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) -> {
                            userManager.updateUserType(!currentUserType);
                            getUserData();
                        }
                    )
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();
        });

        binding.updateButton.setOnClickListener(view -> {
            binding.progressBar.setVisibility(View.VISIBLE);
            userManager.updateUsername(binding.usernameEditText.getText().toString())
                    .addOnSuccessListener(aVoid -> {
                        binding.progressBar.setVisibility(View.INVISIBLE);
                    });
        });

        binding.signOutButton.setOnClickListener(view -> {
            userManager.signOut(this).addOnSuccessListener(aVoid -> {
//                finish(); // Finish the activity
//                finishAffinity(); // Finish all activities (Close application)
                restartApp(); // Restart the application
            });
        });

        binding.deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                            userManager.deleteUser(ProfileActivity.this)
                                    .addOnSuccessListener(aVoid -> {
//                                                finish(); // Finish the activity
//                                                finishAffinity(); // Finish all activities (Close application)
                                                restartApp(); // Restart the application
                                            }
                                    )
                    )
                    .setNegativeButton(R.string.popup_message_choice_no, null)
                    .show();
        });
    }

    private void updateUIWithUserData(){
        if(userManager.isCurrentUserLogged()){
            FirebaseUser user = userManager.getCurrentUser();
            System.out.println("updateUIWithUserData isCurrentUserLogged");
            if(user.getPhotoUrl() != null){
                System.out.println("updateUIWithUserData getPhotoUrl() != null");
                setProfilePicture(user.getPhotoUrl());
                System.out.println(user.getPhotoUrl());
            }
//            setProfilePictureHardcoded();
            setTextUserData(user);
            getUserData();
        }
    }

    private void getUserData(){
        userManager.getUserData().addOnSuccessListener(user -> {
            // Set the data with the user information
            String username = TextUtils.isEmpty(user.getUsername()) ? getString(R.string.info_no_username_found) : user.getUsername();
            binding.usernameEditText.setText(username);

            currentUserType = user.getUserType();
            String userType = currentUserType ? getString(R.string.userType_professor_profile_activity) : getString(R.string.userType_student_profile_activity);

            binding.userTypeTextBox.setText(userType);
        });
    }

    private void setProfilePicture(Uri profilePictureUrl){
        Glide.with(this)
                .load(profilePictureUrl)
                .apply(RequestOptions.circleCropTransform())
                .into(binding.profileImageView);
    }

    private void setProfilePictureHardcoded(){

        /* This code is used for setting the current user profile picture,
        registered on Firebase Authentification.
        TODO Change this code for allow the user changing his own picture on app
         */

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse("INSERT FIREBASE PIC URL HERE"))
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            System.out.println("User profile updated.");
                        }
                    }
                });
    }

    private void setTextUserData(FirebaseUser user){

        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        binding.usernameEditText.setText(username);
        binding.emailTextView.setText(email);
    }

    private void restartApp() {
        Intent restartIntent = getBaseContext().getPackageManager()
                .getLaunchIntentForPackage(getBaseContext().getPackageName());
        if (restartIntent != null) {
            restartIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(restartIntent);
            finish();
        }
    }
}