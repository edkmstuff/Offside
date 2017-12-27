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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.LeagueRecord;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class LeagueAdapter extends ArrayAdapter<LeagueRecord> {

    private Context context;
    private String createdByUserId;

    public LeagueAdapter(Context context, ArrayList<LeagueRecord> leagueRecords, String createdByUserId) {
        super(context, 0, leagueRecords);
        this.context = context;
        this.createdByUserId = createdByUserId;
    }

    private class ViewHolder {
        public LeagueRecord leagueRecord;
        public LinearLayout leagueRecordRoot;
        public TextView leagueRecordPositionTextView;
        public ImageView leagueRecordProfilePictureImageView;
        public TextView leagueRecordPlayerNameTextView;
        public TextView leagueRecordPointsTextView;
        public TextView leagueRecordFactorizedPointsTextView;
        public ImageView creatorImageView;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.league_record_item, parent, false);
                viewHolder = new ViewHolder();

                viewHolder.leagueRecordRoot =  convertView.findViewById(R.id.lr_root);
                viewHolder.leagueRecordPositionTextView =  convertView.findViewById(R.id.lr_position_text_view);
                viewHolder.leagueRecordProfilePictureImageView =  convertView.findViewById(R.id.lr_player_image_view);
                viewHolder.leagueRecordPlayerNameTextView = convertView.findViewById(R.id.lr_player_name_text_view);
                viewHolder.leagueRecordPointsTextView =  convertView.findViewById(R.id.lr_points_text_view);
                viewHolder.leagueRecordFactorizedPointsTextView = convertView.findViewById(R.id.lr_factorized_points_text_view);
                viewHolder.creatorImageView = convertView.findViewById(R.id.lr_creator_image_view);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.leagueRecord = getItem(position);
            int lightBackGround = R.drawable.shape_bg_league_record_even;
            int darkBackground = R.drawable.shape_bg_league_record_odd;

            viewHolder.leagueRecordRoot.setBackgroundResource(position%2 == 0? lightBackGround : darkBackground);
            viewHolder.leagueRecordPositionTextView.setText(String.format("%d",viewHolder.leagueRecord.getPosition()));

            String playerName = viewHolder.leagueRecord.getPlayerName();
            viewHolder.leagueRecordPlayerNameTextView.setText(playerName);
            if(viewHolder.leagueRecord.getPlayerId().equals(createdByUserId))
                viewHolder.creatorImageView.setVisibility(View.VISIBLE);
            else
                viewHolder.creatorImageView.setVisibility(View.GONE);

            String points = Formatter.formatNumber(viewHolder.leagueRecord.getPoints(),Formatter.intCommaSeparator);
            viewHolder.leagueRecordPointsTextView.setText(points);
            viewHolder.leagueRecordFactorizedPointsTextView.setText(String.format("%d",viewHolder.leagueRecord.getGamesCount()));

            Uri uri = Uri.parse(viewHolder.leagueRecord.getImageUrl());
            loadFbImage(viewHolder.leagueRecordProfilePictureImageView, uri);


            return convertView;
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


        return null;
    }



    private void loadFbImage(final ImageView fbProfilePicture, Uri fbImageUri) {
        try
        {
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


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

        }

    }





}


