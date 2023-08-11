package com.abliveira.studycircle.ui.chat;

import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.RequestManager;
import com.bumptech.glide.request.RequestOptions;
import com.abliveira.studycircle.databinding.ItemChatBinding;
import com.abliveira.studycircle.model.Message;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
This ViewHolder represents each line in the RecyclerView, and each message (visually).
Go ahead and check out the corresponding layout so you can absorb it and understand
the visual logic. To sum it up, we have the method  updateWithMessage() , which will
update the various views from ViewHolder based on a message object passed as a parameter.
 */

public class MessageViewHolder extends RecyclerView.ViewHolder {

    private ItemChatBinding binding;

    private boolean isSender;

    public MessageViewHolder(@NonNull View itemView, boolean isSender) {
        super(itemView);
        this.isSender = isSender;
        binding = ItemChatBinding.bind(itemView);
    }

    public void updateWithMessage(Message message, RequestManager glide){

        // Update message
        binding.messageTextView.setText(message.getMessage());
        binding.messageTextView.setTextAlignment(isSender ? View.TEXT_ALIGNMENT_TEXT_END : View.TEXT_ALIGNMENT_TEXT_START);

        // Update date
        if (message.getDateCreated() != null) binding.dateTextView.setText(this.convertDateToHour(message.getDateCreated()));

        // Update userType
        binding.profileUserType.setVisibility(message.getUserSender().getUserType() ? View.VISIBLE : View.INVISIBLE);

        // Update profile picture
        if (message.getUserSender().getUrlPicture() != null)
            glide.load(message.getUserSender().getUrlPicture())
                    .apply(RequestOptions.circleCropTransform())
                    .into(binding.profileImage);

        // Update image sent
        if (message.getUrlImage() != null){
            glide.load(message.getUrlImage())
                    .into(binding.senderImageView);
            binding.senderImageView.setVisibility(View.VISIBLE);
        } else {
            binding.senderImageView.setVisibility(View.GONE);
        }

        updateLayoutFromSenderType();
    }

    private void updateLayoutFromSenderType(){

        binding.messageTextContainer.requestLayout();

        if(!isSender){
            updateProfileContainer();
            updateMessageContainer();
        }
    }

    private void updateProfileContainer(){
        // Update the constraint for the profile container (Push it to the left for receiver message)
        ConstraintLayout.LayoutParams profileContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.profileContainer.getLayoutParams();
        profileContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.UNSET;
        profileContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
        binding.profileContainer.requestLayout();
    }

    private void updateMessageContainer(){
        // Update the constraint for the message container (Push it to the right of the profile container for receiver message)
        ConstraintLayout.LayoutParams messageContainerLayoutParams = (ConstraintLayout.LayoutParams) binding.messageContainer.getLayoutParams();
        messageContainerLayoutParams.startToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.endToStart = ConstraintLayout.LayoutParams.UNSET;
        messageContainerLayoutParams.startToEnd = binding.profileContainer.getId();
        messageContainerLayoutParams.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
        messageContainerLayoutParams.horizontalBias = 0.0f;
        binding.messageContainer.requestLayout();

        // Update the constraint (gravity) for the text of the message (content + date) (Align it to the left for receiver message)
        LinearLayout.LayoutParams messageTextLayoutParams = (LinearLayout.LayoutParams) binding.messageTextContainer.getLayoutParams();
        messageTextLayoutParams.gravity = Gravity.START;
        binding.messageTextContainer.requestLayout();

        LinearLayout.LayoutParams dateLayoutParams = (LinearLayout.LayoutParams) binding.dateTextView.getLayoutParams();
        dateLayoutParams.gravity = Gravity.BOTTOM | Gravity.START;
        binding.dateTextView.requestLayout();
    }

    private String convertDateToHour(Date date){
        DateFormat dfTime = new SimpleDateFormat("HH:mm", Locale.getDefault());
        return dfTime.format(date);
    }
}