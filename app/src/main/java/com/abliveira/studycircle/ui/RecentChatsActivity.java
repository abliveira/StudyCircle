package com.abliveira.studycircle.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.abliveira.studycircle.R;
import com.abliveira.studycircle.databinding.ActivityRecentChatsBinding;
import com.abliveira.studycircle.manager.RecentChatsManager;
import com.abliveira.studycircle.repository.RecentChatsRepository;

public class RecentChatsActivity extends BaseActivity<ActivityRecentChatsBinding> {

    private static final String CHAT_NAME_1 = "chat1";
    private static final String CHAT_NAME_2 = "chat2";
    private static final String CHAT_NAME_3 = "chat3";

    @Override
    protected ActivityRecentChatsBinding getViewBinding() {
        System.out.println("ActivityRecentChatsBinding begin");
        return ActivityRecentChatsBinding.inflate(getLayoutInflater());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("OnCreate");
        getSupportActionBar().setElevation(0);
        LocalBroadcastManager.getInstance(this).registerReceiver(chatPreviewSetReceiver, new IntentFilter(RecentChatsRepository.ACTION_CHAT_PREVIEW_SET));
        setupListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.out.println("onResume");
        getMessagePreviews();
        configureRecentChats();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity(); // or finish();
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(chatPreviewSetReceiver);
        super.onDestroy();
    }

    private BroadcastReceiver chatPreviewSetReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(RecentChatsRepository.ACTION_CHAT_PREVIEW_SET)) {
                System.out.println("RecentChatsActivity: Broadcast received from RecentChatsRepository");
                configureRecentChats();
            }
        }
    };

    private void setupListeners(){

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

    private void getMessagePreviews(){

        RecentChatsRepository recentChatsRepository = new RecentChatsRepository();

        recentChatsRepository.getLastChatMessage(CHAT_NAME_1);
        recentChatsRepository.getLastChatMessage(CHAT_NAME_2);
        recentChatsRepository.getLastChatMessage(CHAT_NAME_3);
    }

    private void configureRecentChats() {

        System.out.println("RecentChatsActivity: configureRecentChats");

        RecentChatsManager recentChatsManager = RecentChatsManager.getInstance();

        int[] chatPreviewIds = {
                R.id.chatMessagePreview1,
                R.id.chatMessagePreview2,
                R.id.chatMessagePreview3
        };

        int[] chatNameIds = {
                R.id.chatNamePreview1,
                R.id.chatNamePreview2,
                R.id.chatNamePreview3
        };

        String[] chatTitles = {
                getString(R.string.chat_title_1_recent_chats_activity),
                getString(R.string.chat_title_2_recent_chats_activity),
                getString(R.string.chat_title_3_recent_chats_activity)
        };

        for (int i = 0; i < chatPreviewIds.length; i++) {

            TextView chatPreviewTextView = findViewById(chatPreviewIds[i]);
            TextView chatNameTextView = findViewById(chatNameIds[i]);
            chatNameTextView.setText(chatTitles[i]);
            String selectedPreview = recentChatsManager.getChatPreview(i + 1);
            if (selectedPreview != null) {
                chatPreviewTextView.setText(selectedPreview);
                System.out.println(selectedPreview);
            } else {
                System.out.println("Invalid index or no chat preview found.");
            }
        }
        ImageView contactImage1 = findViewById(R.id.contactImageView1);
        contactImage1.setImageResource(R.mipmap.ic_sample_circuits);

        ImageView contactImage2 = findViewById(R.id.contactImageView2);
        contactImage2.setImageResource(R.mipmap.ic_sample_book);

        ImageView contactImage3 = findViewById(R.id.contactImageView3);
        contactImage3.setImageResource(R.mipmap.ic_sample_engineering);

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