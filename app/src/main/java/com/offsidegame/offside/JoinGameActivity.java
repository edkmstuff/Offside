package com.offsidegame.offside;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.JoinGameEvent;
import com.offsidegame.offside.models.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class JoinGameActivity extends AppCompatActivity {

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };


    EditText gameCode;
    Button join;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_game);


        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

        gameCode = (EditText) findViewById(R.id.gameCode);
        join = (Button) findViewById(R.id.join_button);
        join.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isBoundToSignalRService){
                    signalRService.joinGame(gameCode.getText().toString());
                }
            }

        });




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
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString(getString(R.string.game_id_key), gameId);
        editor.commit();

        Intent intent = new Intent(context, PlayerScoreActivity.class);
        startActivity(intent);
    }
}
