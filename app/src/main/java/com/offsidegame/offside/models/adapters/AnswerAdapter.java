package com.offsidegame.offside.models.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.Answer;

import java.util.ArrayList;

/**
 * Created by KFIR on 11/21/2016.
 */

public class AnswerAdapter extends ArrayAdapter<Answer> {
    public AnswerAdapter(Context context, ArrayList<Answer> answers){
        super(context, 0, answers);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        Answer answer = getItem(position);

        if (convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.answer_list_item, parent, false);
        }

        TextView answerText = (TextView) convertView.findViewById(R.id.answer_text);
        TextView percentUsersAnswered = (TextView) convertView.findViewById(R.id.percent_users_answered);
        TextView score = (TextView) convertView.findViewById(R.id.score);
        TextView isTheAnswerOfTheUser = (TextView) convertView.findViewById(R.id.is_the_answer_of_the_user);
        TextView isCorrect = (TextView) convertView.findViewById(R.id.is_correct);

        answerText.setText(answer.getAnswerText());
        percentUsersAnswered.setText(Double.toString(answer.getPercentUsersAnswered()));
        score.setText(Double.toString(answer.getScore()));
        isTheAnswerOfTheUser.setText(Boolean.toString(answer.getIsTheAnswerOfTheUser()));
        isCorrect.setText(Boolean.toString(answer.getIsCorrect()));

        return convertView;

    }
}
