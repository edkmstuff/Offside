package com.offsidegame.offside;


import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionAnsweredEvent;
import com.offsidegame.offside.models.QuestionEvent;
import com.offsidegame.offside.models.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.adapters.AnswerAdapter;

import org.greenrobot.eventbus.EventBus;

import java.io.StringBufferInputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A simple {@link Fragment} subclass.
 */
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
//        Answer[] answers = new Answer[]{
//                new Answer("1", "This is answer 1", 0.2, 100, false, false),
//                new Answer("2", "This is answer 1", 0.2, 100, false, false),
//                new Answer("3", "This is answer 1", 0.5, 30, false, false),
//                new Answer("4", "This is answer 1", 0.1, 300, false, false)
//        };

        ArrayList<Answer> values = new ArrayList<Answer>(Arrays.asList(answers));
//
//        values.addAll(answers);

        AnswerAdapter answerAdapter = new AnswerAdapter(getActivity(), values, questionState);

        setListAdapter(answerAdapter);


    }


    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {

        if(questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)){
            super.onListItemClick(l, v, position, id);
            String gameId = question.getGameId();
            String questionId = question.getId();
            String answerId = answers[position].getId();
            EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId));

        }
    }


}

