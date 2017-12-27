package com.offsidegame.offside.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.PlayerActivity;

import org.acra.ACRA;

import java.util.ArrayList;

/**
 * Created by user on 12/26/2017.
 */

public class PlayerActivityAdapter extends ArrayAdapter<PlayerActivity> {



    public PlayerActivityAdapter(Context context, ArrayList<PlayerActivity> playerActivities) {
        super(context, 0, playerActivities);
    }


    private class ViewHolder {
        public PlayerActivity playerActivity;
        public TextView questionTextView;

        public TextView answerTextView;
        public TextView betSizeTextView;
        public TextView potentialEarnTextView;
//        public ImageView coinsImageView;
//        public ImageView powerItemsImageView;

    }

    @Override
    public View getView(int position, View convertView,ViewGroup parent) {

        try {
            PlayerActivityAdapter.ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.player_activity_item, parent, false);
                viewHolder = new PlayerActivityAdapter.ViewHolder();

                viewHolder.questionTextView =  convertView.findViewById(R.id.pai_question_text_view);
                viewHolder.answerTextView =  convertView.findViewById(R.id.pai_answer_text_view);
                viewHolder.betSizeTextView = convertView.findViewById(R.id.pai_bet_size_text_view);
                viewHolder.potentialEarnTextView =  convertView.findViewById(R.id.pai_potential_earn_text_view);
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (PlayerActivityAdapter.ViewHolder) convertView.getTag();
            }

            viewHolder.playerActivity = getItem(position);
//            int lightBackGround = R.drawable.shape_bg_league_record_even;
//            int darkBackground = R.drawable.shape_bg_league_record_odd;
//
//            viewHolder.leagueRecordRoot.setBackgroundResource(position%2 == 0? lightBackGround : darkBackground);
//            viewHolder.leagueRecordPositionTextView.setText(String.format("%d",viewHolder.leagueRecord.getPosition()));

            viewHolder.questionTextView.setText(viewHolder.playerActivity.getQuestionText());
            viewHolder.answerTextView.setText(viewHolder.playerActivity.getAnswer().getAnswerText());
            int betSize = viewHolder.playerActivity.getAnswerIdentifier().getBetSize();
            viewHolder.betSizeTextView.setText(String.format("%d",betSize));
            double multiplier = viewHolder.playerActivity.getAnswer().getPointsMultiplier();
            viewHolder.potentialEarnTextView.setText(String.format("%d",betSize*multiplier));


            return convertView;
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


        return null;

    }
}
