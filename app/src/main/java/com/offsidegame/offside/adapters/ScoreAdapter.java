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
import com.offsidegame.offside.models.Score;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class ScoreAdapter extends ArrayAdapter<Score> {

    private Context context;
    public ScoreAdapter(Context context, ArrayList<Score> scores) {
        super(context, 0, scores);
        this.context = context;
    }




    private class ViewHolder {
        public TextView position;
        public ImageView fbPicture;
        public TextView name;
        public TextView points;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Score score = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.score_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.position = (TextView) convertView.findViewById(R.id.position_text);
            viewHolder.fbPicture = (ImageView) convertView.findViewById(R.id.fb_picture);
            viewHolder.name = (TextView) convertView.findViewById(R.id.name);
            viewHolder.points = (TextView) convertView.findViewById(R.id.points_text);


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }


        Uri fbImageUri = Uri.parse(score.getImageUrl());
        final ImageView fbProfilePicture  = viewHolder.fbPicture;
        loadFbImage(fbProfilePicture, fbImageUri);
        viewHolder.position.setText(Integer.toString(score.getPosition()));
        viewHolder.name.setText(score.getName());
        viewHolder.points.setText(Integer.toString(score.getPoints()));





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
