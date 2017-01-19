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
import com.offsidegame.offside.activities.fragments.AnswersFragment;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.LinkedList;
import java.util.Queue;

public class ViewProcessedQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private Question[] batchedQuestions;
    private Queue<Question> batchedQuestionsQueue;
    private boolean isBatch = false;
    private String questionState;
    private TextView questionTextView;
    private TextView timeToGoBackToPlayerScoreTextView;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private int timeToGoBackToPlayerScore;




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


        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        batchedQuestions = (Question[]) bundle.getSerializable("batchedQuestions");
        batchedQuestionsQueue = new LinkedList<>();
        if (batchedQuestions != null) {
            for (int i = 0; i < batchedQuestions.length; i++)
                batchedQuestionsQueue.add(batchedQuestions[i]);
        }

        isBatch = bundle.getBoolean("isBatch");
        questionState = bundle.getString("questionState");
        question = isBatch ? batchedQuestionsQueue.remove() : question;
        timeToGoBackToPlayerScoreTextView = (TextView) findViewById(R.id.vpq_time_to_go_back_to_player_score_text_view);
        questionTextView = (TextView) findViewById(R.id.question_text);

        showQuestion(!isBatch);
    }

    private void showQuestion(final boolean goToPlayerScore) {
        //reset
        if (timer != null)
            timer.cancel();

        timeToGoBackToPlayerScoreTextView.setText("");
        questionTextView.setText("");
        //end of rest


        final SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

        String questionText = question.getQuestionText();
        questionTextView.setText(questionText);



        final String countDownLabel = getString(R.string.lbl_question_starts_in);
        timeToGoBackToPlayerScoreTextView.setText(countDownLabel);

        AnswersFragment answersFragment = (AnswersFragment) getSupportFragmentManager().findFragmentById(R.id.activity_answers_fragment);
        answersFragment.updateData(question, questionState, context);

        timeToGoBackToPlayerScore = settings.getInt(getString(R.string.time_to_go_back_to_player_score_key), 15000);
        timer = new CountDownTimer(timeToGoBackToPlayerScore, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToGoBackToPlayerScoreTextView.setText(Integer.toString((int) Math.floor(millisUntilFinished / 1000)));
            }

            public void onFinish() {

                if (goToPlayerScore) {
                    timeToGoBackToPlayerScoreTextView.setText(getString(R.string.lbl_go));
                    Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
                    startActivity(intent);
                } else {
                    if (!batchedQuestionsQueue.isEmpty()) {
                        question = batchedQuestionsQueue.remove();
                        EventBus.getDefault().post(question);
                    }
                }
            }
        }.start();
    }

    @Override
    public void onResume() {
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onBatchedQuestionPosted(Question batchedQuestion) {
        showQuestion(batchedQuestionsQueue.isEmpty());
    }


}
