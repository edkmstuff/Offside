package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.IBinder;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.appevents.AppEventsLogger;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.PlayerScore;
import com.offsidegame.offside.events.PlayerScoreEvent;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewPlayerScoreActivity extends AppCompatActivity {

    //<editor-fold desc="Class members">
    private final Context context = this;
    private SignalRService signalRService;
    private boolean boundToSignalRService = false;
    TextView gameTitle;
    TextView score;
    TextView position;
    TextView totalPlayers;
//    TextView leaderScore;
    TextView totalOpenQuestions;
    private Toolbar toolbar;
    TextView fbName;
//    ProfilePictureView fbProfilePicture;
    ImageView fbProfilePicture;
    Button scoreboardBtn;
    Button questionsBtn;
    //</editor-fold>

    //<editor-fold desc="Startup methods">
    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
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

    /**
     * Defines callbacks for service binding, passed to bindService()
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_player_score);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);

        //fbName = (TextView) findViewById(R.id.fbNameTextView);
        //fbProfilePicture = (ProfilePictureView) findViewById(R.id.fbPictureImageView);
        fbProfilePicture = (ImageView) findViewById(R.id.fbPictureImageView);



        //fbName.setText(Profile.getCurrentProfile().getName());
        //fbProfilePicture.setProfileId(Profile.getCurrentProfile().getId());

        Uri fbImageUrl = Profile.getCurrentProfile().getProfilePictureUri(200,200);
        //fbProfilePicture.setImageURI(fbImageUrl);
        Picasso.with(context).load(fbImageUrl).into(fbProfilePicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) fbProfilePicture.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                fbProfilePicture.setImageDrawable(roundedImage);
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
        scoreboardBtn = (Button) findViewById(R.id.scoreboard_btn);
        questionsBtn = (Button) findViewById(R.id.questions_btn);

        scoreboardBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewScoreboardActivity.class);
                startActivity(intent);
            }
        });

        questionsBtn.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                Intent intent = new Intent(context, ViewQuestionsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
//        setContentView(R.layout.activity_player_score);
//        score = (TextView) findViewById(R.id.score);
//        position = (TextView) findViewById(R.id.position);
//        leaderScore = (TextView) findViewById(R.id.leader_score);
//        totalOpenQuestions = (TextView) findViewById(R.id.total_active_questions);
        // Restore preferences

        /*SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        boolean isLoggedIn = settings.getBoolean(getString(R.string.is_logged_in_key), false);

        String loginExpirationTimeAsString = (String) settings.getString(getString(R.string.login_expiration_time), "");

        DateHelper dateHelper = new DateHelper();
        Date loginExpirationTime = dateHelper.formatAsDate(loginExpirationTimeAsString, context);
        if (loginExpirationTime == null)
            loginExpirationTime = dateHelper.getCurrentDate();

        Date current = dateHelper.getCurrentDate();
        if (!isLoggedIn *//*|| current.after(loginExpirationTime)*//* ) {
            Intent intent = new Intent(context, LoginActivity.class);
            startActivity(intent);
            return;
        }*/

        /*Intent intent = new Intent(context, LoginActivity.class);
        startActivity(intent);
        return;
*/
        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);


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
        if (boundToSignalRService) {
            unbindService(signalRServiceConnection);
            boundToSignalRService = false;
        }

        super.onStop();
    }

    //</editor-fold>me

    //<editor-fold desc="Subscribe methods">

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        Context eventContext = signalRServiceBoundEvent.getContext();
        if (eventContext == context) {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            String gameId = settings.getString(getString(R.string.game_id_key), "");
            String userId = settings.getString(getString(R.string.user_id_key), "");
            String userName = settings.getString(getString(R.string.user_name_key), "");

            if (gameId != null && gameId != ""
                    && userId != null && userId != ""
                    && userName != null && userName != "")
                signalRService.getPlayerScore(gameId, userId, userName);
        }
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

        gameTitle.setText(playerScore.getGameTitle().toString());
        score.setText(playerScore.getScore().toString());
        position.setText(playerScore.getPosition().toString());
        totalPlayers.setText(playerScore.getTotalPlayers().toString());
//        leaderScore.setText(playerScore.getLeaderScore().toString());

        totalOpenQuestions.setText(playerScore.getTotalOpenQuestions().toString());
    }


    //to handle question events
//    private QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(context);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveQuestion(QuestionEvent questionEvent) {
        Question question =  questionEvent.getQuestion();
        String questionState = questionEvent.getQuestionState();

        //for admin///////////////////////////////

        adminQuestion = question;


        ////////////////////////////////////////


        // default is NEW_QUESTION
        Class<?> activityClass = AnswerQuestionActivity.class;

        //PROCESSED_QUESTION
        if (questionState.equals(QuestionEvent.QuestionStates.PROCESSED_QUESTION))
            activityClass = ViewProcessedQuestionActivity.class;
        //CLOSED_QUESTION
        else if (questionState.equals(QuestionEvent.QuestionStates.CLOSED_QUESTION))
            activityClass = ViewClosedQuestionActivity.class;

        Intent intent = new Intent(context, activityClass);
        Bundle bundle = new Bundle();
        bundle.putSerializable("question", question);
        bundle.putString("questionState", questionState);
        intent.putExtras(bundle);
        startActivity(intent);




    }
    //</editor-fold>

    //<editor-fold desc="temp code">
    static Question adminQuestion;
    //static String questionState;



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //inflates the menu; this addsitems to the action bar if it is exist
        getMenuInflater().inflate(R.menu.admin_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean handled = true;
        int id = item.getItemId();

        switch (id) {
            case R.id.action_askQuestion:
                onClickMenuAskQuestion(item);
                break;
            case R.id.action_closeQuestion:
                onClickMenuCloseQuestion(item);
                break;
            default:
                handled = super.onOptionsItemSelected(item);
        }

        return handled;

    }

    void onClickMenuAskQuestion(MenuItem item) {


        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String gameId = settings.getString(getString(R.string.game_id_key), "");

        Answer[] answers = new Answer[]{
                new Answer(null, "Eran", 0.5, 300, false, false),
                new Answer(null, "Eran2", 0.5, 300, false, false),
                new Answer(null, "Eran3", 0.5, 300, false, false),
                new Answer(null, "Eran4", 0.5, 300, false, false)

        };
        adminQuestion = new Question("who are you", answers, gameId, true);
        signalRService.adminAskQuestion(adminQuestion);

    }

    void onClickMenuCloseQuestion(MenuItem item) {

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String gameId = settings.getString(getString(R.string.game_id_key), "");

        String questionId = adminQuestion.getId();
        String correctAnswerId = adminQuestion.getAnswers()[2].getId();


        signalRService.adminCloseQuestion(gameId, questionId ,correctAnswerId);


    }

    //</editor-fold>


}
