package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.model.SharePhoto;
import com.facebook.share.model.SharePhotoContent;
import com.facebook.share.widget.ShareButton;
import com.facebook.share.widget.ShareDialog;
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
import com.offsidegame.offside.events.IsAnswerAcceptedEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.RewardEvent;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.Player;
import com.offsidegame.offside.models.Position;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;

import org.acra.ACRA;
import org.apmem.tools.layouts.FlowLayout;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {


   //<editor-fold desc="private members">
    private final Context context = this;
    private final Activity thisActivity = this;
    private TextView chatSendTextView;
    private EditText chatMessageEditText;
    private SharedPreferences settings;
    private String gameId;
    private String gameCode;
    private String playerId;
    private Chat chat;
    private ArrayList messages;
    private ChatMessageAdapter chatMessageAdapter;

    private LinearLayout root;
    private ListView chatListView;

    String privateGameTitle;
    String homeTeam;
    String awayTeam;
    int offsideCoins;
    int trophies;
    int powerItems;

    private TextView privateGameNameTextView;
    private TextView gameTitleTextView;
    private TextView positionTextView;

    private LinearLayout actionsMenuRoot;
    private RelativeLayout contentRoot;
    private ImageView chatActionsButton;

    private boolean isConnected = false;
    private boolean isActionMenuVisible = false;

    private RewardedVideoAd rewardedVideoAd;
    private LinearLayout rewardVideoLoadingRoot;
    private TextView loadingVideoTextView;
    private LinearLayout actionExitGameRoot;

    private LinearLayout scoreboardRoot;
    private  String androidDeviceId;

    private TextView trophiesTextView;
    private TextView powerItemsTextView;
    private TextView offsideCoinsTextView;
    private ImageView playerPictureImageView;
    //private ImageView playerPictureImageView1;
    private ImageView offsideCoinsImageView;
    private ImageView trophiesImageView;
    private ImageView powerItemImageView;


    private FlowLayout actionsFlowLayout;

    private ShareButton facebookShareButton;


    //</editor-fold>


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {

            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_chat);
            androidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);

            gameId = OffsideApplication.getGameInfo().getGameId();
            gameCode = OffsideApplication.getGameInfo().getPrivateGameCode();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            playerId = user.getUid();

            privateGameTitle = OffsideApplication.getGameInfo().getPrivateGameTitle();
            homeTeam = OffsideApplication.getGameInfo().getHomeTeam();
            awayTeam = OffsideApplication.getGameInfo().getAwayTeam();
            Player player = OffsideApplication.getGameInfo().getPlayer();
            offsideCoins = player != null? player.getOffsideCoins() : OffsideApplication.getGameInfo().getOffsideCoins();
            trophies = OffsideApplication.getGameInfo().getTrophies();
            powerItems = player.getPowerItems();



            //<editor-fold desc="FindById">

            root = (LinearLayout) findViewById(R.id.c_root);
            chatListView = (ListView) findViewById(R.id.c_chat_list_view);

            chatSendTextView = (TextView) findViewById(R.id.c_chatSendTextView);
            chatMessageEditText = (EditText) findViewById(R.id.c_chat_message_edit_text);
            //scoreTextView = (TextView) findViewById(R.id.c_score_text_view1);
            //scoreTextView1 = (TextView) findViewById(R.id.c_score_text_view1);
            privateGameNameTextView = (TextView) findViewById(R.id.c_private_game_name_text_view);
            gameTitleTextView = (TextView) findViewById(R.id.c_game_title_text_view);
            positionTextView = (TextView) findViewById(R.id.c_position_text_view);
            scoreboardRoot = (LinearLayout) findViewById(R.id.c_scoreboard_root);

            contentRoot = (RelativeLayout) findViewById(R.id.c_content_root);
            actionsMenuRoot = (LinearLayout) findViewById(R.id.c_actions_menu_root);
            actionsMenuRoot.setScaleX(0f);
            actionsMenuRoot.setScaleY(0f);
            actionsMenuRoot.setAlpha(0.99f);
            chatActionsButton = (ImageView) findViewById(R.id.c_chatActionsButton);
            actionsMenuRoot.setVisibility(View.GONE);
            actionsFlowLayout = (FlowLayout) findViewById(R.id.c_actions_flow_layout);
            loadingVideoTextView = (TextView) findViewById(R.id.c_loading_video_text_view);

            privateGameNameTextView.setText(privateGameTitle);
            gameTitleTextView.setText(homeTeam + " vs. " + awayTeam);

            playerPictureImageView = (ImageView) findViewById(R.id.c_player_picture_image_view);
            //playerPictureImageView1 = (ImageView) findViewById(R.id.c_player_picture_image_view1);
            offsideCoinsTextView = (TextView) findViewById(R.id.c_offside_coins_text_view);
            offsideCoinsImageView = (ImageView) findViewById(R.id.c_offside_coins_image_view);
            //trophiesTextView = (TextView) findViewById(R.id.c_trophies_text_view);
            //trophiesImageView = (ImageView) findViewById(R.id.c_trophies_image_view);
            powerItemsTextView = (TextView) findViewById(R.id.c_power_items_text_view);
            powerItemImageView = (ImageView) findViewById(R.id.c_power_item_image_view);

            facebookShareButton =(ShareButton) findViewById(R.id.c_facebook_share_button);

            shareOnFacebook();


            offsideCoinsTextView.setText(Integer.toString(offsideCoins));
            //trophiesTextView.setText(Integer.toString(trophies));
            powerItemsTextView.setText(Integer.toString(powerItems));

            settings = getSharedPreferences(getString(R.string.preference_name), 0);
            ImageHelper.loadImage(thisActivity, player != null? player.getImageUrl(): settings.getString(getString(R.string.player_profile_picture_url_key),null) , playerPictureImageView, "ChatActivity");
            //ImageHelper.loadImage(thisActivity, player != null? player.getImageUrl(): settings.getString(getString(R.string.player_profile_picture_url_key),null) , playerPictureImageView1, "ChatActivity");

            actionExitGameRoot = (LinearLayout) findViewById(R.id.c_action_exit_game_root);



            chatMessageEditText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isActionMenuVisible)
                    {
                        actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
                        actionsMenuRoot.setVisibility(View.INVISIBLE);

                    }

                }
            });


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

                    hideKeypad();
                    if (isActionMenuVisible)
                    {
                        actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
                        actionsMenuRoot.setVisibility(View.INVISIBLE);

                    }
                    else
                    {
                        actionsMenuRoot.setVisibility(View.VISIBLE);
                        actionsMenuRoot.animate().scaleX(1f).scaleY(1f);
                    }

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


            final Map<String, LinearLayout> actionButtons = new HashMap<String, LinearLayout>() {
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

                            if (command.equals("!share")) {

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

                            } else if (command.equals("!reload")) {
                                Intent intent = new Intent(context,SlotActivity.class);
                                startActivity(intent);


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
                    //Toast.makeText(context, "onRewarded! currency: " + reward.getType() + "  amount: " +  reward.getAmount(), Toast.LENGTH_SHORT).show();
                    resetElementsVisibility();
                    EventBus.getDefault().post(new RewardEvent(rewardAmount));
                }

                @Override
                public void onRewardedVideoAdLeftApplication() {
                    resetElementsVisibility();
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    resetElementsVisibility();

                }

                @Override
                public void onRewardedVideoAdFailedToLoad(int errorCode) {
                    resetElementsVisibility();
                    Toast.makeText(context, R.string.lbl_failed_to_load_video, Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onRewardedVideoAdLoaded() {
                    try {
                        if (rewardedVideoAd.isLoaded()){
                            rewardedVideoAd.show();
                        }
                        resetElementsVisibility();

                    } catch (Exception ex) {

                        ACRA.getErrorReporter().handleException(ex);

                    }

                }

                @Override
                public void onRewardedVideoAdOpened() {
                    resetElementsVisibility();
                }

                @Override
                public void onRewardedVideoStarted() {
                    resetElementsVisibility();
                }

                public void resetElementsVisibility(){
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }
                //</editor-fold>

            });

            actionWatchVideoRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Player player = OffsideApplication.getGameInfo().getPlayer();
                    if(player==null)
                        return;
                    if(player.getRewardVideoWatchCount() < OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame() ){
                        loadingVideoTextView.setText("Loading "+Integer.toString(player.getRewardVideoWatchCount()+1)+ " of "+ Integer.toString(OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame())+" allowed videos");
                        actionsFlowLayout.setVisibility(View.GONE);
                        rewardVideoLoadingRoot.setVisibility(View.VISIBLE);

                        loadRewardedVideoAd();
                    }
                    else
                    {
                        Toast.makeText(context, R.string.lbl_exceed_allowed_reward_video_watch_message, Toast.LENGTH_SHORT).show();
                    }




                }
            });
            //</editor-fold>

            //<editor-fold desc="exit game">
            actionExitGameRoot.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String androidDeviceId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                    OffsideApplication.signalRService.quitGame(gameId, playerId, androidDeviceId);
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
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

                    startActivity(intent);
                }
            });


            //</editor-fold>


        //</editor-fold>

    //</editor-fold>

            rewardVideoLoadingRoot = (LinearLayout) findViewById(R.id.c_reward_video_loading_root);

            //loadRewardedVideoAd();

            actionsFlowLayout.setVisibility(View.VISIBLE);
            rewardVideoLoadingRoot.setVisibility(View.GONE);



            //<editor-fold desc="IRON SOURCE STUFF">

            /*   ---------------Iron Source stuff
            IronSource.setUserId(playerId);  //must be before init;
            //Ad Units should be in the type of IronSource.Ad_Unit.AdUnitName//
            String ironSourceAppKey = "6248b53d";
            //IronSource.init(thisActivity, ironSourceAppKey);
            IronSource.init(thisActivity,ironSourceAppKey, IronSource.AD_UNIT.OFFERWALL, IronSource.AD_UNIT.INTERSTITIAL, IronSource.AD_UNIT.REWARDED_VIDEO);

            IntegrationHelper.validateIntegration(thisActivity);
            */

            /*
            IronSource.setRewardedVideoListener(new RewardedVideoListener() {

                @Override
                public void onRewardedVideoAdOpened() {
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }

                @Override
                public void onRewardedVideoAdClosed() {
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }

                @Override
                public void onRewardedVideoAvailabilityChanged(boolean available) {
                    //Change the in-app 'Traffic Driver' state according to availability.
                }

                @Override
                public void onRewardedVideoAdStarted() {
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }

                @Override
                public void onRewardedVideoAdEnded() {
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                }

                @Override
                public void onRewardedVideoAdRewarded(Placement placement) {

                    int rewardAmount = placement.getRewardAmount();
                    //Toast.makeText(context, "onRewarded! currency: " + reward.getType() + "  amount: " +  reward.getAmount(), Toast.LENGTH_SHORT).show();
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new RewardEvent(rewardAmount));

                }

                @Override
                public void onRewardedVideoAdShowFailed(IronSourceError error) {
                    actionsFlowLayout.setVisibility(View.VISIBLE);
                    rewardVideoLoadingRoot.setVisibility(View.GONE);
                    Toast.makeText(context, "Failed to load video", Toast.LENGTH_SHORT).show();
                }
            });

        */
            //</editor-fold>

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private void loadRewardedVideoAd() {
        try {

            //Google Firebase Ad-Mob
            String rewardedVideoAdAppUnitId = context.getString(R.string.rewarded_video_ad_unit_id_key);

            if(rewardedVideoAd == null)
                return;

            if (!rewardedVideoAd.isLoaded())
                rewardedVideoAd.loadAd(rewardedVideoAdAppUnitId, new AdRequest.Builder().build());

//IronSource
//            boolean isIronSourceRewardVideoAvailable = IronSource.isRewardedVideoAvailable();
//            if(isIronSourceRewardVideoAvailable)
//                IronSource.showRewardedVideo("DefaultRewardedVideo");


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
            //IronSource.onResume(thisActivity);
            OffsideApplication.setIsChatActivityVisible(true);
            hideKeypad();

            //reset to chat adapter
            createNewChatAdapter(true);

            Player player = OffsideApplication.getGameInfo().getPlayer();
            offsideCoins = player != null? player.getOffsideCoins() : 0;
            offsideCoinsTextView.setText(Integer.toString(offsideCoins));

            EventBus.getDefault().post(new SignalRServiceBoundEvent(context));


            // updating scoreboard in ui
            Scoreboard scoreboard = OffsideApplication.getScoreboard();
            if (scoreboard != null)
                generateScoreboard();
// updating player data in ui
            if (player != null)
                onReceivePlayer(player);



        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Override
    public void onPause(){
        super.onPause();
        //IronSource.onResume(thisActivity);
        OffsideApplication.setIsChatActivityVisible(false);
    }

    @Override
    public void onStop() {

        try {
            OffsideApplication.setIsChatActivityVisible(false);
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
            if (gameId != null && !gameId.isEmpty() && gameCode != null && !gameCode.isEmpty() && playerId != null && !playerId.isEmpty()) {
                OffsideApplication.signalRService.getChatMessages(gameId, gameCode, playerId, androidDeviceId);
            }


        } else {
            Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
            chatSendTextView.setAlpha(0.4f);
            chatActionsButton.setAlpha(0.4f);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        if (OffsideApplication.signalRService == null)
            return;

        Context eventContext = signalRServiceBoundEvent.getContext();

        if (eventContext == context || eventContext == getApplicationContext()) {

            if (gameId != null && !gameId.isEmpty() && gameCode != null && !gameCode.isEmpty() && playerId != null && !playerId.isEmpty()) {
                OffsideApplication.signalRService.getChatMessages(gameId, gameCode, playerId, androidDeviceId);

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

            Player player = chat.getPlayer();
            if (player == null)
                return;

            OffsideApplication.getGameInfo().setPlayer(player);
            OffsideApplication.playerAnswers = player.getPlayerAnswers();

            //scoreTextView.setText(String.valueOf((int) player.getPoints()));
            //scoreTextView1.setText(String.valueOf((int) player.getPoints()));

            createNewChatAdapter(false);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessage(ChatMessageEvent chatMessageEvent) {

        try {
            ChatMessage message = chatMessageEvent.getChatMessage();
            message.startCountdownTimer();

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

        chatMessageAdapter = new ChatMessageAdapter(context, messages, OffsideApplication.playerAnswers);
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



    public void removeButton (View view){
        view.setVisibility(View.GONE);
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onQuestionAnsweredEvent(QuestionAnsweredEvent questionAnsweredEvent) {

        try {
            String gameId = questionAnsweredEvent.getGameId();
            String questionId = questionAnsweredEvent.getQuestionId();
            //boolean isRandomAnswer = questionAnsweredEvent.isRandomAnswer();
            boolean isSkipped = questionAnsweredEvent.isSkipped();
            int betSize = questionAnsweredEvent.getBetSize();

            // this parameter will be null if the user does not answer
            String answerId = questionAnsweredEvent.getAnswerId();
            OffsideApplication.signalRService.postAnswer(gameId, playerId, questionId, answerId, isSkipped, betSize);
            if (!OffsideApplication.playerAnswers.containsKey(questionId))
                OffsideApplication.playerAnswers.put(questionId, new AnswerIdentifier(answerId, isSkipped, betSize, true));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerAcceptedEvent(IsAnswerAcceptedEvent isAnswerAcceptedEvent) {

        try {
            boolean isAnswerAccepted = isAnswerAcceptedEvent.getIsAnswerAccepted();
            if (!isAnswerAccepted) {
                String msg = "Answered not accepted! details: " + "gameid: " + isAnswerAcceptedEvent.getGameId() + " playerid: " + isAnswerAcceptedEvent.getPlayerId() + " questionid: " + isAnswerAcceptedEvent.getQuestionId() + " answerid: " + isAnswerAcceptedEvent.getAnswerId() + " isSkippped: " + isAnswerAcceptedEvent.isSkipped() + " betsize: " + isAnswerAcceptedEvent.getBetSize();
                ACRA.getErrorReporter().handleSilentException(new Throwable(msg));
            }

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
    public void onReceiveRewardEvent(RewardEvent rewardEvent) {
        try {
            int rewardAmount = rewardEvent.getRewardAmount();
            Player player = OffsideApplication.getGameInfo().getPlayer();
            if ( player == null || rewardAmount==0)
                return;

            player.incrementRewardVideoWatchCount();
            OffsideApplication.signalRService.setPowerItems(gameId, playerId, rewardAmount, true);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(Player updatedPlayer) {
        try {
            if (updatedPlayer == null)
                return;


            OffsideApplication.playerAnswers = updatedPlayer.getPlayerAnswers();
            //scoreTextView.setText(Integer.toString((int) updatedPlayer.getOffsideCoins()));
            //scoreTextView1.setText(Integer.toString((int) updatedPlayer.getOffsideCoins()));

            Player currentPlayer = OffsideApplication.getGameInfo().getPlayer();

            if (currentPlayer != null) {
                int oldOffsideCoinsValue = currentPlayer.getOffsideCoins();
                int newOffsideCoinsValue = updatedPlayer.getOffsideCoins();
                offsideCoinsTextView.setText(Integer.toString(newOffsideCoinsValue));
                if (newOffsideCoinsValue != oldOffsideCoinsValue) {
                    offsideCoinsImageView.animate().rotationXBy(360.0f).setDuration(1000).start();

                }

//                int oldTrophiesValue = currentPlayer.getTrophies();
//                int newTrophiesValue = updatedPlayer.getTrophies();
//                if (newTrophiesValue != oldTrophiesValue) {
//                    trophiesTextView.setText(Integer.toString(newTrophiesValue));
//                    trophiesImageView.animate().rotationXBy(360.0f).setDuration(1000).start();
//
//                }

                int oldPowerItems = currentPlayer.getPowerItems();
                int newPowerItems = updatedPlayer.getPowerItems();
                powerItemsTextView.setText(Integer.toString(newPowerItems));
                if (newPowerItems != oldPowerItems) {

                    //powerItemImageView.animate().rotationXBy(360.0f).setDuration(1000).start();
                    Animation a = new RotateAnimation(0.0f, 360.0f,
                            Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                            0.5f);
                    a.setRepeatCount(1);
                    a.setDuration(1000);
                    powerItemImageView.startAnimation(a);

                }
            }

            //OffsideApplication.getGameInfo().setTrophies(updatedPlayer.getTrophies());
            //OffsideApplication.setPlayer(updatedPlayer);
            OffsideApplication.getGameInfo().setPlayer(updatedPlayer);



        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveScoreboard(ScoreboardEvent scoreboardEvent) {
        try {

            Scoreboard scoreboard = scoreboardEvent.getScoreboard();

            if (scoreboard == null)
                return;
            Score[]  scores = scoreboard.getScores();

            if (scores == null || scores.length == 0)
                return;

            //check if scoreboard was changed
            Scoreboard currentScoreboard = OffsideApplication.getScoreboard();
            boolean isScoreboardsEquals = false;
            if (!scoreboard.isForceUpdate()&& currentScoreboard != null && currentScoreboard.getScores() != null && currentScoreboard.getScores().length == scores.length) {
                Score [] currentScores = currentScoreboard.getScores();
                for(int i=0; i<currentScores.length;i++){
                    boolean isEqualScore = currentScores[i].getImageUrl().equals(scores[i].getImageUrl()) &&
                            currentScores[i].getPosition() ==scores[i].getPosition() &&
                            currentScores[i].getOffsideCoins()==scores[i].getOffsideCoins();

                    if(!isEqualScore)
                        break;
                    if(i==currentScores.length-1)
                        isScoreboardsEquals = true;
                }

            }
            if(isScoreboardsEquals)
                return;

            OffsideApplication.setScoreboard(scoreboard);


            generateScoreboard();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void generateScoreboard() {
        scoreboardRoot.removeAllViewsInLayout();

        for(Score score: OffsideApplication.getScoreboard().getScores()){

            ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.scoreboard_item, scoreboardRoot,false);

            TextView rankTextView = (TextView) layout.getChildAt(0);
            ImageView imageView = (ImageView) layout.getChildAt(1);
            TextView textView = (TextView) layout.getChildAt(2);

            rankTextView.setText(Integer.toString(score.getPosition()));
            ImageHelper.loadImage(thisActivity, score.getImageUrl(), imageView, "ChatActivity");
            textView.setText(Integer.toString(score.getOffsideCoins()));

            scoreboardRoot.addView(layout);
        }
    }

    public void shareOnFacebook(){
//        ShareLinkContent content = new ShareLinkContent.Builder()
//                .setContentUrl(Uri.parse("http://www.sidekickgame.com/"))
//                .setQuote("this is a set Quote")
//                .build();

        Bitmap image = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo_25);
        SharePhoto photo = new SharePhoto.Builder()
                .setBitmap(image)

                .build();
        SharePhotoContent content = new SharePhotoContent.Builder()
                .addPhoto(photo)
                .build();

        //ShareDialog.show(thisActivity, content);
        facebookShareButton.setShareContent(content);

    }





}
