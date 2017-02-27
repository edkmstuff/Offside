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
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.fragments.ScoresFragment;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class PlayActivity extends AppCompatActivity {


    private final Context context = this;

    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
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
        setContentView(R.layout.activity_play);

        int j=50;
        ChatMessage [] chatMessages = new ChatMessage[j];
        String[] msgs = new String []{
                "Hi kfir , you are super gay",
                "Hi Eran, I know what s up",
                " Hi As usual, we use ViewHolder pattern for efficiency when recreating each items in the Listview. LayoutParams are for designing the Layout left or right aligned according to the Chat Message Send or Received. A dummy boolean value is used as the property to check whether ",
                " Hi  we use ViewHolder pattern for efficiency when recreating each items in the Listview. LayoutParams are for designing the Layout left or right al"
        };

        for(int i=0;i<j;i++){

            ChatMessage chat = new ChatMessage( "--" + i +"--" +msgs[i%4],i%3==0);
            chatMessages[i] = chat;

        }

        ArrayList messages = new ArrayList(Arrays.asList(chatMessages));
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(context, messages);

        ListView chatListView = (ListView)findViewById(R.id.p_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);

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
//                Intent intent = new Intent(context, JoinGameActivity.class);
//                context.startActivity(intent);
                return;
            }


        if (eventContext == context) {
//            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
//            String gameId = settings.getString(getString(R.string.game_id_key), "");
//
//
//            if (gameId != null && !gameId.isEmpty())
//                signalRService.getScoreboard(gameId);
        }
    }

}
