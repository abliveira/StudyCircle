package com.abliveira.studycircle.repository;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.abliveira.studycircle.manager.UserManager;
import com.abliveira.studycircle.model.Message;

import java.util.UUID;

public final class ChatRepository {

    private UserManager userManager;
    private static final String CHAT_COLLECTION = "chats";
    private static final String MESSAGE_COLLECTION = "messages";
    private static volatile ChatRepository instance;

    private ChatRepository() {
        this.userManager = UserManager.getInstance();
    }

    public static ChatRepository getInstance() {
        ChatRepository result = instance;
        if (result != null) {
            return result;
        }
        synchronized(ChatRepository.class) {
            if (instance == null) {
                instance = new ChatRepository();
            }
            return instance;
        }
    }

    public CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(CHAT_COLLECTION);
    }

    public Query getChatMessages(String chat){
        return this.getChatCollection()
                .document(chat)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(100);
    }

    public void createMessage(String textMessage, String chat){

        userManager.getUserData().addOnSuccessListener(user -> {
            Message message = new Message(textMessage, user);

            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);
        });
    }

    public void createMessageWithImage(String urlImage, String textMessage, String chat){
        userManager.getUserData().addOnSuccessListener(user -> {
            Message message = new Message(textMessage, urlImage, user);

            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);

        });
    }

    public UploadTask uploadImage(Uri imageUri, String chat){
        String uuid = UUID.randomUUID().toString(); // GENERATE UNIQUE STRING
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(chat + "/" + uuid);
        return mImageRef.putFile(imageUri);
    }
}