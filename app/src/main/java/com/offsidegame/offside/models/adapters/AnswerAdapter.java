package com.offsidegame.offside.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.QuestionEvent;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class AnswerAdapter extends ArrayAdapter<Answer> {

    private String questionState;
    private Context context;
    public AnswerAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    public AnswerAdapter(Context context, ArrayList<Answer> answers, String questionState) {
        super(context, 0, answers);
        this.context = context;
        this.questionState = questionState;
    }



    private class ViewHolder {
        public ImageView rightWrongAnswerIndicator;
        public TextView answerNumber;
        public TextView answerText;
        public TextView percentUsersAnswered;
        public ProgressBar percentUsersAnsweredProgressBar;
        public TextView score;
        public TextView isTheAnswerOfTheUser;
        public TextView isCorrect;
        public LinearLayout answerListItem;
        public TextView answeredByText;
        public TextView percentSignText;
        public TextView youCanEarnText;
        public TextView pointsText;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Answer answer = getItem(position);
        //Answer answer = new Answer("id","I am answer text", 0.5,200,true, true);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.rightWrongAnswerIndicator = (ImageView) convertView.findViewById(R.id.right_wrong_answer_indicator);
            viewHolder.answerNumber = (TextView) convertView.findViewById(R.id.answer_number);
            viewHolder.answerText = (TextView) convertView.findViewById(R.id.answer_text);
            viewHolder.percentUsersAnswered = (TextView) convertView.findViewById(R.id.percent_users_answered);
            //viewHolder.percentUsersAnsweredProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar_percent_user_Answered);
            viewHolder.score = (TextView) convertView.findViewById(R.id.score);
            //viewHolder.isTheAnswerOfTheUser = (TextView) convertView.findViewById(R.id.is_the_answer_of_the_user);
            //viewHolder.isCorrect = (TextView) convertView.findViewById(R.id.is_correct);
            viewHolder.answerListItem = (LinearLayout) convertView.findViewById(R.id.answer_list_item);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String answerNumber = Integer.toString(position + 1);



        viewHolder.rightWrongAnswerIndicator.setVisibility(View.INVISIBLE);
        //viewHolder.answerNumber.setVisibility(View.INVISIBLE);
        //viewHolder.answerText.setVisibility(View.INVISIBLE);
        viewHolder.percentUsersAnswered.setVisibility(View.INVISIBLE);
        //viewHolder.percentUsersAnsweredProgressBar.setVisibility(View.INVISIBLE);
        viewHolder.score.setVisibility(View.INVISIBLE);
        //viewHolder.isTheAnswerOfTheUser.setVisibility(View.INVISIBLE);
        //viewHolder.isCorrect.setVisibility(View.INVISIBLE);


//        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
//            viewHolder.answerNumber.setVisibility(View.VISIBLE);
//            viewHolder.answerText.setVisibility(View.VISIBLE);
//        }
        if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {

            if(answer.getIsTheAnswerOfTheUser()) {
                viewHolder.answerListItem.setBackgroundResource(R.color.colorAccent);
            }

//            viewHolder.answerNumber.setVisibility(View.VISIBLE);
//            viewHolder.answerText.setVisibility(View.VISIBLE);
            viewHolder.percentUsersAnswered.setVisibility(View.VISIBLE);
            //viewHolder.percentUsersAnsweredProgressBar.setVisibility(View.VISIBLE);
            viewHolder.score.setVisibility(View.VISIBLE);
            //viewHolder.isTheAnswerOfTheUser.setVisibility(View.VISIBLE);
        } else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {

            if(answer.getIsCorrect())
                viewHolder.rightWrongAnswerIndicator.setImageResource(R.drawable.ic_done_black_24dp);
            else
                viewHolder.rightWrongAnswerIndicator.setImageResource(R.drawable.ic_clear_black_24dp);

            if(answer.getIsTheAnswerOfTheUser()) {
                viewHolder.answerListItem.setBackgroundResource(R.color.colorAccent);
            }
//            viewHolder.answerNumber.setVisibility(View.VISIBLE);
//            viewHolder.answerText.setVisibility(View.VISIBLE);
            viewHolder.score.setVisibility(View.INVISIBLE);
            //viewHolder.isTheAnswerOfTheUser.setVisibility(View.INVISIBLE);
            //viewHolder.isCorrect.setVisibility(View.INVISIBLE);

        }

        viewHolder.answerNumber.setText(answerNumber.toString() + ".");
        viewHolder.answerText.setText(answer.getAnswerText());

        //viewHolder.percentUsersAnswered.setText(context.getString(R.string.lbl_answered_by) + " " + Long.toString(percentUsersAnswered) + "%");
        viewHolder.percentUsersAnswered.setText(Long.toString(Math.round(answer.getPercentUsersAnswered())));
        viewHolder.score.setText(Long.toString(Math.round(answer.getScore())));
        //viewHolder.isTheAnswerOfTheUser.setText(Boolean.toString(answer.getIsTheAnswerOfTheUser()));
        //viewHolder.isCorrect.setText(Boolean.toString(answer.getIsCorrect()));


        return convertView;

    }


}
