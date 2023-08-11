package com.abliveira.studycircle.repository;

import static com.google.firebase.firestore.Query.Direction.DESCENDING;

import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.abliveira.studycircle.manager.RecentChatsManager;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RecentChatsRepository {

    public static final String ACTION_CHAT_PREVIEW_SET = "com.abliveira.studycircle.CHAT_PREVIEW_SET";
    private static final String CHAT_COLLECTION = "chats";
    private static final String MESSAGE_COLLECTION = "messages";

    public void getLastChatMessage(String chatId) {

        System.out.println("RecentChatsRepository getLastChatMessage: " + chatId);

        Query query = getLastChatMessageQuery(chatId, CHAT_COLLECTION, MESSAGE_COLLECTION);

        query.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {

            @Override
            public void onSuccess(QuerySnapshot querySnapshot) {

                if (!querySnapshot.isEmpty()) {

                    DocumentSnapshot document = querySnapshot.getDocuments().get(0);

                    String message = document.getString("message");
                    String sender = document.getString("sender");

                    System.out.print("ChatID: " + chatId + ", ");
                    System.out.print("Message: " + message + ", ");
                    System.out.println("Sender: " + sender);

                    String regex = "\\d+";

                    Pattern pattern = Pattern.compile(regex);
                    Matcher matcher = pattern.matcher(chatId);

                    int chatNumber = 0;

                    if (matcher.find()) {
                        String numberStr = matcher.group();
                            chatNumber = Integer.parseInt(numberStr);
                    }

                    RecentChatsManager chatManager = RecentChatsManager.getInstance();
                    chatManager.setChatPreview(chatNumber, message);

                    Intent intent = new Intent(ACTION_CHAT_PREVIEW_SET);
                    Context context = null;
                    LocalBroadcastManager.getInstance(context).sendBroadcast(intent);

                } else {
                    System.out.println("RecentChatsRepository: Document not found.");
                }
            }
        });
    }

    public CollectionReference getChatCollection(String chatCollection){
        return FirebaseFirestore.getInstance().collection(chatCollection);
    }

    private Query getLastChatMessageQuery(String chatId, String chatCollection, String messageCollection) {

        System.out.println("RecentChatsRepository: getLastChatMessageQuery: " + chatId);

        return this.getChatCollection(chatCollection)
                .document(chatId)
                .collection(messageCollection)
                .orderBy("dateCreated", DESCENDING)
                .limit(1);

    }
}