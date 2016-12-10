package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.facebook.login.widget.ProfilePictureView;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.ActiveGameEvent;
import com.offsidegame.offside.models.GameCreationEvent;
import com.offsidegame.offside.models.JoinGameEvent;
import com.offsidegame.offside.models.SignalRServiceBoundEvent;

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
    Button createGame;
    TextView fbName;
    ProfilePictureView fbProfilePicture;

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

        if(Profile.getCurrentProfile()==null){
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getString(R.string.is_logged_in_key), false);
            editor.commit();
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            startActivity(intent);
            return;
        }
        else{
            Intent intent = new Intent();
            intent.setClass(context, SignalRService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

            fbName = (TextView) findViewById(R.id.fbNameTextView);
            fbProfilePicture = (ProfilePictureView) findViewById(R.id.fbPictureImageView);

            fbName.setText(Profile.getCurrentProfile().getName());
            fbProfilePicture.setProfileId(Profile.getCurrentProfile().getId());

            gameCode = (EditText) findViewById(R.id.gameCode);
            join = (Button) findViewById(R.id.join_button);
            createGame = (Button) findViewById(R.id.create_game_button);
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isBoundToSignalRService) {
                        signalRService.joinGame(gameCode.getText().toString());
                    }
                }
            });

            createGame.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (isBoundToSignalRService) {
                        signalRService.adminCreateGame("1234", "Barcelona", "Real Madrid");
                    }
                }
            });
        }

        /*FacebookLoginInfo fbLoginInfo;

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        Gson gson = new Gson();
        String fbProfileJson = settings.getString(getString(R.string.fbProfile_key), "");
        if(fbProfileJson!=""){
            fbLoginInfo= gson.fromJson(fbProfileJson, FacebookLoginInfo.class);
        }
        else {
            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean(getString(R.string.is_logged_in_key), false);
            editor.commit();
            Intent intent = new Intent();
            intent.setClass(context, LoginActivity.class);
            startActivity(intent);
            return;

        }*/



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
        String gameId = joinGameEvent.getGameId();
        if (gameId == null) {
            Toast.makeText(context, "No such game code", Toast.LENGTH_LONG).show();
            return;
        }
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(getString(R.string.game_id_key), gameId);
        editor.commit();

        Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
        startActivity(intent);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        Context eventContext = signalRServiceBoundEvent.getContext();
        if (eventContext == context) {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            String gameId = settings.getString(getString(R.string.game_id_key), "");

            signalRService.isGameActive(gameId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveIsGameActive(ActiveGameEvent activeGameEvent) {
        Boolean isGameActive = activeGameEvent.getIsGameActive();

        if(isGameActive){
            Intent intent = new Intent(context, ViewPlayerScoreActivity.class);
            startActivity(intent);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveIsGameCreated(GameCreationEvent gameCreationEvent) {
        String newGameCode = gameCreationEvent.getGameCode();
        gameCode.setText(newGameCode);
    }


}
