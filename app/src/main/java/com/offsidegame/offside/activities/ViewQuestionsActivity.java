package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.fragments.QuestionsFragment;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.QuestionsEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ViewQuestionsActivity extends AppCompatActivity {

    private final Context context = this;
    private Question[] questions;
    private SignalRService signalRService;
    private TextView gameDidNotStartYetTextView;
    private boolean isBoundToSignalRService = false;
    private Toolbar toolbar;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    public final ServiceConnection signalRServiceConnection = new ServiceConnection() {
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
        setContentView(R.layout.activity_view_questions);

        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        gameDidNotStartYetTextView = (TextView) findViewById(R.id.game_did_not_start_yet_text_view);
        gameDidNotStartYetTextView.setVisibility(View.GONE);


    }

    @Override
    public void onResume() {
        super.onResume();
        questionEventsHandler.register();
        EventBus.getDefault().register(context);
        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

    }

    @Override
    public void onStart() {
        super.onStart();

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
                signalRService.getQuestions(gameId);
        }
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
    public void onReceiveQuestions(QuestionsEvent questionsEvent) {
        questions = questionsEvent.getQuestions();
        if (questions == null || questions.length < 1){
            gameDidNotStartYetTextView.setVisibility(View.VISIBLE);
            return;
        }

        QuestionsFragment questionsFragment = (QuestionsFragment) getSupportFragmentManager().findFragmentById(R.id.questions_fragment);
        questionsFragment.updateData(questions);


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
