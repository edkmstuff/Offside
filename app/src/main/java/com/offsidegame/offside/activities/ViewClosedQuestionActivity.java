package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Date;

public class ViewClosedQuestionActivity extends AppCompatActivity implements IQuestionHolder {

    //<editor-fold desc="Class members">
    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private Question question;
    private String questionState;
    private CountDownTimer timer;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private int timeToGoBackToPlayerScore;

    private TextView timeToGoBackToPlayerScoreTextView;
    private TextView questionTextView;
    private TextView correctAnswerTextView;
    private ImageView playerAnswerImageView;
    private ImageView playerAnswerRightWrongIndicatorImageView;
    private TextView playerAnswerTextView;
    private LinearLayout youEarnedRoot;
    private TextView earnedPointsTextView;
    private boolean isActivityPaused =false ;


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

        final SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

        Answer correctAnswer = null;
        Answer playerAnswer = null;
        //get the question
        Bundle bundle = getIntent().getExtras();
        question = (Question) bundle.getSerializable("question");
        questionState = bundle.getString("questionState");

        for (Answer ans : question.getAnswers()) {
            if (ans.isTheAnswerOfTheUser())
                playerAnswer = ans;
            if (ans.isCorrect())
                correctAnswer = ans;
        }


        questionTextView = (TextView) findViewById(R.id.vcq_question_text_view);
        correctAnswerTextView = (TextView) findViewById(R.id.vcq_correct_answer_text_view);
        playerAnswerImageView = (ImageView) findViewById(R.id.vcq_player_answer_image_view);
        playerAnswerRightWrongIndicatorImageView = (ImageView) findViewById(R.id.vcq_player_answer_right_wrong_indicator_image_view);
        playerAnswerTextView = (TextView) findViewById(R.id.vcq_player_answer_text_view);
        youEarnedRoot = (LinearLayout) findViewById(R.id.vcq_you_earned_root);
        earnedPointsTextView = (TextView) findViewById(R.id.vcq_earned_points_text_view);

        questionTextView.setText(question.getQuestionText());
        correctAnswerTextView.setText(correctAnswer != null ? correctAnswer.getAnswerText() : "error: no correct answer");
        loadPlayerImage(playerAnswerImageView);
        if (playerAnswer != null && playerAnswer.isCorrect())
            playerAnswerRightWrongIndicatorImageView.setImageResource(R.drawable.ic_done_white_24dp);
        else
            playerAnswerRightWrongIndicatorImageView.setImageResource(R.drawable.ic_clear_red_24dp);

        playerAnswerTextView.setText(playerAnswer != null ? playerAnswer.getAnswerText(): "");
        if (playerAnswer != null && playerAnswer.isCorrect()) {
            youEarnedRoot.setVisibility(View.VISIBLE);
            earnedPointsTextView.setText(Integer.toString((int) playerAnswer.getScore()));
        } else {
            youEarnedRoot.setVisibility(View.GONE);
        }


        timeToGoBackToPlayerScoreTextView = (TextView) findViewById(R.id.vcq_time_to_go_back_to_player_score_text_view);
        timeToGoBackToPlayerScore = settings.getInt(getString(R.string.time_to_go_back_to_player_score_key), 15000);
        timer = new CountDownTimer(timeToGoBackToPlayerScore, 1000) {

            public void onTick(long millisUntilFinished) {
                timeToGoBackToPlayerScoreTextView.setText(Integer.toString((int) Math.floor(millisUntilFinished / 1000)));
            }

            public void onFinish() {
                Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
                startActivity(intent);
            }
        }.start();


    }


    private void loadPlayerImage(final ImageView imageView) {
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        String userPictureUrl = settings.getString(context.getString(R.string.user_profile_picture_url_key), "");
        Uri imageUri = Uri.parse(userPictureUrl);

        Picasso.with(context).load(imageUri).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                imageView.setImageDrawable(roundedImage);
                imageView.animate().alpha(1.1f).setDuration(1000).start();
            }

            @Override
            public void onError() {

            }
        });
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
        else{
            Intent intent = new Intent();
            intent.setClass(context, SignalRService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
        }

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);
        questionEventsHandler.register();
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        boolean isAlertsOn = settings.getBoolean(getString(R.string.is_alerts_on_key), true);
        if (isAlertsOn) {
            MediaPlayer player;
            Answer correctAnswer = null;
            for (Answer ans : question.getAnswers()) {
                if (ans.isCorrect()) {
                    correctAnswer = ans;
                    break;
                }
            }
            if (correctAnswer != null) {
                if (correctAnswer.isTheAnswerOfTheUser())
                    player = MediaPlayer.create(context, R.raw.hooray);
                else
                    player = MediaPlayer.create(context, R.raw.aww);

                player.start();
            }

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

        //delayHandler.removeCallbacks(goToViewPlayerScore);
        if(timer!=null)
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

    //<editor-fold desc="Subscribers">


//</editor-fold>


}
