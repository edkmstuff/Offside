package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.RewardEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.Player;
import com.offsidegame.offside.models.Position;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


    private final Context context = this;
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
    private ListView chatListView;

    String privateGameTitle;
    String homeTeam;
    String awayTeam;
    int offsideCoins;

    private Player player;


    private TextView scoreTextView;
    private TextView privateGameNameTextView;
    private TextView gameTitleTextView;
    private TextView positionTextView;

    private LinearLayout actionsMenuRoot;
    private RelativeLayout contentRoot;
    private TextView chatActionsButton;

    private boolean isConnected = false;
    private boolean isActionMenuVisible = false;

    private RewardedVideoAd rewardedVideoAd;
    private LinearLayout rewardVideoLoadingRoot;
    private LinearLayout actionExitGameRoot;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);

            gameId = OffsideApplication.getGameInfo().getGameId();
            gameCode = OffsideApplication.getGameInfo().getPrivateGameCode();
            FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
            playerId = player.getUid();

            privateGameTitle = OffsideApplication.getGameInfo().getPrivateGameTitle();
            homeTeam = OffsideApplication.getGameInfo().getHomeTeam();
            awayTeam = OffsideApplication.getGameInfo().getAwayTeam();
            offsideCoins = OffsideApplication.getGameInfo().getOffsideCoins();


            //<editor-fold desc="FindById">

            root = (LinearLayout) findViewById(R.id.c_root);
            chatListView = (ListView) findViewById(R.id.c_chat_list_view);

            chatSendTextView = (TextView) findViewById(R.id.c_chatSendTextView);
            chatMessageEditText = (EditText) findViewById(R.id.c_chat_message_edit_text);
            scoreTextView = (TextView) findViewById(R.id.c_score_text_view);
            privateGameNameTextView = (TextView) findViewById(R.id.c_private_game_name_text_view);
            gameTitleTextView = (TextView) findViewById(R.id.c_game_title_text_view);
            positionTextView = (TextView) findViewById(R.id.c_position_text_view);

            contentRoot = (RelativeLayout) findViewById(R.id.c_content_root);
            actionsMenuRoot = (LinearLayout) findViewById(R.id.c_actions_menu_root);
            actionsMenuRoot.setScaleX(0f);
            actionsMenuRoot.setScaleY(0f);
            actionsMenuRoot.setAlpha(0.99f);
            chatActionsButton = (TextView) findViewById(R.id.c_chatActionsButton);
            privateGameNameTextView.setText(privateGameTitle);
            gameTitleTextView.setText(homeTeam + " vs. " + awayTeam);

            loadRewardedVideoAd();
            rewardVideoLoadingRoot = (LinearLayout) findViewById(R.id.c_reward_video_loading_root);
            rewardVideoLoadingRoot.setVisibility(View.GONE);

            actionExitGameRoot = (LinearLayout) findViewById(R.id.c_action_exit_game_root);


            chatSendTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    if (!isConnected)
//                        return;

                    String message = chatMessageEditText.getText().toString();
                    if (message != null && message.length() > 0) {
                        OffsideApplication.signalRService.sendChatMessage(gameId, gameCode, message, playerId);
                        //clear text
                        chatMessageEditText.setText("");
                        //hide keypad
                        hideKeypad();
                    }


                }
            });

            chatActionsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

//                    if (!isConnected)
//                        return;

                    hideKeypad();
                    if (isActionMenuVisible)
                        actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
                    else
                        actionsMenuRoot.animate().scaleX(1f).scaleY(1f);

                    isActionMenuVisible = !isActionMenuVisible;

                }
            });


            chatListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    hideKeypad();
                }
            });

            root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //hide keypad
                    hideKeypad();
                }


            });

            //<editor-fold desc="ACTIONS SHORT VERSION">

            final LinearLayout actionLeadersRoot = (LinearLayout) findViewById(R.id.c_action_leaders_root);
            final LinearLayout actionCurrentQuestionRoot = (LinearLayout) findViewById(R.id.c_action_current_question_root);
            final LinearLayout actionOffsideCoinsRoot = (LinearLayout) findViewById(R.id.c_action_offside_coins_root);
            final LinearLayout actionReloadRoot = (LinearLayout) findViewById(R.id.c_action_reload_root);
            final LinearLayout actionCodeRoot = (LinearLayout) findViewById(R.id.c_action_code_root);
            final LinearLayout actionShareRoot = (LinearLayout) findViewById(R.id.c_action_share_root);
            final LinearLayout actionWatchVideoRoot = (LinearLayout) findViewById(R.id.c_action_watch_video_root);


            Map<String, LinearLayout> actionButtons = new HashMap<String, LinearLayout>() {
                {
                    put("!leaders", actionLeadersRoot);
                    put("!question", actionCurrentQuestionRoot);
                    put("!coins", actionOffsideCoinsRoot);
                    put("!reload", actionReloadRoot);
                    put("!code", actionCodeRoot);
                    put("!share", actionShareRoot);

                }
            };

            for (String action : actionButtons.keySet()) {

                final String command = action;
                final LinearLayout actionElement = actionButtons.get(action);

                actionElement.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        try {
                            if (!isActionMenuVisible)
                                return;

                            if (command == "!share") {

                                PackageManager pm = context.getPackageManager();
                                boolean isWhatsappInstalled = isPackageInstalled("com.whatsapp", pm);
                                String shareMessage = "Yo! I am *Offsiding* with the gang, come join us using this code:   *" + gameCode + "*";
                                Intent sendIntent = new Intent();
                                sendIntent.setAction(Intent.ACTION_SEND);
                                sendIntent.setType("text/plain");
                                if (isWhatsappInstalled) {
                                    sendIntent.setPackage("com.whatsapp");
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                                } else {
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, shareMessage.replaceAll("[*]", ""));
                                }

                                startActivity(sendIntent);

                            } else {
                                OffsideApplication.signalRService.sendChatMessage(gameId, gameCode, command, playerId);
                            }

                            chatActionsButton.performClick();

                        } catch (Exception ex) {
                            ACRA.getErrorReporter().handleSilentException(ex);
                            chatActionsButton.performClick();
                        }


                    }
                });

            }

            //<editor-fold desc="VIDEO AD">

            //prepare watch video objects

            // Use an activity context to get the rewarded video instance.
            rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
            rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

                //<editor-fold desc="RewardedVideoAdListener Methods">
                @Override
                public void onRewarded(RewardItem reward) {

                    int rewardAmount = reward.getAmount();
                    //int currentOffsideCoinsValue = OffsideApplication.getOffsideCoins();


                    //Toast.makeText(context, "onRewarded! currency: " + reward.getType() + "  amount: " +  reward.getAmount(), Toast.LENGTH_SHORT).show();

                    EventBus.getDefault().post(new RewardEvent(rewardAmount));

                }

                @Override
                public void onRewardedVideoAdLeftApplication() {

                   // Toast.makeText(context, "onRewardedVideoAdLeftApplication",Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    //Toast.makeText(context, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();

                    rewardedVideoAd = null;


                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int errorCode) {
                    //Toast.makeText(context, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
                    rewardedVideoAd = null;

                }

                @Override
                public void onRewardedVideoAdLoaded() {
                    try {

                        if (rewardedVideoAd.isLoaded()){
                            rewardVideoLoadingRoot.setVisibility(View.VISIBLE);
                            rewardedVideoAd.show();
                        }

                        //Toast.makeText(context, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();

                    } catch (Exception ex) {

                        ACRA.getErrorReporter().handleException(ex);

                    }

                }

                @Override
                public void onRewardedVideoAdOpened() {
                    //Toast.makeText(context, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }

                @Override
                public void onRewardedVideoStarted() {
                    //Toast.makeText(context, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
                }
                //</editor-fold>

            });

            actionWatchVideoRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    rewardVideoLoadingRoot.setVisibility(View.VISIBLE);
                    loadRewardedVideoAd();
                    //chatActionsButton.performClick();


                }
            });
            //</editor-fold>

            //<editor-fold desc="exit game">
            actionExitGameRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OffsideApplication.signalRService.quitGame(gameId,playerId);
                    chatActionsButton.performClick();

                    SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                    SharedPreferences.Editor editor = settings.edit();

                    editor.putString(getString(R.string.game_id_key), null);
                    editor.putString(getString(R.string.game_code_key), null);
                    editor.putString(getString(R.string.private_game_title_key), null);
                    editor.putString(getString(R.string.home_team_key), null);
                    editor.putString(getString(R.string.away_team_key), null);



                    editor.commit();
                    Intent intent = new Intent(context,JoinGameActivity.class);
                    startActivity(intent);
                }
            });


            //</editor-fold>


        //</editor-fold>

    //</editor-fold>

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void loadRewardedVideoAd() {
        try {

            String rewardedVideoAdAppUnitId = context.getString(R.string.rewarded_video_ad_unit_id_key);

            if (!rewardedVideoAd.isLoaded())
                rewardedVideoAd.loadAd(rewardedVideoAdAppUnitId, new AdRequest.Builder().build());

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onResume() {

        try {
            super.onResume();

            hideKeypad();

            //reset to chat adapter
            createNewChatAdapter(true);

            EventBus.getDefault().post(new SignalRServiceBoundEvent(context));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Override
    public void onStop() {

        try {
            EventBus.getDefault().unregister(context);
            super.onStop();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void hideKeypad() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        isConnected = connectionEvent.getConnected();

        if (isConnected) {
            Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
            chatSendTextView.setAlpha(1f);
            chatActionsButton.setAlpha(1f);

        } else {
            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
            chatSendTextView.setAlpha(0.4f);
            chatActionsButton.setAlpha(0.4f);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {

        Context eventContext = signalRServiceBoundEvent.getContext();

        if (eventContext == context || eventContext == getApplicationContext()) {

            if (gameId != null && !gameId.isEmpty() && gameCode != null && !gameCode.isEmpty() && playerId != null && !playerId.isEmpty()) {
                OffsideApplication.signalRService.getChatMessages(gameId, gameCode, playerId);

            } else {
                Intent intent = new Intent(context, JoinGameActivity.class);
                context.startActivity(intent);
            }

        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChat(ChatEvent chatEvent) {

        try {

            chat = new Chat(chatEvent.getChat());


            EventBus.getDefault().post(new PositionEvent(chat.getPosition()));

            player = chat.getPlayer();
            if (player == null)
                return;
            playerAnswers = player.getPlayerAnswers();

            scoreTextView.setText(String.valueOf((int) player.getPoints()));

            createNewChatAdapter(false);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessage(ChatMessageEvent chatMessageEvent) {

        try {
            ChatMessage message = chatMessageEvent.getChatMessage();

            boolean isAdded = chat.addMessageIfNotAlreadyExists(message);

            if (!isAdded) {
                throw new Exception("Duplicate chat message. id: " + message.getId() + " content: " + message.getMessageText());
            }

            //new message was added, notify data change
            if (messages != null && chatMessageAdapter != null) {
                chatMessageAdapter.notifyDataSetChanged();
                return;
            }


            createNewChatAdapter(false);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void createNewChatAdapter(boolean reset) {
        messages = new ArrayList();

        if (!reset && chat != null)
            messages = chat.getChatMessages();

        chatMessageAdapter = new ChatMessageAdapter(context, messages, playerAnswers);
        ListView chatListView = (ListView) findViewById(R.id.c_chat_list_view);
        chatListView.setAdapter(chatMessageAdapter);
    }

    private boolean isPackageInstalled(String packagename, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packagename, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnsweredEvent) {

        try {
            String gameId = questionAnsweredEvent.getGameId();
            String questionId = questionAnsweredEvent.getQuestionId();
            boolean isRandomAnswer = questionAnsweredEvent.isRandomAnswer();
            int betSize = questionAnsweredEvent.getBetSize();

            // this parameter will be null if the user does not answer
            String answerId = questionAnsweredEvent.getAnswerId();
            OffsideApplication.signalRService.postAnswer(gameId, questionId, answerId, isRandomAnswer, betSize);
            if (!playerAnswers.containsKey(questionId))
                playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomAnswer, betSize, true));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePosition(PositionEvent positionEvent) {

        try {
            Position position = positionEvent.getPosition();
            String positionDisplay = Integer.toString(position.getPrivateGamePosition()) + "/" + Integer.toString(position.getPrivateGameTotalPlayers());
            positionTextView.setText(positionDisplay);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(Player player) {
        try {
            if (player == null)
                return;

            this.player = player;
            this.playerAnswers = player.getPlayerAnswers();
            scoreTextView.setText(Integer.toString((int) player.getPoints()));
            OffsideApplication.setOffsideCoins(player.getOffsideCoins());

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveRewardEvent(RewardEvent rewardEvent) {
        try {
            int rewardAmount = rewardEvent.getRewardAmount();
            if (player == null || rewardAmount==0)
                return;
            int updatedOffsideCoins = player.getOffsideCoins()+rewardAmount;

//            player.setOffsideCoins(updatedOffsideCoins);
//            OffsideApplication.setOffsideCoins(updatedOffsideCoins);
            OffsideApplication.signalRService.setOffsideCoins(gameId, playerId, updatedOffsideCoins);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveOffsideCoinsUpdated(Integer newOffsideCoinsValue) {
        try {
            if (player == null)
                return;

            player.setOffsideCoins(newOffsideCoinsValue);
            OffsideApplication.setOffsideCoins(newOffsideCoinsValue);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }





}
