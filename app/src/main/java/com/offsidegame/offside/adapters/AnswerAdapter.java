package com.offsidegame.offside.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
    private String questionId;

    public AnswerAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    public AnswerAdapter(Context context, ArrayList<Answer> answers, String questionState, String questionId) {
        super(context, 0, answers);
        this.context = context;
        this.questionState = questionState;
        this.answersCount = answers.size();
        this.questionId = questionId;
    }


    private class ViewHolder {
        public LinearLayout answerQuestionRoot;
        public LinearLayout processedQuestionRoot;
        public LinearLayout closedQuestionRoot;

        public ImageView answerQuestionUserAnswerImageView;
        public TextView answerQuestionAnswerTextView;

        public ImageView processedQuestionUserAnswerImageView;

        public TextView processedQuestionAnswerTextView;
        public TextView processedQuestionAnsweredByTextView;
        public TextView processedQuestionPercentUsersAnsweredTextView;
        public TextView processedQuestionYouCanEarnTextView;
        public TextView processedQuestionScoreTextView;
        public TextView processedQuestionPointsTextView;
        public ImageView closedQuestionUserAnswerImageView;
        public ImageView closedQuestionRightWrongAnswerIndicatorImageView;
        public TextView closedQuestionAnswerTextView;
        public TextView closedQuestionAnsweredByTextView;
        public TextView closedQuestionPercentUsersAnsweredTextView;
        //public TextView closedQuestionYouCanEarnTextView;
        public TextView closedQuestionScoreTextView;
        public TextView closedQuestionPointsTextView;

//        public ImageView rightWrongAnswerIndicator;
//        public ImageView fbPicture;
//        //public TextView answerNumber;
//        public TextView answerText;
//        public TextView percentUsersAnswered;
//        public TextView score;
//        public LinearLayout answerListItem;
//        public TextView answeredByText;
//       // public TextView percentSignText;
//        public TextView youCanEarnText;
//        //public TextView dividerText;
//        public LinearLayout backgroundRoot;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        Answer answer = getItem(position);
        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item, parent, false);
            viewHolder = new ViewHolder();

            // root elements
            viewHolder.answerQuestionRoot = (LinearLayout) convertView.findViewById(R.id.answer_question_root);
            viewHolder.processedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.processed_question_root);
            viewHolder.closedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.closed_question_root);

            //answer question elements
            if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
                viewHolder.answerQuestionUserAnswerImageView = (ImageView) convertView.findViewById(R.id.answer_question_user_answer_image_view);
                viewHolder.answerQuestionAnswerTextView = (TextView) convertView.findViewById(R.id.answer_question_answer_text_view);
            }
            //processed question elements
            else if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {
                viewHolder.processedQuestionUserAnswerImageView = (ImageView) convertView.findViewById(R.id.processed_question_user_answer_image_view);
                viewHolder.processedQuestionAnswerTextView = (TextView) convertView.findViewById(R.id.processed_question_answer_text_view);
                viewHolder.processedQuestionAnsweredByTextView = (TextView) convertView.findViewById(R.id.processed_question_answered_by_text_view);
                viewHolder.processedQuestionPercentUsersAnsweredTextView = (TextView) convertView.findViewById(R.id.processed_question_percent_users_answered_text_view);
                viewHolder.processedQuestionYouCanEarnTextView = (TextView) convertView.findViewById(R.id.processed_question_you_can_earn_text_view);
                viewHolder.processedQuestionScoreTextView = (TextView) convertView.findViewById(R.id.processed_question_score_text_view);
                viewHolder.processedQuestionPointsTextView = (TextView) convertView.findViewById(R.id.processed_question_points_text_view);
            }
            //closed question elements
            else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {

                //closed question elements
                viewHolder.closedQuestionUserAnswerImageView = (ImageView) convertView.findViewById(R.id.closed_question_user_answer_image_view);
                viewHolder.closedQuestionRightWrongAnswerIndicatorImageView = (ImageView) convertView.findViewById(R.id.closed_question_right_wrong_answer_indicator_image_view);
                viewHolder.closedQuestionAnswerTextView = (TextView) convertView.findViewById(R.id.closed_question_answer_text_view);
                viewHolder.closedQuestionAnsweredByTextView = (TextView) convertView.findViewById(R.id.closed_question_answered_by_text_view);
                viewHolder.closedQuestionPercentUsersAnsweredTextView = (TextView) convertView.findViewById(R.id.closed_question_percent_users_answered_text_view);
                //viewHolder.closedQuestionYouCanEarnTextView = (TextView) convertView.findViewById(R.id.closed_question_you_can_earn_text_view);
                viewHolder.closedQuestionScoreTextView = (TextView) convertView.findViewById(R.id.closed_question_score_text_view);
                viewHolder.closedQuestionPointsTextView = (TextView) convertView.findViewById(R.id.closed_question_points_text_view);
            }

            convertView.setTag(viewHolder);

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        //String answerNumber = Integer.toString(position + 1);


        viewHolder.answerQuestionRoot.setVisibility(View.GONE);
        viewHolder.processedQuestionRoot.setVisibility(View.GONE);
        viewHolder.closedQuestionRoot.setVisibility(View.GONE);

        //final ImageView fbProfilePicture  = viewHolder.fbPicture;


        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
            viewHolder.answerQuestionRoot.setVisibility(View.VISIBLE);
            viewHolder.answerQuestionAnswerTextView.setText(answer.getAnswerText());

        } else if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {
            viewHolder.processedQuestionRoot.setVisibility(View.VISIBLE);
            viewHolder.processedQuestionAnswerTextView.setText(answer.getAnswerText());
            viewHolder.processedQuestionPercentUsersAnsweredTextView.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())) + "%");
            viewHolder.processedQuestionScoreTextView.setText(Long.toString(Math.round(answer.getScore())));

            viewHolder.processedQuestionUserAnswerImageView.setAlpha(0.0f);

            if (answer.isTheAnswerOfTheUser()) {
                loadFbImage(viewHolder.processedQuestionUserAnswerImageView);
            }


//            viewHolder.fbPicture.setAlpha(0.0f);
//            viewHolder.fbPicture.setVisibility(View.VISIBLE);
//            if (answer.getIsTheAnswerOfTheUser()) {
//                loadFbImage(fbProfilePicture);
//            }
//            viewHolder.percentUsersAnswered.setVisibility(View.VISIBLE);
//            viewHolder.score.setVisibility(View.VISIBLE);
//            viewHolder.answeredByText.setVisibility(View.VISIBLE);
//            //viewHolder.percentSignText.setVisibility(View.VISIBLE);
//            viewHolder.youCanEarnText.setVisibility(View.VISIBLE);
//            //viewHolder.dividerText.setVisibility(View.VISIBLE);

        } else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {
            viewHolder.closedQuestionRoot.setVisibility(View.VISIBLE);
            viewHolder.closedQuestionAnswerTextView.setText(answer.getAnswerText());
            viewHolder.closedQuestionPercentUsersAnsweredTextView.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())) + "%");
            viewHolder.closedQuestionScoreTextView.setText(Long.toString(Math.round(answer.getScore())));
            viewHolder.closedQuestionUserAnswerImageView.setAlpha(0.0f);


            if (answer.isTheAnswerOfTheUser()) {
                loadFbImage(viewHolder.closedQuestionUserAnswerImageView);
            }

            viewHolder.closedQuestionRightWrongAnswerIndicatorImageView.setVisibility(View.VISIBLE);
            if (answer.isCorrect())
                viewHolder.closedQuestionRightWrongAnswerIndicatorImageView.setImageResource(R.drawable.ic_done_white_24dp);
            else
                viewHolder.closedQuestionRightWrongAnswerIndicatorImageView.setImageResource(R.drawable.ic_clear_red_24dp);


        }

        //viewHolder.answerNumber.setText(answerNumber.toString() + ".");
        //viewHolder.answerText.setText(answer.getAnswerText());

        //viewHolder.percentUsersAnswered.setText(context.getString(R.string.lbl_answered_by) + " " + Long.toString(percentUsersAnswered) + "%");
        //viewHolder.percentUsersAnswered.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())) + "%");
        //viewHolder.score.setText(Long.toString(Math.round(answer.getScore())));
        //viewHolder.isTheAnswerOfTheUser.setText(Boolean.toString(answer.getIsTheAnswerOfTheUser()));
        //viewHolder.isCorrect.setText(Boolean.toString(answer.getIsCorrect()));


//        Height


        LinearLayout actualListViewParent = findParent(parent, "question_and_answers_root");
        int listViewHeight = (int) Math.floor(actualListViewParent.getChildAt(1).getHeight() -  actualListViewParent.getChildAt(0).getHeight() );

//workaround to solve different parent height between ask question and process question
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        int savedListViewHeight = settings.getInt(context.getString(R.string.saved_list_view_height_key), 0);
        boolean needsSave = savedListViewHeight == 0;
        savedListViewHeight = needsSave ? listViewHeight : savedListViewHeight;
        // end of workaround


        int itemHeight = (int) Math.floor(savedListViewHeight / answersCount);
        viewHolder.answerQuestionRoot.setMinimumHeight((int) Math.floor(itemHeight * 0.7));
        viewHolder.processedQuestionRoot.setMinimumHeight((int) Math.floor(itemHeight * 0.7));
        convertView.setMinimumHeight(itemHeight);

        if (needsSave) {
            SharedPreferences.Editor editor = settings.edit();
            editor.putInt(context.getString(R.string.saved_list_view_height_key), savedListViewHeight);
            editor.commit();
        }

        //int listViewWidth = parent.getWidth();

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

    private void loadFbImage(final ImageView imageView) {
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        String userPictureUrl = settings.getString(context.getString(R.string.user_profile_picture_url_key), "");
        Uri imageUri = Uri.parse(userPictureUrl);

        Picasso.with(context).load(imageUri).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                imageView.setImageDrawable(roundedImage);
                imageView.animate().alpha(1.1f).setDuration(1000).start();
            }

            @Override
            public void onError() {

            }
        });
    }

//    private View findParentRecursively(View view, String tag) {
//        if (view.getTag() != null && view.getTag().toString() == tag) {
//            return view;
//        }
//        Object parent = null;
//        parent = view.getParent();
//
//        while (!(view.getParent() instanceof LinearLayout)) {
//            try {
//
//                Thread.sleep(1);
//            } catch (InterruptedException e) {
//            }
//        }
//
//
//        if (parent == null) {
//            return null;
//        }
//        return findParentRecursively(parent, tag);
//    }


    private LinearLayout findParent(View view, String tag) {


        ViewParent parent;
        parent = view.getParent();

        while (parent != null) {
            if (parent instanceof LinearLayout
                    && ((View)parent).getTag() != null
                    && ((View)parent).getTag().toString().equals(tag) ) {

                return (LinearLayout) parent;
            }

            parent = parent.getParent();
        }


        return null;

    }










}
