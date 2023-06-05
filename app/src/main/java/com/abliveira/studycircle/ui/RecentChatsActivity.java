package com.abliveira.studycircle.ui;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.abliveira.studycircle.R;
import com.abliveira.studycircle.databinding.ActivityRecentChatsBinding;
import com.abliveira.studycircle.manager.ChatManager;
import com.abliveira.studycircle.manager.UserManager;
import com.abliveira.studycircle.model.Message;
import com.abliveira.studycircle.ui.chat.ChatAdapter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

public class RecentChatsActivity extends BaseActivity<ActivityRecentChatsBinding> implements ChatAdapter.Listener {

    private ChatAdapter chatAdapter;
    private String currentChatName;

    private static final String PERMS = Manifest.permission.READ_EXTERNAL_STORAGE;
    private static final int RC_IMAGE_PERMS = 100;
    private static final int RC_CHOOSE_PHOTO = 200;

    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    private static final String CHAT_NAME_1 = "Chat 1";
    private static final String CHAT_NAME_2 = "Chat 2";
    private static final String CHAT_NAME_3 = "Chat 3";

    private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();

    private Uri uriImageSelected;

    @Override
    protected ActivityRecentChatsBinding getViewBinding() {
        return ActivityRecentChatsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureRecentChats();
        configureRecyclerView(CHAT_NAME_ANDROID);
        setupListeners();
    }

//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }

    private void setupListeners(){

        // Chat buttons
//        binding.androidChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_ANDROID); });
//        binding.firebaseChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_FIREBASE); });
//        binding.bugChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_BUG); });
        binding.chatPreview1.setOnClickListener(view -> { startChatActivity(CHAT_NAME_ANDROID); });
        binding.chatPreview2.setOnClickListener(view -> { startChatActivity(CHAT_NAME_FIREBASE); });
        binding.chatPreview3.setOnClickListener(view -> { startChatActivity(CHAT_NAME_BUG); });


        // Tab Buttons
//        binding.groupChatsButton.setOnClickListener(view -> {
//                startChatActivity();
//        });

//        binding.groupChatsButton.setOnClickListener(view -> {
//            startChatActivity();
//        });

        binding.profileButton.setOnClickListener(view -> {
            startProfileActivity();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        this.handleResponse(requestCode, resultCode, data);
    }

//    @AfterPermissionGranted(RC_IMAGE_PERMS)
//    private void addFile(){
//        if (!EasyPermissions.hasPermissions(this, PERMS)) {
//            EasyPermissions.requestPermissions(this, getString(R.string.popup_title_permission_files_access), RC_IMAGE_PERMS, PERMS);
//            return;
//        }
//        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//        startActivityForResult(i, RC_CHOOSE_PHOTO);
//    }

    // Handle activity response (after user has chosen or not a picture)
    private void handleResponse(int requestCode, int resultCode, Intent data){
//        if (requestCode == RC_CHOOSE_PHOTO) {
//            if (resultCode == RESULT_OK) { //SUCCESS
//                this.uriImageSelected = data.getData();
//                Glide.with(this) //SHOWING PREVIEW OF IMAGE
//                        .load(this.uriImageSelected)
//                        .apply(RequestOptions.circleCropTransform())
//                        .into(binding.imagePreview);
//            } else {
//                Toast.makeText(this, getString(R.string.toast_title_no_image_chosen), Toast.LENGTH_SHORT).show();
//            }
//        }
    }

    private void configureRecentChats(){
        TextView chatNamePreview1TextView = (TextView)findViewById(R.id.chatNamePreview1);
        chatNamePreview1TextView.setText(CHAT_NAME_1);
        TextView chatNamePreview2TextView = (TextView)findViewById(R.id.chatNamePreview2);
        chatNamePreview2TextView.setText(CHAT_NAME_2);
        TextView chatNamePreview3TextView = (TextView)findViewById(R.id.chatNamePreview3);
        chatNamePreview3TextView.setText(CHAT_NAME_3);
    }

    // Configure RecyclerView
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(
                generateOptionsForAdapter(chatManager.getChatMessages(this.currentChatName)),
                Glide.with(this), this);

        chatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatRecyclerView.smoothScrollToPosition(chatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(this.chatAdapter);
    }

//    private void sendMessage(){
//        // Check if user can send a message (Text not null + user logged)
//        boolean canSendMessage = !TextUtils.isEmpty(binding.chatEditText.getText()) && userManager.isCurrentUserLogged();
//
//        if (canSendMessage){
//            String messageText = binding.chatEditText.getText().toString();
//            // Check if there is an image to add with the message
//            if(binding.imagePreview.getDrawable() == null){
//                // Create a new message for the chat
//                chatManager.createMessage(messageText, this.currentChatName);
//            }else {
//                // Create a new message with an image for the chat
//                chatManager.sendMessageWithImage(messageText, this.uriImageSelected, this.currentChatName);
//                binding.imagePreview.setImageDrawable(null);
//            }
//            // Reset text field
//            binding.chatEditText.setText("");
//
//        }
//    }

    // Create options for RecyclerView from a Query
    private FirestoreRecyclerOptions<Message> generateOptionsForAdapter(Query query){
        return new FirestoreRecyclerOptions.Builder<Message>()
                .setQuery(query, Message.class)
                .setLifecycleOwner(this)
                .build();
    }

    @Override
    public void onDataChanged() {
        // Show TextView in case RecyclerView is empty
        binding.emptyRecyclerView.setVisibility(this.chatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }

    // Launch Chat Activity
    private void startChatActivity(String chatId){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("CHAT_ID", chatId);
        startActivity(intent);
    }

    // Launch Chat Activity
    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // TODO Disable transition. Implement it right for all screens
    }
}
