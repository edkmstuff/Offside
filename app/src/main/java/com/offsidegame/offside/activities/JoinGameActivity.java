package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.events.ActiveGameEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.GameInfo;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;

public class JoinGameActivity extends AppCompatActivity implements  Serializable{

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;

    EditText gameCode;
    Button join;
    //Button createGame;
    TextView userName;
    ImageView profilePicture;
    private LinearLayout joinGameRoot;
    private LinearLayout loadingGameRoot;

    private Toolbar toolbar;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);

        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);

//        if(Profile.getCurrentProfile()==null){
//            SharedPreferences.Editor editor = settings.edit();
//            editor.putBoolean(getString(R.string.is_logged_in_key), false);
//            editor.commit();
//            Intent intent = new Intent();
//            intent.setClass(context, LoginActivity.class);
//            startActivity(intent);
//            return;
//        }
//        else{


            userName = (TextView) findViewById(R.id.join_game_user_name_text_view);
            userName.setText(settings.getString(getString(R.string.user_name_key),""));

            profilePicture = (ImageView) findViewById(R.id.userPictureImageView);
            final String userPictureUrl = settings.getString(getString(R.string.user_profile_picture_url_key),"");

            Picasso.with(context).load(userPictureUrl).into(profilePicture, new com.squareup.picasso.Callback() {
                @Override
                public void onSuccess() {
                    Bitmap bm = ((BitmapDrawable) profilePicture.getDrawable()).getBitmap();
                    RoundImage roundedImage = new RoundImage(bm);
                    profilePicture.setImageDrawable(roundedImage);
                }

                @Override
                public void onError() {

                }
            });


            gameCode = (EditText) findViewById(R.id.gameCode);
            join = (Button) findViewById(R.id.join_button);

            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isBoundToSignalRService) {
                        signalRService.joinGame(gameCode.getText().toString());
                        loadingGameRoot.setVisibility(View.VISIBLE);
                        joinGameRoot.setVisibility(View.GONE);

                    }
                }
            });


        joinGameRoot = (LinearLayout) findViewById(R.id.jg_join_game_root);
        loadingGameRoot = (LinearLayout) findViewById(R.id.jg_loading_root);

        loadingGameRoot.setVisibility(View.VISIBLE);
        joinGameRoot.setVisibility(View.GONE);

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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onJoinGame(JoinGameEvent joinGameEvent) {
        GameInfo gameInfo = joinGameEvent.getGameInfo();
        if (gameInfo == null || gameInfo.getGameId() == null){
            Toast.makeText(context, R.string.lbl_no_such_game, Toast.LENGTH_LONG).show();
            loadingGameRoot.setVisibility(View.GONE);
            joinGameRoot.setVisibility(View.VISIBLE);
            return;
        }
        String gameId = gameInfo.getGameId();
        int timeToGoBackToPlayerScore = gameInfo.getTimeToGoBackToPlayerScore();
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(getString(R.string.game_id_key), gameId);
        editor.putInt(getString(R.string.time_to_go_back_to_player_score_key),timeToGoBackToPlayerScore);


        editor.commit();

        Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
        startActivity(intent);
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


        if (eventContext == null){
                Intent intent = new Intent(context, JoinGameActivity.class);
                context.startActivity(intent);
                return;
        }

        if (eventContext == context) {
            Intent intent = getIntent();
            String gameCodeFromNotification = intent.getExtras().getString("gameCodeFromNotification");
            if(!(gameCodeFromNotification.equals("") || gameCodeFromNotification ==null)){

                signalRService.joinGame(gameCodeFromNotification.toString());
                loadingGameRoot.setVisibility(View.VISIBLE);
                joinGameRoot.setVisibility(View.GONE);

            }
            else
            {
                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                String gameId = settings.getString(getString(R.string.game_id_key), "");
                signalRService.isGameActive(gameId);
            }


        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveIsGameActive(ActiveGameEvent activeGameEvent) {
        Boolean isGameActive = activeGameEvent.getIsGameActive();

        if(isGameActive){
            Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
            startActivity(intent);
        }else{
            loadingGameRoot.setVisibility(View.GONE);
            joinGameRoot.setVisibility(View.VISIBLE);
        }
    }


}
