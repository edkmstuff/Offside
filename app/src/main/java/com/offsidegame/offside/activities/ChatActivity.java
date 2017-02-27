package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;

public class ChatActivity extends AppCompatActivity {


    private final Context context = this;

    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private ImageView profilePictureImageView;


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
        setContentView(R.layout.activity_chat);

        questionEventsHandler.register();
        EventBus.getDefault().register(context);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

//        profilePictureImageView = (ImageView) findViewById(R.id.fbPictureImageView);
//
//        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
//        String userPictureUrl = settings.getString(getString(R.string.user_profile_picture_url_key), "");
//        Uri fbImageUrl = Uri.parse(userPictureUrl);
//
//        Picasso.with(context).load(fbImageUrl).into(profilePictureImageView, new com.squareup.picasso.Callback() {
//            @Override
//            public void onSuccess() {
//                Bitmap bm = ((BitmapDrawable) profilePictureImageView.getDrawable()).getBitmap();
//                RoundImage roundedImage = new RoundImage(bm);
//                profilePictureImageView.setImageDrawable(roundedImage);
//            }
//
//            @Override
//            public void onError() {
//
//            }
//        });

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
                Intent intent = new Intent(context, JoinGameActivity.class);
                context.startActivity(intent);
                return;
            }


        if (eventContext == context) {
            SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
            String gameId = settings.getString(getString(R.string.game_id_key), "");
            String gameCode = settings.getString(getString(R.string.game_code_key), "");


            if (gameId != null && !gameId.isEmpty())
                signalRService.getChatMessages(gameId, gameCode);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChat(ChatEvent chatEvent) {

        Chat chat = chatEvent.getChat();

        ArrayList messages = new ArrayList(Arrays.asList(chat.getChatMessages()));
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(context, messages);

        ListView chatListView = (ListView)findViewById(R.id.p_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);


    }
    //ToDo: delete sometime
    public void dummyChatMessages() {

        int j=10;
        ChatMessage [] chatMessages = new ChatMessage[j];

        String[] msgs = new String []{
                "Hi kfir , you are super gay",
                "Hi Eran, I know what s up",
                " Hi As usual, we use ViewHolder pattern for efficiency when recreating each items in the Listview. LayoutParams are for designing the Layout left or right aligned according to the Chat Message Send or Received. A dummy boolean value is used as the property to check whether ",
                " Hi  we use ViewHolder pattern for efficiency when recreating each items in the Listview. LayoutParams are for designing the Layout left or right al"
        };

        for(int i=0;i<j;i++){

            ChatMessage chat = new ChatMessage(msgs[i%4],i%3==0);
            chatMessages[i] = chat;

        }

        ArrayList messages = new ArrayList(Arrays.asList(chatMessages));
        ChatMessageAdapter chatMessageAdapter = new ChatMessageAdapter(context, messages);

        ListView chatListView = (ListView)findViewById(R.id.p_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);
    }



}
