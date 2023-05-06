package com.ocr.firebaseoc.manager;

import com.google.firebase.firestore.Query;
import com.ocr.firebaseoc.repository.ChatRepository;

/*
Nothing special in this class: we have a function getChatCollection().
It uses our repository to retrieve messages.
 */
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

    public void createMessageForChat(String message, String chat){
        chatRepository.createMessageForChat(message, chat);
    }

    public Query getAllMessageForChat(String chat){
        return chatRepository.getAllMessageForChat(chat);
    }

}
