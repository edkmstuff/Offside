package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.IsAnswerAcceptedEvent;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionAnsweredEvent;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class AnswerQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private String questionState;
    private TextView questionTextView;
    private LinearLayout questionAndAnswersRoot;
    private LinearLayout timeToNextQuestionRoot;
    private CountDownTimer timer;
    private TextView timeToNextQuestionTextView;
    private final int timeToNextQuestion = 10000;
    private int secondsLeft = 0;

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };


    //</editor-fold>

    //<editor-fold desc="Getters">

    @Override
    public Question getQuestion() {
        return question;
    }

    @Override
    public String getQuestionState() {
        return questionState;
    }

    //</editor-fold>

    //<editor-fold desc="startup">


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        questionAndAnswersRoot = (LinearLayout) findViewById(R.id.question_and_answers_root);
        timeToNextQuestionRoot = (LinearLayout) findViewById(R.id.time_to_next_question_root);
        questionTextView = (TextView) findViewById(R.id.question_text);
        timeToNextQuestionTextView = (TextView) findViewById(R.id.time_to_next_question);

        //show timer first
        questionAndAnswersRoot.setVisibility(View.GONE);
        timeToNextQuestionRoot.setVisibility(View.VISIBLE);

        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        questionState = bundle.getString("questionState");
        questionTextView.setText(question.getQuestionText());

        //bind to signalR service
        Intent bindServiceIntent = new Intent();
        bindServiceIntent.setClass(context, SignalRService.class);
        bindService(bindServiceIntent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

        //run timer

        timer = new CountDownTimer(timeToNextQuestion, 100) {
            public void onTick(long millisUntilFinished) {
                if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                    secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                    timeToNextQuestionTextView.setText(Integer.toString(secondsLeft));
                }
            }
            public void onFinish() {
                //timer is done now we show question
                timeToNextQuestionRoot.setVisibility(View.GONE);
                questionAndAnswersRoot.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(context);
        // Unbind from the service
        if (isBoundToSignalRService) {
            unbindService(signalRServiceConnection);
            isBoundToSignalRService = false;
        }

        //clear stuff
        timer.cancel();
        timer = null;

        super.onStop();
    }


    //</editor-fold>

    //<editor-fold desc="Callbacks">

    // event fires when user clicks on answer in the list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnswered) {
        String gameId = questionAnswered.getGameId();
        String questionId = questionAnswered.getQuestionId();
        String answerId = questionAnswered.getAnswerId();
        signalRService.postAnswer(gameId, questionId, answerId);

    }

    //event fires when the server approved it got the user answer
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIsAnswerAcceptedEvent(IsAnswerAcceptedEvent answerAcceptedEvent) {
        if (answerAcceptedEvent.getIsAnswerAccepted())
            Toast.makeText(context, getString(R.string.lbl_answer_accepted_message), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, getString(R.string.lbl_answer_not_accepted_message), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
        startActivity(intent);
    }

    //</editor-fold>


}
