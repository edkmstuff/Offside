package com.offsidegame.offside.models.adapters;

import android.content.Context;
import android.opengl.Visibility;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
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

    public AnswerAdapter(Context context, ArrayList<Answer> answers) {
        super(context, 0, answers);
    }

    public AnswerAdapter(Context context, ArrayList<Answer> answers, String questionState) {
        super(context, 0, answers);
        this.questionState = questionState;
    }

    private String questionState;

    private class ViewHolder {
        public TextView answerNumber;
        public TextView answerText;
        public TextView percentUsersAnswered;
        public ProgressBar percentUsersAnsweredProgressBar;
        public TextView score;
        public TextView isTheAnswerOfTheUser;
        public TextView isCorrect;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Answer answer = getItem(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item, parent, false);
            viewHolder = new ViewHolder();

            viewHolder.answerNumber = (TextView) convertView.findViewById(R.id.answer_number);
            viewHolder.answerText = (TextView) convertView.findViewById(R.id.answer_text);
            viewHolder.percentUsersAnswered = (TextView) convertView.findViewById(R.id.percent_users_answered);
            viewHolder.percentUsersAnsweredProgressBar = (ProgressBar) convertView.findViewById(R.id.progress_bar_percent_user_Answered);
            viewHolder.score = (TextView) convertView.findViewById(R.id.score);
            viewHolder.isTheAnswerOfTheUser = (TextView) convertView.findViewById(R.id.is_the_answer_of_the_user);
            viewHolder.isCorrect = (TextView) convertView.findViewById(R.id.is_correct);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        String answerNumber = Integer.toString(position + 1);



        viewHolder.answerNumber.setVisibility(View.GONE);
        viewHolder.answerText.setVisibility(View.GONE);
        viewHolder.percentUsersAnswered.setVisibility(View.GONE);
        viewHolder.score.setVisibility(View.GONE);
        viewHolder.isTheAnswerOfTheUser.setVisibility(View.GONE);
        viewHolder.isCorrect.setVisibility(View.GONE);

        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
            viewHolder.answerNumber.setVisibility(View.VISIBLE);
            viewHolder.answerText.setVisibility(View.VISIBLE);
        } else if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {
            viewHolder.answerNumber.setVisibility(View.VISIBLE);
            viewHolder.answerText.setVisibility(View.VISIBLE);
            viewHolder.percentUsersAnswered.setVisibility(View.VISIBLE);
            viewHolder.score.setVisibility(View.VISIBLE);
            viewHolder.isTheAnswerOfTheUser.setVisibility(View.VISIBLE);
        } else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {
            viewHolder.answerNumber.setVisibility(View.VISIBLE);
            viewHolder.answerText.setVisibility(View.VISIBLE);
            viewHolder.score.setVisibility(View.INVISIBLE);
            viewHolder.isTheAnswerOfTheUser.setVisibility(View.INVISIBLE);
            viewHolder.isCorrect.setVisibility(View.INVISIBLE);

        }

        viewHolder.answerNumber.setText(answerNumber.toString());
        viewHolder.answerText.setText(answer.getAnswerText());
        viewHolder.percentUsersAnswered.setText(Double.toString(answer.getPercentUsersAnswered()));
        viewHolder.score.setText(Double.toString(answer.getScore()));
        viewHolder.isTheAnswerOfTheUser.setText(Boolean.toString(answer.getIsTheAnswerOfTheUser()));
        viewHolder.isCorrect.setText(Boolean.toString(answer.getIsCorrect()));


        return convertView;

    }


}
