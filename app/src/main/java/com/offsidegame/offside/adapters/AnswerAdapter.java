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

import com.facebook.Profile;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.events.QuestionEvent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class AnswerAdapter extends ArrayAdapter<Answer> {
    private static final String[] greenPalette = new String[]{"#2E7D32", "#2E7D32", "#A5D6A7", "#E8F5E9"};

    private String questionState;
    private Context context;
    private int answersCount;

    public AnswerAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    public AnswerAdapter(Context context, ArrayList<Answer> answers, String questionState) {
        super(context, 0, answers);
        this.context = context;
        this.questionState = questionState;
        this.answersCount = answers.size();
    }


    private class ViewHolder {
        public ImageView rightWrongAnswerIndicator;
        public ImageView fbPicture;
        //public TextView answerNumber;
        public TextView answerText;
        public TextView percentUsersAnswered;
        public TextView score;
        public LinearLayout answerListItem;
        public TextView answeredByText;
        public TextView percentSignText;
        public TextView youCanEarnText;
        public TextView dividerText;
        public LinearLayout backgroundRoot;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Answer answer = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.rightWrongAnswerIndicator = (ImageView) convertView.findViewById(R.id.right_wrong_answer_indicator);
            viewHolder.fbPicture = (ImageView) convertView.findViewById(R.id.fb_picture);
            //viewHolder.answerNumber = (TextView) convertView.findViewById(R.id.answer_number);
            viewHolder.answerText = (TextView) convertView.findViewById(R.id.answer_text);
            viewHolder.percentUsersAnswered = (TextView) convertView.findViewById(R.id.percent_users_answered);
            viewHolder.score = (TextView) convertView.findViewById(R.id.score);
            viewHolder.answerListItem = (LinearLayout) convertView.findViewById(R.id.answer_list_item);
            viewHolder.answeredByText = (TextView) convertView.findViewById(R.id.answered_by_text);
            viewHolder.percentSignText = (TextView) convertView.findViewById(R.id.percent_sign_text);
            viewHolder.youCanEarnText = (TextView) convertView.findViewById(R.id.you_can_earn_text);
            viewHolder.dividerText = (TextView) convertView.findViewById(R.id.divider_text);
            viewHolder.backgroundRoot = (LinearLayout) convertView.findViewById(R.id.background_root);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //String answerNumber = Integer.toString(position + 1);


        viewHolder.rightWrongAnswerIndicator.setVisibility(View.GONE);
        viewHolder.fbPicture.setVisibility(View.GONE);
        viewHolder.percentUsersAnswered.setVisibility(View.GONE);
        viewHolder.score.setVisibility(View.GONE);
        viewHolder.answeredByText.setVisibility(View.GONE);
        viewHolder.percentSignText.setVisibility(View.GONE);
        viewHolder.youCanEarnText.setVisibility(View.GONE);
        viewHolder.dividerText.setVisibility(View.GONE);

        final ImageView fbProfilePicture  = viewHolder.fbPicture;

        if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {
            viewHolder.fbPicture.setAlpha(0.0f);
            viewHolder.fbPicture.setVisibility(View.VISIBLE);
            if (answer.getIsTheAnswerOfTheUser()) {
                loadFbImage(fbProfilePicture);
            }
            viewHolder.percentUsersAnswered.setVisibility(View.VISIBLE);
            viewHolder.score.setVisibility(View.VISIBLE);
            viewHolder.answeredByText.setVisibility(View.VISIBLE);
            viewHolder.percentSignText.setVisibility(View.VISIBLE);
            viewHolder.youCanEarnText.setVisibility(View.VISIBLE);
            viewHolder.dividerText.setVisibility(View.VISIBLE);

        } else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {
            viewHolder.fbPicture.setVisibility(View.VISIBLE);
            if (answer.getIsTheAnswerOfTheUser()) {
                loadFbImage(fbProfilePicture);
            }

            viewHolder.rightWrongAnswerIndicator.setVisibility(View.VISIBLE);
            if (answer.getIsCorrect())
                viewHolder.rightWrongAnswerIndicator.setImageResource(R.drawable.ic_done_black_24dp);
            else
                viewHolder.rightWrongAnswerIndicator.setImageResource(R.drawable.ic_clear_red_24dp);


            viewHolder.score.setVisibility(View.VISIBLE);
            viewHolder.youCanEarnText.setVisibility(View.VISIBLE);

            //viewHolder.dividerText.setVisibility(View.VISIBLE);

        }

        //viewHolder.answerNumber.setText(answerNumber.toString() + ".");
        viewHolder.answerText.setText(answer.getAnswerText());

        //viewHolder.percentUsersAnswered.setText(context.getString(R.string.lbl_answered_by) + " " + Long.toString(percentUsersAnswered) + "%");
        viewHolder.percentUsersAnswered.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())));
        viewHolder.score.setText(Long.toString(Math.round(answer.getScore())));
        //viewHolder.isTheAnswerOfTheUser.setText(Boolean.toString(answer.getIsTheAnswerOfTheUser()));
        //viewHolder.isCorrect.setText(Boolean.toString(answer.getIsCorrect()));


//        Height
        int listViewHeight =(int) Math.floor(parent.getHeight() * 1);
        int itemHeight = (int) Math.floor(listViewHeight / answersCount);

        convertView.setMinimumHeight(itemHeight);

        int listViewWidth = parent.getWidth();

//        ViewGroup.LayoutParams params = viewHolder.backgroundRoot.getLayoutParams();
//        params.height = (int) Math.floor(itemHeight * 0.95);
//        params.width = (int) Math.floor(listViewWidth * 0.95);
//
//        viewHolder.backgroundRoot.setLayoutParams(params);


//        background
        // String backgroundColor = greenPalette[position%2];
        // viewHolder.backgroundRoot.setBackgroundColor(Color.parseColor(backgroundColor));
        //convertView.


        return convertView;

    }

    private void loadFbImage(final ImageView fbProfilePicture) {
        Uri fbImageUrl = Profile.getCurrentProfile().getProfilePictureUri(100,100);
        //fbProfilePicture.setImageURI(fbImageUrl);

        Picasso.with(context).load(fbImageUrl).into(fbProfilePicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) fbProfilePicture.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                fbProfilePicture.setImageDrawable(roundedImage);
                fbProfilePicture.animate().alpha(1.1f).setDuration(1000).start();
            }

            @Override
            public void onError() {

            }
        });
    }


}
