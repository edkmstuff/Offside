package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.fragments.AnswersFragment;
import com.offsidegame.offside.activities.fragments.QuestionsFragment;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.DateHelper;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.events.IsAnswerAcceptedEvent;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionAnsweredEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class AnswerQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">
    private Context callingContext = null;
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private Question[] batchedQuestions;
    private Queue<Question> batchedQuestionsQueue;
    private boolean isBatch;
    private String questionState;
    private TextView questionTextView;
    private TextView statQuestionTextView;
    private LinearLayout questionAndAnswersRoot;
    private LinearLayout calcQuestionStatisticsRoot;
    private LinearLayout timeToNextQuestionRoot;
    private CountDownTimer timeToAnswerTimer;
    private CountDownTimer timeToNextQuestionTimer;
    private TextView timeToNextQuestionTextView;
    private TextView timeToAnswerTextView;
    private int timeToQuestionToPop;
    private int timeToAnswer;
    private int secondsLeft = 0;
    private String answerId = null;
    private final DateHelper dateHelper = new DateHelper();
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private boolean isRandomAnswer = false;
    private boolean isActivityPaused =false ;



    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
            EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };

    @Override
    public void onBackPressed() {
    }

    private boolean isAnswered = false;

    public boolean isAnswered() {
        return isAnswered;
    }

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

        //final SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

        questionAndAnswersRoot = (LinearLayout) findViewById(R.id.question_and_answers_root);
        calcQuestionStatisticsRoot = (LinearLayout) findViewById(R.id.calc_question_statistics_root);
        timeToNextQuestionRoot = (LinearLayout) findViewById(R.id.time_to_next_question_root);
        questionTextView = (TextView) findViewById(R.id.question_text);
        statQuestionTextView = (TextView) findViewById(R.id.stat_question_text_view);
        timeToNextQuestionTextView = (TextView) findViewById(R.id.time_to_next_question);
        timeToAnswerTextView = (TextView) findViewById(R.id.time_to_answer_text_view);
        //lblRandomAnswerAcceptedMessageTextView = (TextView) findViewById(R.id.lbl_random_answer_accepted_message);

        //show timer first
        questionAndAnswersRoot.setVisibility(View.GONE);
        timeToNextQuestionRoot.setVisibility(View.VISIBLE);

        //get the question / batched questions
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

        callingContext = ((OffsideApplication) getApplicationContext()).getContext();

        showQuestion();
    }

    private void showQuestion(/*final boolean doProcess*/) {
        //reset state
        if(timeToNextQuestionTimer != null)
            timeToNextQuestionTimer.cancel();
        if (timeToAnswerTimer != null)
            timeToAnswerTimer.cancel();

        timeToNextQuestionTextView.setText("");
        timeToAnswerTextView.setText("");
        isAnswered = false;

        timeToNextQuestionRoot.setVisibility(View.VISIBLE);
        questionAndAnswersRoot.setVisibility(View.GONE);
        answerId = null;
        //end of reset

        questionTextView.setText(question.getQuestionText());
        statQuestionTextView.setText(question.getQuestionText());
        AnswersFragment answersFragment = (AnswersFragment) getSupportFragmentManager().findFragmentById(R.id.activity_answers_fragment);
        answersFragment.updateData(question, questionState, context);

        //run timer
        timeToQuestionToPop = question.getTimeToQuestionToPop();
        timeToNextQuestionTimer = new CountDownTimer(timeToQuestionToPop, 100) {
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
                secondsLeft = 0;

                timeToAnswer = question.getTimeToAnswerQuestion();
                timeToAnswerTimer = new CountDownTimer(timeToAnswer, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                            secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                            timeToAnswerTextView.setText(Integer.toString(secondsLeft));
                            if (secondsLeft < 7 && secondsLeft > 3)
                                timeToAnswerTextView.setBackgroundColor(Color.parseColor("#FFAB00"));
                            if (secondsLeft < 4)
                                timeToAnswerTextView.setBackgroundColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFinish() {
                        //user did not answer this question, we select random answer
                        if (answerId == null) {
                            isRandomAnswer = true;
                            int answersCount = question.getAnswers().length;
                            int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
                            String randomAnswerId = question.getAnswers()[selectedAnswerIndex].getId();
                            QuestionAnsweredEvent questionAnsweredEvent = new QuestionAnsweredEvent(question.getGameId(), question.getId(), randomAnswerId, true);
                            EventBus.getDefault().post(questionAnsweredEvent);
                            //signalRService.postAnswer(question.getGameId(), question.getId(), randomAnswerId);
//                            if (doProcess){
//                                calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
//                                questionAndAnswersRoot.setVisibility(View.GONE);
//                                lblRandomAnswerAcceptedMessageTextView.setVisibility(View.VISIBLE);
//                            }
                        }
                    }
                }.start();
            }
        }.start();
    }

    @Override
    public void onResume() {
        super.onResume();
        if(isActivityPaused){
            isActivityPaused=false;
            Intent intent = new Intent();
            intent.setClass(context, ViewPlayerScoreActivity.class);
            startActivity(intent);
        }

        else {
            Intent intent = new Intent();
            intent.setClass(context, SignalRService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        questionEventsHandler.register();
        EventBus.getDefault().register(context);
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        boolean isAlertsOn = settings.getBoolean(getString(R.string.is_alerts_on_key), true);
        if (isAlertsOn) {
            MediaPlayer player;
            player = MediaPlayer.create(context, R.raw.referee_short_whistle);
            player.start();
        }
    }

    @Override
    public void onPause(){
        isActivityPaused = true;
        super.onPause();
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

        //clear stuff
        if(timeToNextQuestionTimer != null)
            timeToNextQuestionTimer.cancel();
        timeToNextQuestionTimer = null;

        super.onStop();
    }


    //</editor-fold>

    //<editor-fold desc="Callbacks">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        boolean isConnected = connectionEvent.getConnected();
        if (isConnected)
            Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
    }

    // event fires when user clicks on answer in the list
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnswered) {
        String gameId = questionAnswered.getGameId();
        String questionId = questionAnswered.getQuestionId();
        boolean isRandomAnswer = questionAnswered.isRandomAnswer();

        // this parameter will be null if the user does not answer
        answerId = questionAnswered.getAnswerId();
        signalRService.postAnswer(gameId, questionId, answerId);
        if (!isBatch) {
            calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
            questionAndAnswersRoot.setVisibility(View.GONE);
//            if (isRandomAnswer)
//                lblRandomAnswerAcceptedMessageTextView.setVisibility(View.VISIBLE);
        } else {
            if (batchedQuestionsQueue.isEmpty()) {
                calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
                questionAndAnswersRoot.setVisibility(View.GONE);
//                if (isRandomAnswer)
//                    lblRandomAnswerAcceptedMessageTextView.setVisibility(View.VISIBLE);
            } else {
                question = batchedQuestionsQueue.remove();
                showQuestion();
            }
        }

    }

    //event fires when the server approved it got the user answer
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onIsAnswerAcceptedEvent(IsAnswerAcceptedEvent answerAcceptedEvent) {

//        if(isRandomAnswer){
//            Toast.makeText(context, getString(R.string.lbl_random_answer_accepted_message), Toast.LENGTH_LONG).show();
//        }
//
//        else
        if (answerAcceptedEvent.getIsAnswerAccepted())
            Toast.makeText(context, getString(R.string.lbl_answer_accepted_message), Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, getString(R.string.lbl_answer_not_accepted_message), Toast.LENGTH_LONG).show();

        //calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
        //questionAndAnswersRoot.setVisibility(View.GONE);
        isAnswered = true;

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        Context eventContext = signalRServiceBoundEvent.getContext();
        if (eventContext == null) {
            Intent intent = new Intent(context, JoinGameActivity.class);
            context.startActivity(intent);
            return;
        }
        if (callingContext != null && callingContext.getClass() == ViewPlayerScoreActivity.class) {
            callingContext.unbindService(((ViewPlayerScoreActivity) callingContext).signalRServiceConnection);
            callingContext = null;
            ((OffsideApplication) getApplicationContext()).setContext(null);
        }
    }


    //</editor-fold>


}
