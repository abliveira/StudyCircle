package com.abliveira.studycircle.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseUser;
import com.abliveira.studycircle.R;
import com.abliveira.studycircle.databinding.ActivityProfileBinding;
import com.abliveira.studycircle.manager.UserManager;

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
                finish();
            });
        });

        binding.deleteButton.setOnClickListener(view -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.popup_message_confirmation_delete_account)
                    .setPositiveButton(R.string.popup_message_choice_yes, (dialogInterface, i) ->
                            userManager.deleteUser(ProfileActivity.this)
                                    .addOnSuccessListener(aVoid -> {
                                                finish();
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

            if(user.getPhotoUrl() != null){
                setProfilePicture(user.getPhotoUrl());
            }
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

    private void setTextUserData(FirebaseUser user){

        String email = TextUtils.isEmpty(user.getEmail()) ? getString(R.string.info_no_email_found) : user.getEmail();
        String username = TextUtils.isEmpty(user.getDisplayName()) ? getString(R.string.info_no_username_found) : user.getDisplayName();

        binding.usernameEditText.setText(username);
        binding.emailTextView.setText(email);
    }
}