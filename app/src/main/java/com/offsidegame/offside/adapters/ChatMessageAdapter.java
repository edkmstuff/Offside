package com.offsidegame.offside.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.ChatMessage;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {

    private Context context;
    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages) {
        super(context, 0, chatMessages);
        this.context = context;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage chatMessage = getItem(position);

            if(chatMessage.isIncoming()) {
                if(chatMessage.getMessageType()=="TEXT"){
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_list_item, parent, false);

                    ImageView profilePictureImageView  = (ImageView) convertView.findViewById(R.id.cm_profile_picture_image_view);
                    TextView messageTextView = (TextView) convertView.findViewById(R.id.cm_message_text_view);

                    Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());

                    loadFbImage(profilePictureImageView, profilePictureUri);

                    messageTextView.setText(chatMessage.getMessageText());


                }
                else if(chatMessage.getMessageType()=="ASKED_QUESTION") {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
                }

                else if(chatMessage.getMessageType()=="PROCESSED_QUESTION") {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
                }

                else if(chatMessage.getMessageType()=="CLOSED_QUESTION") {
                    convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
                }


            }
            else {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_rtl_list_item, parent, false);

                ImageView profilePictureImageView  = (ImageView) convertView.findViewById(R.id.cm_profile_picture_image_view);
                TextView messageTextView = (TextView) convertView.findViewById(R.id.cm_message_text_view);

                Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());

                loadFbImage(profilePictureImageView, profilePictureUri);

                messageTextView.setText(chatMessage.getMessageText());

            }


        return convertView;

    }

    private void loadFbImage(final ImageView fbProfilePicture, Uri fbImageUri) {
        Picasso.with(context).load(fbImageUri).into(fbProfilePicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) fbProfilePicture.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                fbProfilePicture.setImageDrawable(roundedImage);
                //fbProfilePicture.animate().alpha(1.1f).setDuration(200).start();
            }

            @Override
            public void onError() {

            }
        });
    }


}
