package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.PlayerScore;
import com.offsidegame.offside.events.PlayerScoreEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewPlayerScoreActivity extends AppCompatActivity {

    //<editor-fold desc="Class members">

    public boolean isInBackground = false;
    private final Context context = this;
    private SignalRService signalRService;
    private boolean boundToSignalRService = false;
    private TextView gameTitle;
    private TextView score;
    private TextView position;
    private TextView totalPlayers;
    private TextView totalOpenQuestions;
    private TextView totalQuestions;
    private Toolbar toolbar;
    //private TextView fbName;
    private ImageView profilePictureImageView;
    private Button scoreboardBtn;
    private Button questionsBtn;
    private LinearLayout loadingRoot;
    private LinearLayout contentRoot;

    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    public final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            boundToSignalRService = true;
            EventBus.getDefault().post(new SignalRServiceBoundEvent(context));
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            boundToSignalRService = false;
        }
    };

    private TextView currentQuestionTimeLeftTextView;
    private TextView currentQuestionTextView;
    private TextView currentQuestionAnswerTextView;
    private LinearLayout currentQuestionRoot;
    private LinearLayout currentQuestionTimerRoot;
    private LinearLayout nextQuestionTimerRoot;
    private TextView nextQuestionTimeLeftTextView;


    private CountDownTimer timeLeftToCurrentOrNextQuestionTimer;
    private ImageView controlAlertsImageView;

//    private String currentQuestionText;
//    private String currentQuestionAnswerText;
//    private int timeLeftToAnswer;

    //</editor-fold>

    //<editor-fold desc="Startup methods">

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player_score);

        questionEventsHandler.register();
        EventBus.getDefault().register(context);



        //ENABLE THIS WHEN YOU DO ENGLISH (OR ANY OTHER LTR LANG) VERSION
//        View root = findViewById(R.id.activity_player_score_root);
//        ViewGroupHelper.ChangeRtlToLtr(root);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);


        profilePictureImageView = (ImageView) findViewById(R.id.fbPictureImageView);

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String userPictureUrl = settings.getString(getString(R.string.user_profile_picture_url_key), "");
        Uri fbImageUrl = Uri.parse(userPictureUrl);

        Picasso.with(context).load(fbImageUrl).into(profilePictureImageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) profilePictureImageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                profilePictureImageView.setImageDrawable(roundedImage);
            }

            @Override
            public void onError() {

            }
        });


        gameTitle = (TextView) findViewById(R.id.game_title);
        score = (TextView) findViewById(R.id.score);
        position = (TextView) findViewById(R.id.position);
        totalPlayers = (TextView) findViewById(R.id.total_players);
        totalOpenQuestions = (TextView) findViewById(R.id.total_active_questions);
        totalQuestions = (TextView) findViewById(R.id.total_questions);
        scoreboardBtn = (Button) findViewById(R.id.scoreboard_btn);
        questionsBtn = (Button) findViewById(R.id.questions_btn);

        currentQuestionTimeLeftTextView = (TextView) findViewById(R.id.current_question_time_left_text_view);
        currentQuestionTextView = (TextView) findViewById(R.id.current_question_text_view);
        currentQuestionAnswerTextView = (TextView) findViewById(R.id.current_question_answer_text_view);
        currentQuestionRoot = (LinearLayout) findViewById(R.id.current_question_root);
        currentQuestionTimerRoot = (LinearLayout) findViewById(R.id.current_question_timer_root);
        nextQuestionTimerRoot = (LinearLayout) findViewById(R.id.next_question_timer_root);
        nextQuestionTimeLeftTextView = (TextView) findViewById(R.id.next_question_time_left_text_view);

        loadingRoot = (LinearLayout) findViewById(R.id.vps_loading_root);
        contentRoot = (LinearLayout) findViewById(R.id.vps_content_root);

        loadingRoot.setVisibility(View.VISIBLE);
        contentRoot.setVisibility(View.GONE);



        currentQuestionRoot.setVisibility(View.GONE);


        scoreboardBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewScoreboardActivity.class);
                String positionText = (String) position.getText();
                String totalPlayersText = (String) totalPlayers.getText();

                intent.putExtra("position", positionText);
                intent.putExtra("totalPlayers", totalPlayersText);
                startActivity(intent);
            }
        });

        questionsBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewQuestionsActivity.class);
                startActivity(intent);
            }
        });


        //alerts
        controlAlertsImageView = (ImageView) findViewById(R.id.vps_control_alerts_image_view);
        boolean isAlertsOn = settings.getBoolean(getString(R.string.is_alerts_on_key), true);
        if (isAlertsOn)
            controlAlertsImageView.setImageResource(R.mipmap.ic_alerts_enabled);
        else
            controlAlertsImageView.setImageResource(R.mipmap.ic_alerts_disabled);

        controlAlertsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                boolean isAlertsOn = settings.getBoolean(getString(R.string.is_alerts_on_key), true);
                SharedPreferences.Editor editor = settings.edit();
                if (isAlertsOn){
                    editor.putBoolean(getString(R.string.is_alerts_on_key), false);
                    controlAlertsImageView.setImageResource(R.mipmap.ic_alerts_disabled);
                }else{
                    editor.putBoolean(getString(R.string.is_alerts_on_key), true);
                    controlAlertsImageView.setImageResource(R.mipmap.ic_alerts_enabled);
                }

                editor.commit();
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        //bind or re-bind to signalRService
        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);


        //resetting list height (workaround in array adapter)
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(context.getString(R.string.saved_list_view_height_key), 0);
        editor.commit();
    }


    @Override
    public void onStart() {
        super.onStart();
       // questionEventsHandler.register();
       // EventBus.getDefault().register(context);
//        Intent intent = getIntent();
//        currentQuestionText = intent.getStringExtra("questionText");
//        currentQuestionAnswerText = intent.getStringExtra("answerText");
//        timeLeftToAnswer = intent.getIntExtra("timeToAnswer",0);
//        if (currentQuestionText != null && currentQuestionAnswerText != null) {
//            currentQuestionTimeLeftTextView.setText("מסתיימת בעוד מספר דקות");
//            currentQuestionTextView.setText(currentQuestionText);
//            currentQuestionAnswerTextView.setText(currentQuestionAnswerText);
//            currentQuestionText = null;
//            currentQuestionAnswerText = null;
//        }

    }

    @Override
    public void onStop() {
        //questionEventsHandler.unregister();
        //EventBus.getDefault().unregister(context);

        // Unbind from signalRService service
        if (boundToSignalRService) {
            //unbindService(signalRServiceConnection);
            //boundToSignalRService = false;
        }
        if (timeLeftToCurrentOrNextQuestionTimer != null) {
            timeLeftToCurrentOrNextQuestionTimer.cancel();
            timeLeftToCurrentOrNextQuestionTimer = null;
        }

        isInBackground = true;
        super.onStop();
    }

    //disable back button (option 1)
    @Override
    public void onBackPressed() {
    }

    //disable back button (option 2)
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        return false;
//    }

    //</editor-fold>

    //<editor-fold desc="Subscribe methods">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        boolean isConnected = connectionEvent.getConnected();
        if (isConnected) {
            Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
            //getPlayerScore();
            //Toast.makeText(context,connectionEvent.getMsg(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
            //Toast.makeText(context,connectionEvent.getMsg(), Toast.LENGTH_LONG).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        Context eventContext = signalRServiceBoundEvent.getContext();
        if (eventContext == null) {
            Intent intent = new Intent(context, JoinGameActivity.class);
            context.startActivity(intent);
            return;
        }
        if (eventContext == context) {


            getPlayerScore();
        }
    }

    private void getPlayerScore() {
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String gameId = settings.getString(getString(R.string.game_id_key), "");
        String userId = settings.getString(getString(R.string.user_id_key), "");
        String userName = settings.getString(getString(R.string.user_name_key), "");

        if (gameId != null && gameId != ""
                && userId != null && userId != ""
                && userName != null && userName != "")
            signalRService.getPlayerScore(gameId, userId, userName);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayerScore(PlayerScoreEvent playerScoreEvent) {

        PlayerScore playerScore = playerScoreEvent.getPlayerScore();
        updatePlayerScoreInUi(playerScore);

        Toast.makeText(context, getString(R.string.lbl_data_updated), Toast.LENGTH_SHORT).show();
    }


    void updatePlayerScoreInUi(PlayerScore playerScore) {
        boolean isOnMainThread = Looper.myLooper() == Looper.getMainLooper();
        if (!isOnMainThread)
            return;

        loadingRoot.setVisibility(View.GONE);
        contentRoot.setVisibility(View.VISIBLE);


        gameTitle.setText(playerScore.getGameTitle().toString());
        score.setText(playerScore.getScore().toString());
        position.setText(playerScore.getPosition().toString());
        totalPlayers.setText(playerScore.getTotalPlayers().toString());
        totalOpenQuestions.setText(playerScore.getTotalOpenQuestions().toString());
        totalQuestions.setText(playerScore.getTotalQuestions().toString());
        String currentQuestionText = playerScore.getCurrentQuestionText();
        if (currentQuestionText != null && currentQuestionText.length() > 0) {
            currentQuestionRoot.setVisibility(View.VISIBLE);
            nextQuestionTimeLeftTextView.setVisibility(View.GONE);
            currentQuestionTimerRoot.setVisibility(View.VISIBLE);
            //currentQuestionTimeLeftTextView.setText(Integer.toString(playerScore.getCurrentQuestionTimeLeftToAnswer()));
            currentQuestionTextView.setText(playerScore.getCurrentQuestionText());
            currentQuestionAnswerTextView.setText(playerScore.getCurrentQuestionAnswerText());

            int currentQuestionExpirationInMilliseconds = playerScore.getCurrentQuestionExpirationInMilliseconds();

            timeLeftToCurrentOrNextQuestionTimer = new CountDownTimer(currentQuestionExpirationInMilliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int min = (int) Math.floor(millisUntilFinished / 1000 / 60);
                    int sec = ((int) Math.floor(millisUntilFinished / 1000) % 60);
                    String minString = Integer.toString(min);
                    String secString = Integer.toString(sec);

                    if (min < 10)
                        minString = "0" + minString;
                    if (sec < 10)
                        secString = "0" + secString;

                    currentQuestionTimeLeftTextView.setText(minString + ":" + secString);
//                    if (millisUntilFinished < 30*1000)
//                        currentQuestionTimeLeftTextView.setText("30 sec");
//                    if (millisUntilFinished < 60*1000)
//                        currentQuestionTimeLeftTextView.setText("1 min");
//                    else if (millisUntilFinished < 2*60*1000)
//                        currentQuestionTimeLeftTextView.setText("2 min");
//                    else if (millisUntilFinished < 3*60*1000)
//                        currentQuestionTimeLeftTextView.setText("3 min");
//                    else if (millisUntilFinished < 4*60*1000)
//                        currentQuestionTimeLeftTextView.setText("4 min");
//                    else if (millisUntilFinished < 5*60*1000)
//                        currentQuestionTimeLeftTextView.setText("5 min");
//
//                    else
//                        currentQuestionTimeLeftTextView.setText("more than 5 min");
                }

                @Override
                public void onFinish() {
                    currentQuestionTimeLeftTextView.setText(R.string.lbl_few_moments);
                }
            }.start();

        } else if (playerScore.getTimeToNextQuestionInMilliseconds() > 0) {

            int timeLeftToNextQuestionInMilliseconds = playerScore.getTimeToNextQuestionInMilliseconds();

            timeLeftToCurrentOrNextQuestionTimer = new CountDownTimer(timeLeftToNextQuestionInMilliseconds, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {

                    int min = (int) Math.floor(millisUntilFinished / 1000 / 60);
                    int sec = ((int) Math.floor(millisUntilFinished / 1000) % 60);
                    String minString = Integer.toString(min);
                    String secString = Integer.toString(sec);

                    if (min < 10)
                        minString = "0" + minString;
                    if (sec < 10)
                        secString = "0" + secString;

                    nextQuestionTimeLeftTextView.setText(minString + ":" + secString);
//                    if (millisUntilFinished < 30*1000)
//                        currentQuestionTimeLeftTextView.setText("30 sec");
//                    if (millisUntilFinished < 60*1000)
//                        currentQuestionTimeLeftTextView.setText("1 min");
//                    else if (millisUntilFinished < 2*60*1000)
//                        currentQuestionTimeLeftTextView.setText("2 min");
//                    else if (millisUntilFinished < 3*60*1000)
//                        currentQuestionTimeLeftTextView.setText("3 min");
//                    else if (millisUntilFinished < 4*60*1000)
//                        currentQuestionTimeLeftTextView.setText("4 min");
//                    else if (millisUntilFinished < 5*60*1000)
//                        currentQuestionTimeLeftTextView.setText("5 min");
//
//                    else
//                        currentQuestionTimeLeftTextView.setText("more than 5 min");
                }

                @Override
                public void onFinish() {
                    nextQuestionTimeLeftTextView.setText(R.string.lbl_few_moments);
                }
            }.start();


            currentQuestionRoot.setVisibility(View.VISIBLE);
            nextQuestionTimeLeftTextView.setVisibility(View.VISIBLE);
            currentQuestionTimerRoot.setVisibility(View.GONE);
        }

    }


    //</editor-fold>


}
