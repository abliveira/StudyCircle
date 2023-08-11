package com.abliveira.studycircle.manager;

import android.net.Uri;

import com.google.firebase.firestore.Query;
import com.abliveira.studycircle.repository.ChatRepository;

public class ChatManager {

    private static volatile ChatManager instance;
    private ChatRepository chatRepository;

    private ChatManager() {
        chatRepository = ChatRepository.getInstance();
    }

    public static ChatManager getInstance() {
        ChatManager result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatManager.class) {
            if (instance == null) {
                instance = new ChatManager();
            }
            return instance;
        }
    }

    public Query getChatMessages(String chat){
        return chatRepository.getChatMessages(chat);
    }

    public void createMessage(String message, String chat){
        chatRepository.createMessage(message, chat);
    }

    public void sendMessageWithImage(String message, Uri imageUri, String chat){
        chatRepository.uploadImage(imageUri, chat).addOnSuccessListener(taskSnapshot -> {
            taskSnapshot.getStorage().getDownloadUrl().addOnSuccessListener(uri -> {
                chatRepository.createMessageWithImage(uri.toString(), message, chat);
            });
        });
    }
}