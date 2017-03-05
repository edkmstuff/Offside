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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.QuestionEventsHandler;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private final Context context = this;

    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    private final QuestionEventsHandler questionEventsHandler = new QuestionEventsHandler(this);
    private TextView chatSendTextView;
    private EditText chatMessageEditText;
    private String gameId;
    private String gameCode;
    private String playerId;
    private Chat chat;
    private ArrayList messages;
    private ChatMessageAdapter chatMessageAdapter;
    private Map<String, AnswerIdentifier> playerAnswers;
    private LinearLayout root;

    private boolean isBatch = false;


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

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        gameId = settings.getString(getString(R.string.game_id_key), "");
        gameCode = settings.getString(getString(R.string.game_code_key), "");
        playerId = settings.getString(getString(R.string.user_id_key), "");

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        root = (LinearLayout) findViewById(R.id.c_root);
        chatSendTextView = (TextView) findViewById(R.id.c_chatSendTextView);
        chatMessageEditText = (EditText) findViewById(R.id.c_chat_message_edit_text);
        chatSendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatMessageEditText.getText().toString();
                signalRService.sendChatMessage(gameId, gameCode, message, playerId);
                //clear text
                chatMessageEditText.setText("");
                //hide keypad
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

            }
        });


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

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

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


        if (eventContext == null) {
            Intent intent = new Intent(context, JoinGameActivity.class);
            context.startActivity(intent);
            return;
        }


        if (eventContext == context) {

            if (gameId != null && !gameId.isEmpty())
                signalRService.getChatMessages(gameId, gameCode,playerId );
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChat(ChatEvent chatEvent) {

        chat = chatEvent.getChat();
        messages = new ArrayList(Arrays.asList(chat.getChatMessages()));
        playerAnswers = chat.getPlayer()!=null ? chat.getPlayer().getPlayerAnswers(): null ;

        chatMessageAdapter = new ChatMessageAdapter(context, messages, playerAnswers);
        ListView chatListView = (ListView) findViewById(R.id.c_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessage(ChatMessageEvent chatMessageEvent) {

        ChatMessage message = chatMessageEvent.getChatMessage();
        chat.addMessage(message);

        if (messages != null && chatMessageAdapter != null) {
            messages.add(message);
            chatMessageAdapter.notifyDataSetChanged();
            return;
        }

        messages = new ArrayList(Arrays.asList(chat.getChatMessages()));
        chatMessageAdapter = new ChatMessageAdapter(context, messages, playerAnswers);
        ListView chatListView = (ListView) findViewById(R.id.c_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnswered) {
        String gameId = questionAnswered.getGameId();
        String questionId = questionAnswered.getQuestionId();
        boolean isRandomAnswer = questionAnswered.isRandomAnswer();
        int betSize = questionAnswered.getBetSize();

        // this parameter will be null if the user does not answer
        String answerId = questionAnswered.getAnswerId();
        signalRService.postAnswer(gameId, questionId, answerId, isRandomAnswer, betSize );
        if (!playerAnswers.containsKey(questionId))
            playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomAnswer, betSize));

//        if (!isBatch) {
//            calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
//            questionAndAnswersRoot.setVisibility(View.GONE);
//        } else {
//            if (batchedQuestionsQueue.isEmpty()) {
//                calcQuestionStatisticsRoot.setVisibility(View.VISIBLE);
//                questionAndAnswersRoot.setVisibility(View.GONE);
//            } else {
//                question = batchedQuestionsQueue.remove();
//                showQuestion();
//            }
//        }

    }



}
