package com.offsidegame.offside;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;


import com.offsidegame.offside.helpers.SignalRService;
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

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
            //EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer_question);

        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        questionState = bundle.getString("questionState");

        questionTextView = (TextView) findViewById(R.id.question_text);
        questionTextView.setText(question.getQuestionText());

        Intent bindServiceIntent = new Intent();
        bindServiceIntent.setClass(context, SignalRService.class);
        bindService(bindServiceIntent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
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

        super.onStop();
    }


    //</editor-fold>

    //<editor-fold desc="Subscribers">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnswered) {
        String gameId = questionAnswered.getGameId();
        String questionId = questionAnswered.getQuestionId();
        String answerId = questionAnswered.getAnswerId();
        signalRService.postAnswer(gameId, questionId, answerId);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIsAnswerAcceptedEvent(IsAnswerAcceptedEvent answerAcceptedEvent) {
        if (answerAcceptedEvent.getIsAnswerAccepted())
            Toast.makeText(context, getString(R.string.answer_accepted_message), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, getString(R.string.answer_not_accepted_message), Toast.LENGTH_LONG).show();

        Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
        startActivity(intent);
    }

    //</editor-fold>


}
