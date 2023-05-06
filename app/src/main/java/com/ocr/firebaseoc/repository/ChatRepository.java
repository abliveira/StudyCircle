package com.ocr.firebaseoc.repository;

/*
In this class, we created a method ( getChatCollection() ) allowing us to
 create a reference of the root Collection, "chats."

We have created a method ( getAllMessageForChat()) allowing us, from the "chats"
 root  Collection, to retrieve the Document specified as a parameter (in this
  case "Android", "Firebase" or "Bug"), then the Sub-Collection, "messages",
   to retrieve the list of messages from these chats.

We also ordered orderBy("dateCreated") (our query by creation date to retrieve
 the most recent posts first). Finally, we limit to 50 the maximum number of
 messages ( limit(50) ) that we wish to recover.

For the query  getAllMessageForChat() , you'll also notice that we're not
returning the reference (DocumentReference or CollectionReference), but an
object of the type Query! This is because the object Query corresponds to
the result of the query using comparison operators likewhereEqualTo()
,whereLessThan()  ,orderBy() , or   limit()  .
 */

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.ocr.firebaseoc.manager.UserManager;
import com.ocr.firebaseoc.model.Message;

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

    public Query getAllMessageForChat(String chat){
        return this.getChatCollection()
                .document(chat)
                .collection(MESSAGE_COLLECTION)
                .orderBy("dateCreated")
                .limit(50);
    }

    public void createMessageForChat(String textMessage, String chat){

        userManager.getUserData().addOnSuccessListener(user -> {
            // Create the Message object
            Message message = new Message(textMessage, user);

            // Store Message to Firestore
            this.getChatCollection()
                    .document(chat)
                    .collection(MESSAGE_COLLECTION)
                    .add(message);
        });
    }
}
