package com.offsidegame.offside.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;

import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.QuestionEvent;
import com.offsidegame.offside.adapters.AnswerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;


public class AnswersFragment extends ListFragment {

    public Question question;
    public Answer[] answers;
    public String questionState;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        IQuestionHolder activity = (IQuestionHolder) getActivity();

        question = activity.getQuestion();
        questionState = activity.getQuestionState();
        answers = question.getAnswers();

        ArrayList<Answer> values = new ArrayList<Answer>(Arrays.asList(answers));
        Context context = getActivity();
        AnswerAdapter answerAdapter = new AnswerAdapter(context, values, questionState);
        setListAdapter(answerAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
            super.onListItemClick(l, v, position, id);
            final String gameId = question.getGameId();
            final String questionId = question.getId();
            final String answerId = answers[position].getId();

            v.animate().rotationX(360.0f).setDuration(200).start();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId));
                }
            }, 300);


        }
    }
}

