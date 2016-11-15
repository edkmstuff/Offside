package com.offsidegame.offside;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.offsidegame.offside.helpers.PlayerScoreEvent;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.helpers.SignalrClient;
import com.offsidegame.offside.models.PlayerScore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class PlayerScoreActivity extends AppCompatActivity {
    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;


    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private final ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_score);

        Button btn = (Button) findViewById(R.id.getsig);
        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                mService.getPlayerScore();
            }

        });

        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        super.onStop();
    }


    @Subscribe
    public void onPlayerScoreEvent(PlayerScoreEvent event) {
        PlayerScore playerScore = event.playerScore;

        //ToDo:now update the ui


    }


}
