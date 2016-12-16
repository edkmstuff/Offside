package com.offsidegame.offside.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.offsidegame.offside.activities.AnswerQuestionActivity;
import com.offsidegame.offside.activities.ViewClosedQuestionActivity;
import com.offsidegame.offside.activities.ViewPlayerScoreActivity;
import com.offsidegame.offside.activities.ViewProcessedQuestionActivity;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by KFIR on 11/27/2016.
 */

public class QuestionEventsHandler {
    private Activity activity;

    public QuestionEventsHandler(Context context) {
        this.activity = (Activity)context;
    }

    public void register() {
        EventBus.getDefault().register(this);
    }

    public void unregister() {
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveQuestion(QuestionEvent questionEvent) {
        Question question = questionEvent.getQuestion();
        String questionState = questionEvent.getQuestionState();

        // NEW_QUESTION is default
        Class<?> activityClass = AnswerQuestionActivity.class;

        // PROCESSED_QUESTION
        if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION))
            activityClass = ViewProcessedQuestionActivity.class;

            // CLOSED_QUESTION
        else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION))
            activityClass = ViewClosedQuestionActivity.class;

        boolean shouldStartAnswerQuestionActivity = activityClass == AnswerQuestionActivity.class
                && !(activity.getClass() == AnswerQuestionActivity.class);

        boolean shouldStartOtherQuestionActivity = activity.getClass() == ViewPlayerScoreActivity.class
                && (activityClass == ViewClosedQuestionActivity.class || activityClass == ViewProcessedQuestionActivity.class);

        if (shouldStartAnswerQuestionActivity || shouldStartOtherQuestionActivity) {
            Intent intent = new Intent(activity, activityClass);
            Bundle bundle = new Bundle();
            bundle.putSerializable("question", question);
            bundle.putString("questionState", questionState);
            intent.putExtras(bundle);
            activity.startActivity(intent);
        }
    }

}
