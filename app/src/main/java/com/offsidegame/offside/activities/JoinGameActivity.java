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
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Profile;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.events.ActiveGameEvent;
import com.offsidegame.offside.events.GameCreationEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
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
    Button createGame;
    TextView userName;
    ImageView profilePicture;

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
            Intent intent = new Intent();
            intent.setClass(context, SignalRService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

            userName = (TextView) findViewById(R.id.fbNameTextView);
            userName.setText(settings.getString(getString(R.string.user_name_key),""));

            profilePicture = (ImageView) findViewById(R.id.userPictureImageView);
            String useProfilePictureUrl = settings.getString(getString(R.string.user_profile_picture_url_key),"");

            Picasso.with(context).load(useProfilePictureUrl).into(profilePicture, new com.squareup.picasso.Callback() {
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
                    }
                }
            });


       // }

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


}
