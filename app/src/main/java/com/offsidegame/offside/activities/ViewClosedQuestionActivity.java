package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;

public class ViewClosedQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private String questionState;
    private TextView questionTextView;
    //private Handler delayHandler;
    //private Runnable goToViewPlayerScore;
    private CountDownTimer timer;
    private TextView timeToStartQuestionText;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);


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
        setContentView(R.layout.activity_view_closed_question);

        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        questionState = bundle.getString("questionState");

        questionTextView = (TextView) findViewById(R.id.question_text);
        String questionText = question.getQuestionText();
        questionTextView.setText(questionText);


        Intent bindServiceIntent = new Intent();
        bindServiceIntent.setClass(context, SignalRService.class);
        bindService(bindServiceIntent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

        timeToStartQuestionText = (TextView) findViewById(R.id.timeToStartQuestionText);
        timer = new CountDownTimer(20000, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToStartQuestionText.setText(Integer.toString( (int)Math.floor(millisUntilFinished / 1000)));
            }

            public void onFinish() {
                Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
                startActivity(intent);
            }
        }.start();




        // to go back to view player score
//        delayHandler = new Handler();
//        goToViewPlayerScore = new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
//                startActivity(intent);
//            }
//        };
//
//        delayHandler.postDelayed(goToViewPlayerScore, 20000);
    }

    @Override
    public void onStart() {
        super.onStart();
        questionEventsHandler.register();

    }

    @Override
    public void onStop() {
        questionEventsHandler.unregister();
        // Unbind from the service
        if (isBoundToSignalRService) {
            unbindService(signalRServiceConnection);
            isBoundToSignalRService = false;
        }

        //delayHandler.removeCallbacks(goToViewPlayerScore);
        timer.cancel();
        timer = null;

        super.onStop();
    }
    //</editor-fold>

    //<editor-fold desc="Subscribers">


//</editor-fold>


}
