package com.offsidegame.offside.helpers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.offsidegame.offside.activities.AnswerQuestionActivity;
import com.offsidegame.offside.activities.ViewClosedQuestionActivity;
import com.offsidegame.offside.activities.ViewProcessedQuestionActivity;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by KFIR on 11/27/2016.
 */

public class QuestionEventsHandler {
    private Context context;
    public QuestionEventsHandler(Context context){
        this.context = context;
        EventBus.getDefault().register(context);
    }

    @Override
    public void finalize() {
        EventBus.getDefault().unregister(context);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveQuestion(QuestionEvent questionEvent) {
        Question question =  questionEvent.getQuestion();
        String questionState = questionEvent.getQuestionState();

        // NEW_QUESTION is default
        Class<?> activityClass = AnswerQuestionActivity.class;

        // PROCESSED_QUESTION
        if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION))
            activityClass = ViewProcessedQuestionActivity.class;

        // CLOSED_QUESTION
        else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION))
            activityClass = ViewClosedQuestionActivity.class;

        Intent intent = new Intent(context, activityClass);
        Bundle bundle = new Bundle();
        bundle.putSerializable("question", question);
        bundle.putString("questionState", questionState);
        intent.putExtras(bundle);
        context.startActivity(intent);


    }


}
