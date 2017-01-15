package com.offsidegame.offside.helpers;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.offsidegame.offside.activities.AnswerQuestionActivity;
import com.offsidegame.offside.activities.ViewClosedQuestionActivity;
import com.offsidegame.offside.activities.ViewPlayerScoreActivity;
import com.offsidegame.offside.activities.ViewProcessedQuestionActivity;
import com.offsidegame.offside.activities.ViewQuestionsActivity;
import com.offsidegame.offside.activities.ViewScoreboardActivity;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * Created by KFIR on 11/27/2016.
 */

public class QuestionEventsHandler {
    private Activity context;

    public QuestionEventsHandler(Context context) {
        this.context = (Activity) context;
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


        // all question activities can start when we are in view player score
        boolean shouldStartAllQuestionActivities = false;
        shouldStartAllQuestionActivities = context.getClass() == ViewPlayerScoreActivity.class;

        // answer question activity can start when we are in all other activities except for when we are during the process of answering a question
        boolean shouldStartAnswerQuestionActivity = false;
        shouldStartAnswerQuestionActivity = activityClass == AnswerQuestionActivity.class
                && !(context.getClass() == AnswerQuestionActivity.class);

        // processed question activity can also start when we have already answered the question (calc stats msg is visible)
        boolean shouldStartProcessedQuestionActivity = false;

        AnswerQuestionActivity answerQuestionActivity = null;
        if (context.getClass() == AnswerQuestionActivity.class)
            answerQuestionActivity = (AnswerQuestionActivity) context;

        shouldStartProcessedQuestionActivity = activityClass == ViewProcessedQuestionActivity.class
                && answerQuestionActivity != null
                && answerQuestionActivity.isAnswered();


        if (shouldStartAllQuestionActivities || shouldStartAnswerQuestionActivity || shouldStartProcessedQuestionActivity) {
            Intent intent = new Intent(context, activityClass);
            Bundle bundle = new Bundle();
            bundle.putSerializable("question", question);
            bundle.putString("questionState", questionState);
            intent.putExtras(bundle);
            ((OffsideApplication)context.getApplicationContext()).setContext(context);

            if (context.getClass() == ViewPlayerScoreActivity.class && ((ViewPlayerScoreActivity) context).isInBackground) {

                EventBus.getDefault().unregister(context);
                EventBus.getDefault().unregister(this);
                //context.unbindService(((ViewPlayerScoreActivity) context).signalRServiceConnection);
                //context = null;
            }



            context.startActivity(intent);


        }
    }

}
