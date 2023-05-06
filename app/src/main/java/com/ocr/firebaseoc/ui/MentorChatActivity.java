package com.ocr.firebaseoc.ui;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.ocr.firebaseoc.databinding.ActivityMentorChatBinding;
import com.ocr.firebaseoc.manager.ChatManager;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.model.Message;
import com.ocr.firebaseoc.ui.chat.MentorChatAdapter;

public class MentorChatActivity extends BaseActivity<ActivityMentorChatBinding> implements MentorChatAdapter.Listener {

    private MentorChatAdapter mentorChatAdapter;
    private String currentChatName;

    private static final String CHAT_NAME_ANDROID = "android";
    private static final String CHAT_NAME_BUG = "bug";
    private static final String CHAT_NAME_FIREBASE = "firebase";

    private UserManager userManager = UserManager.getInstance();
    private ChatManager chatManager = ChatManager.getInstance();

    @Override
    protected ActivityMentorChatBinding getViewBinding() {
        return ActivityMentorChatBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureRecyclerView(CHAT_NAME_ANDROID);
        setupListeners();
    }

    private void setupListeners(){

        // Chat buttons
        binding.androidChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_ANDROID); });
        binding.firebaseChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_FIREBASE); });
        binding.bugChatButton.setOnClickListener(view -> { this.configureRecyclerView(CHAT_NAME_BUG); });
    }

    // Configure RecyclerView
    private void configureRecyclerView(String chatName){
        //Track current chat name
        this.currentChatName = chatName;
        //Configure Adapter & RecyclerView
        this.mentorChatAdapter = new MentorChatAdapter(
                generateOptionsForAdapter(chatManager.getAllMessageForChat(this.currentChatName)),
                Glide.with(this), this);

        mentorChatAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                binding.chatRecyclerView.smoothScrollToPosition(mentorChatAdapter.getItemCount()); // Scroll to bottom on new messages
            }
        });

        binding.chatRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.chatRecyclerView.setAdapter(this.mentorChatAdapter);
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
        binding.emptyRecyclerView.setVisibility(this.mentorChatAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
    }
}
