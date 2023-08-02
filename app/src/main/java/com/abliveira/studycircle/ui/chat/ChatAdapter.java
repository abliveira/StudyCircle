package com.abliveira.studycircle.ui.chat;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.abliveira.studycircle.R;
import com.abliveira.studycircle.manager.UserManager;
import com.abliveira.studycircle.model.Message;

/*
We created an adapter that looks fairly simple at first. However, it inherits
 an object available in the FirebaseUI library from FirestoreRecyclerAdapter.
But why not have the adapter inherit from RecyclerView.Adapter as usual?
Because FirestoreRecyclerAdapter makes it possible to manage the real-time updating
 of RecyclerView automatically to reflect a Firestore database (and more particularly
  the result of a Query) accurately, or caching of data so that you can access it even
   when offline! I'll tell you about it in a moment. ;)

For example, if a chat message is added to Firestore, a new line will automatically
 appear in the RecyclerView. Likewise, if a message is deleted, the corresponding
 line will disappear from the RecyclerView.

It's important that you know that Firebase's SDK allows you to create Listeners to
 Firestore data. If data is modified in the database, it will also be modified in
 real-time in your Android app.
 */

public class ChatAdapter extends FirestoreRecyclerAdapter<Message, MessageViewHolder> {

    public interface Listener {
        void onDataChanged();
    }

    // VIEW TYPES
    private static final int SENDER_TYPE = 1;
    private static final int RECEIVER_TYPE = 2;

    private final RequestManager glide;

    private Listener callback;

    public ChatAdapter(@NonNull FirestoreRecyclerOptions<Message> options, RequestManager glide, Listener callback) {
        super(options);
        this.glide = glide;
        this.callback = callback;
    }

    @Override
    public int getItemViewType(int position) {
        // Determine the type of the message by if the user is the sender or not
        String currentUserId = UserManager.getInstance().getCurrentUser().getUid();
        boolean isSender = getItem(position).getUserSender().getUid().equals(currentUserId);

        return (isSender) ? SENDER_TYPE : RECEIVER_TYPE;
    }

    @Override
    protected void onBindViewHolder(@NonNull MessageViewHolder holder, int position, @NonNull Message model) {
        holder.itemView.invalidate();
        holder.updateWithMessage(model, this.glide);
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MessageViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false), viewType == 1);
    }

    @Override
    public void onDataChanged() {
        super.onDataChanged();
        this.callback.onDataChanged();
    }
}