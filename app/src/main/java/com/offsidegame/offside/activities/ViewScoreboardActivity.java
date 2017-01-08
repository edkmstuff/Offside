package com.offsidegame.offside.activities;

import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;

import com.offsidegame.offside.activities.fragments.ScoresFragment;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewScoreboardActivity extends AppCompatActivity {

    private final Context context = this;
    private Scoreboard scoreboard;
    private TextView gameDidNotStartYet;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
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
    private String position;
    private String totalPlayers;
    private TextView positionTextView;
    private TextView totalPlayersTextView;
    private LinearLayout positionRoot;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_scoreboard);
        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);





        gameDidNotStartYet = (TextView) findViewById(R.id.sb_game_did_not_start_yet_text_view);
        gameDidNotStartYet.setVisibility(View.GONE);
        positionRoot = (LinearLayout) findViewById(R.id.sb_position_root);
        positionRoot.setVisibility(View.GONE);
        Intent intent = getIntent();
        position = intent.getStringExtra("position");
        totalPlayers = intent.getStringExtra("totalPlayers");
        positionTextView = (TextView) findViewById(R.id.sb_position_text_view);
        totalPlayersTextView = (TextView) findViewById(R.id.sb_total_players_text_view);
        positionTextView.setText(position);
        totalPlayersTextView.setText(totalPlayers);



    }

    @Override
    public void onResume() {
        super.onResume();

        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStart() {
        super.onStart();
        questionEventsHandler.register();
        EventBus.getDefault().register(context);
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

        super.onStop();
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
            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            String gameId = settings.getString(getString(R.string.game_id_key), "");


            if (gameId != null && !gameId.isEmpty())
                signalRService.getScoreboard(gameId);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveScoreboard(ScoreboardEvent scoreboardEvent) {
        scoreboard = scoreboardEvent.getScoreboard();
        Score[] scores = scoreboard.getScores();
        if (scores == null || scores.length < 1){
            gameDidNotStartYet.setVisibility(View.VISIBLE);
        }else{
            positionRoot.setVisibility(View.VISIBLE);
        }

        ScoresFragment scoresFragment = (ScoresFragment) getSupportFragmentManager().findFragmentById(R.id.scores_fragment);
        scoresFragment.updateData(scoreboard);


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
