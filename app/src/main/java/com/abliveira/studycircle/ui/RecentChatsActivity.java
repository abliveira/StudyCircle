package com.abliveira.studycircle.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;

public class RecentChatsActivity extends BaseActivity<ActivityRecentChatsBinding> implements ChatAdapter.Listener {

    private ChatAdapter chatAdapter;
    private String currentChatName;

    private static final String CHAT_NAME_1 = "chat1";
    private static final String CHAT_NAME_2 = "chat2";
    private static final String CHAT_NAME_3 = "chat3";

    private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();

    @Override
    protected ActivityRecentChatsBinding getViewBinding() {
        return ActivityRecentChatsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setElevation(0);
        configureRecentChats();
        configureRecyclerView(CHAT_NAME_1);
        setupListeners();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // or finish();
    }

    private void setupListeners(){

        // Chat buttons
        binding.chatPreview1.setOnClickListener(view -> {
            startChatActivity(CHAT_NAME_1, getString(R.string.chat_title_1_recent_chats_activity));
        });
        binding.chatPreview2.setOnClickListener(view -> {
            startChatActivity(CHAT_NAME_2, getString(R.string.chat_title_2_recent_chats_activity));
        });
        binding.chatPreview3.setOnClickListener(view -> {
            startChatActivity(CHAT_NAME_3, getString(R.string.chat_title_3_recent_chats_activity));
        });

        binding.profileButton.setOnClickListener(view -> {
            startProfileActivity();
        });
    }

    private void configureRecentChats(){
        TextView chatNamePreview1TextView = findViewById(R.id.chatNamePreview1);
        chatNamePreview1TextView.setText(getString(R.string.chat_title_1_recent_chats_activity));
        TextView chatNamePreview2TextView = findViewById(R.id.chatNamePreview2);
        chatNamePreview2TextView.setText(getString(R.string.chat_title_2_recent_chats_activity));
        TextView chatNamePreview3TextView = findViewById(R.id.chatNamePreview3);
        chatNamePreview3TextView.setText(getString(R.string.chat_title_3_recent_chats_activity));
    }

    // Configure RecyclerView
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.chatAdapter = new ChatAdapter(
                generateOptionsForAdapter(chatManager.getLastChatMessage(this.currentChatName)),
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

    private void startChatActivity(String chatId, String chatName){
        Intent intent = new Intent(this, ChatActivity.class);
        intent.putExtra("CHAT_ID", chatId);
        intent.putExtra("CHAT_NAME", chatName);
        startActivity(intent);
    }

    private void startProfileActivity(){
        Intent intent = new Intent(this, ProfileActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0); // Disables transition
    }
}