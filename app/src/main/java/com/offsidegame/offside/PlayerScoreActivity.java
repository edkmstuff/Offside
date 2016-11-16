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
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.PlayerScore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class PlayerScoreActivity extends AppCompatActivity {
    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;

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


    TextView score;
    TextView position;
    TextView leaderScore;
    TextView totalOpenQuestions;



    /**
     * Defines callbacks for service binding, passed to bindService()
     */

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

        score = (TextView) findViewById(R.id.score);
        position = (TextView) findViewById(R.id.position);
        leaderScore = (TextView) findViewById(R.id.leader_score);
        totalOpenQuestions = (TextView) findViewById(R.id.total_active_questions);


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(mContext);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(mContext);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        super.onStop();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayerScore(PlayerScore playerScore) {
        updatePlayerScoreInUi(playerScore);
        Toast.makeText(mContext, getString(R.string.data_updated), Toast.LENGTH_SHORT).show();
    }

    void updatePlayerScoreInUi(PlayerScore playerScore) {
        score.setText(Integer.toString(playerScore.getScore()));
        //player position
        position.setText(Integer.toString(playerScore.getPosition()) + " " + getString(R.string.out_of) + " " + Integer.toString(playerScore.getTotalPlayers()));
        //leader score
        leaderScore.setText(Integer.toString(playerScore.getLeaderScore()));
        //open questions
        totalOpenQuestions.setText(Integer.toString(playerScore.getTotalOpenQuestions()));
    }


}
