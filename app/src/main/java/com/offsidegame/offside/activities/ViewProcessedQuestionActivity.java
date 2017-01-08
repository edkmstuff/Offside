package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewProcessedQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private String questionState;
    private TextView questionTextView;
    private TextView timeToStartQuestionText;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private int timeToGoBackToPlayerScore;


    private Handler delayHandler;
    private Runnable goToViewPlayerScore;

    private CountDownTimer timer;


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
        setContentView(R.layout.activity_view_processed_question);

        final SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        questionState = bundle.getString("questionState");

        questionTextView = (TextView) findViewById(R.id.question_text);
        String questionText = question.getQuestionText();
        questionTextView.setText(questionText);

        timeToStartQuestionText = (TextView) findViewById(R.id.timeToStartQuestionText);

        final String countDownLabel = getString(R.string.lbl_question_starts_in);
        timeToStartQuestionText.setText(countDownLabel);





//        // to go back to view player score
//        delayHandler = new Handler();
//        goToViewPlayerScore = new Runnable() {
//            @Override
//            public void run() {
//                Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
//                startActivity(intent);
//            }
//        };
//
//        delayHandler.postDelayed(goToViewPlayerScore, 10000);


        timeToGoBackToPlayerScore = settings.getInt(getString(R.string.time_to_go_back_to_player_score_key),15000);
        timer = new CountDownTimer(timeToGoBackToPlayerScore, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToStartQuestionText.setText(Integer.toString( (int)Math.floor(millisUntilFinished / 1000)));
            }

            public void onFinish() {
                timeToStartQuestionText.setText(getString(R.string.lbl_go));
                Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
                startActivity(intent);
            }
        }.start();








    }

    @Override
    public void onResume(){
        super.onResume();
        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        questionEventsHandler.register();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onStop() {
        questionEventsHandler.unregister();
        EventBus.getDefault().unregister(context);
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

    @Override
    public void onBackPressed() {
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        boolean isConnected = connectionEvent.getConnected();
        if (isConnected)
            Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        Context eventContext = signalRServiceBoundEvent.getContext();
        if (eventContext == null) {
            Intent intent = new Intent(context, JoinGameActivity.class);
            context.startActivity(intent);
            return;
        }
    }


    //</editor-fold>
//ToDo: ?? add subscriber to QuestionEvent (ask) on each activity?
    //<editor-fold desc="Subscribers">

//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onReceiveQuestion(QuestionEvent questionEvent) {
//        Question question = questionEvent.getQuestion();
//        String questionState = questionEvent.getQuestionState();
//        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {
//            Intent intent = new Intent(context, AnswerQuestionActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("question", question);
//            bundle.putString("questionState", questionState);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        } else if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION)) {
//            Intent intent = new Intent(context, ViewProcessedQuestionActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("question", question);
//            bundle.putString("questionState", questionState);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        } else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION)) {
//            Intent intent = new Intent(context, ViewClosedQuestionActivity.class);
//            Bundle bundle = new Bundle();
//            bundle.putSerializable("question", question);
//            bundle.putString("questionState", questionState);
//            intent.putExtras(bundle);
//            startActivity(intent);
//        }
//
//
//    }

    //</editor-fold>


}
