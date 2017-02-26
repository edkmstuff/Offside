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




    private class ViewHolder {

        public ImageView fbPicture;
        public TextView messageText;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage chatMessage = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            if(chatMessage.isIncoming())
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_list_item, parent, false);
            else
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_rtl_list_item, parent, false);

            viewHolder = new ViewHolder();

            viewHolder.fbPicture = (ImageView) convertView.findViewById(R.id.cm_fb_picture);
            viewHolder.messageText = (TextView) convertView.findViewById(R.id.cm_message_text);



            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();


        }


        Uri fbImageUri = Uri.parse(chatMessage.getImageUrl());
        final ImageView fbProfilePicture  = viewHolder.fbPicture;
        loadFbImage(fbProfilePicture, fbImageUri);

        viewHolder.messageText.setText(chatMessage.getMessageText());



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
