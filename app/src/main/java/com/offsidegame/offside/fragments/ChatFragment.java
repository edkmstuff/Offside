package com.offsidegame.offside.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.LobbyActivity;
import com.offsidegame.offside.activities.SlotActivity;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.RewardEvent;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.Position;
import com.offsidegame.offside.models.PostAnswerRequestInfo;
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

/**
 * Created by user on 8/22/2017.
 */


public class ChatFragment extends Fragment {


    //<editor-fold desc="****************MEMBERS**************">
    private FrameLayout loadingRoot;
    private TextView versionTextView;

    private TextView chatSendTextView;
    private EditText chatMessageEditText;
    private SharedPreferences settings;


    private Chat chat;
    private ArrayList messages;
    private ChatMessageAdapter chatMessageAdapter;

    private LinearLayout root;
    private ListView chatListView;

    private String privateGameTitle;
    private String homeTeam;
    private String awayTeam;
    private int offsideCoins;
    private int trophies;
    private int powerItems;

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


    private TextView trophiesTextView;
    private TextView powerItemsTextView;
    private TextView offsideCoinsTextView;
    private ImageView playerPictureImageView;

    private ImageView offsideCoinsImageView;
    private ImageView trophiesImageView;
    private ImageView powerItemImageView;


    private FlowLayout actionsFlowLayout;

    private ShareButton facebookShareButton;

    private LinearLayout actionLeadersRoot;
    private LinearLayout actionCurrentQuestionRoot;
    private LinearLayout actionOffsideCoinsRoot;
    private LinearLayout actionReloadRoot;
    private LinearLayout actionCodeRoot;
    private LinearLayout actionShareRoot;
    private LinearLayout actionWatchVideoRoot;

    private AvailableGame selectedAvailableGame = null;
    private String gameId = null;
    private String privateGameId = null;
private String groupId = null;
    private String androidDeviceId = null;
    private String playerId = null;

    //</editor-fold>

    public static ChatFragment newInstance(Activity activity, String playerId) {
        ChatFragment chatFragment = new ChatFragment();
        EventBus.getDefault().register(chatFragment);
        //chat data
        chatFragment.gameId = OffsideApplication.getSelectedGameId();
        chatFragment.privateGameId = OffsideApplication.getSelectedPrivateGameId();
        chatFragment.groupId = OffsideApplication.getSelectedPrivateGroupId();
        chatFragment.androidDeviceId = OffsideApplication.getAndroidDeviceId();
        chatFragment.playerId = playerId;

        if (chatFragment.gameId != null && chatFragment.privateGameId != null && chatFragment.groupId != null && chatFragment.androidDeviceId != null && chatFragment.playerId != null) {
            OffsideApplication.signalRService.requestJoinPrivateGame(chatFragment.gameId, chatFragment.groupId, chatFragment.privateGameId, chatFragment.playerId, chatFragment.androidDeviceId);
        } else {
            Toast.makeText(activity, "Can not join this game", Toast.LENGTH_LONG);
        }

        return chatFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        getIDs(view);
        setEvents();

        return view;
    }

    private void getIDs(View view) {

        loadingRoot = (FrameLayout) view.findViewById(R.id.shared_loading_root);
        versionTextView = (TextView) view.findViewById(R.id.shared_version_text_view);
        root = (LinearLayout) view.findViewById(R.id.fc_root);
        chatListView = (ListView) view.findViewById(R.id.fc_chat_list_view);
        chatSendTextView = (TextView) view.findViewById(R.id.fc_chatSendTextView);
        chatMessageEditText = (EditText) view.findViewById(R.id.fc_chat_message_edit_text);
        privateGameNameTextView = (TextView) view.findViewById(R.id.fc_private_game_name_text_view);
        gameTitleTextView = (TextView) view.findViewById(R.id.fc_game_title_text_view);
        positionTextView = (TextView) view.findViewById(R.id.fc_position_text_view);
        scoreboardRoot = (LinearLayout) view.findViewById(R.id.fc_scoreboard_root);
        contentRoot = (RelativeLayout) view.findViewById(R.id.fc_content_root);
        actionsMenuRoot = (LinearLayout) view.findViewById(R.id.fc_actions_menu_root);

        chatActionsButton = (ImageView) view.findViewById(R.id.fc_chatActionsButton);
        actionsFlowLayout = (FlowLayout) view.findViewById(R.id.fc_actions_flow_layout);
        loadingVideoTextView = (TextView) view.findViewById(R.id.fc_loading_video_text_view);
        playerPictureImageView = (ImageView) view.findViewById(R.id.fc_player_picture_image_view);
        offsideCoinsTextView = (TextView) view.findViewById(R.id.fc_offside_coins_text_view);
        offsideCoinsImageView = (ImageView) view.findViewById(R.id.fc_offside_coins_image_view);
        powerItemsTextView = (TextView) view.findViewById(R.id.fc_power_items_text_view);
        powerItemImageView = (ImageView) view.findViewById(R.id.fc_power_item_image_view);

        facebookShareButton = (ShareButton) view.findViewById(R.id.fc_facebook_share_button);
        actionExitGameRoot = (LinearLayout) view.findViewById(R.id.fc_action_exit_game_root);

        actionLeadersRoot = (LinearLayout) view.findViewById(R.id.fc_action_leaders_root);
        actionCurrentQuestionRoot = (LinearLayout) view.findViewById(R.id.fc_action_current_question_root);
        actionOffsideCoinsRoot = (LinearLayout) view.findViewById(R.id.fc_action_offside_coins_root);
        actionReloadRoot = (LinearLayout) view.findViewById(R.id.fc_action_reload_root);
        actionCodeRoot = (LinearLayout) view.findViewById(R.id.fc_action_code_root);
        actionShareRoot = (LinearLayout) view.findViewById(R.id.fc_action_share_root);
        actionWatchVideoRoot = (LinearLayout) view.findViewById(R.id.fc_action_watch_video_root);
        rewardVideoLoadingRoot = (LinearLayout) view.findViewById(R.id.fc_reward_video_loading_root);
        chatListView = (ListView) view.findViewById(R.id.fc_chat_list_view);


    }


    private void setEvents() {

        chatMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isActionMenuVisible) {
                    actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
                    actionsMenuRoot.setVisibility(View.INVISIBLE);
                }
            }
        });


        chatSendTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatMessageEditText.getText().toString();
                if (message != null && message.length() > 0) {
                    OffsideApplication.signalRService.requestSendChatMessage(gameId, privateGameId, message, playerId);
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
                if (isActionMenuVisible) {
                    actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
                    actionsMenuRoot.setVisibility(View.INVISIBLE);

                } else {
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

                            PackageManager pm = getContext().getPackageManager();
                            boolean isWhatsappInstalled = isPackageInstalled("com.whatsapp", pm);
                            String shareMessage = "Yo! I am *Offsiding* with the gang, come join us using this code:   *" + OffsideApplication.getSelectedPrivateGameId() + "*";
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
                            Intent intent = new Intent(getContext(), SlotActivity.class);
                            startActivity(intent);


                        } else {
                            OffsideApplication.signalRService.requestSendChatMessage(selectedAvailableGame.getGameId(), OffsideApplication.getSelectedPrivateGameId(), command, OffsideApplication.getPlayerAssets().getPlayerId());
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
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(getContext());
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
                Toast.makeText(getContext(), R.string.lbl_failed_to_load_video, Toast.LENGTH_SHORT).show();


            }

            @Override
            public void onRewardedVideoAdLoaded() {
                try {
                    if (rewardedVideoAd.isLoaded()) {
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

            public void resetElementsVisibility() {
                actionsFlowLayout.setVisibility(View.VISIBLE);
                rewardVideoLoadingRoot.setVisibility(View.GONE);
            }
            //</editor-fold>

        });

        actionWatchVideoRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                PlayerModel player = OffsideApplication.getGameInfo().getPlayer();
                if (player == null)
                    return;
                if (player.getRewardVideoWatchCount() < OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame()) {
                    loadingVideoTextView.setText("Loading " + Integer.toString(player.getRewardVideoWatchCount() + 1) + " of " + Integer.toString(OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame()) + " allowed videos");
                    actionsFlowLayout.setVisibility(View.GONE);
                    rewardVideoLoadingRoot.setVisibility(View.VISIBLE);

                    loadRewardedVideoAd();
                } else {
                    Toast.makeText(getContext(), R.string.lbl_exceed_allowed_reward_video_watch_message, Toast.LENGTH_SHORT).show();
                }


            }
        });
        //</editor-fold>

        //<editor-fold desc="exit game">
        actionExitGameRoot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String androidDeviceId = OffsideApplication.getAndroidDeviceId();
                OffsideApplication.signalRService.quitGame(gameId, playerId, androidDeviceId);
                chatActionsButton.performClick();
                SharedPreferences settings = getContext().getSharedPreferences(getString(R.string.preference_name), 0);
                SharedPreferences.Editor editor = settings.edit();

                editor.putString(getString(R.string.game_id_key), null);
                editor.putString(getString(R.string.private_game_id_key), null);
                editor.putString(getString(R.string.private_group_id_key), null);
                editor.putString(getString(R.string.private_game_title_key), null);
                editor.putString(getString(R.string.home_team_key), null);
                editor.putString(getString(R.string.away_team_key), null);

                editor.commit();
                Intent intent = new Intent(getContext(), LobbyActivity.class);
                startActivity(intent);
            }
        });


        //</editor-fold>


        //</editor-fold>

        //</editor-fold>


        //loadRewardedVideoAd();

        actionsFlowLayout.setVisibility(View.VISIBLE);
        rewardVideoLoadingRoot.setVisibility(View.GONE);


    }

    private void hideKeypad() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(root.getWindowToken(), 0);
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    private void loadRewardedVideoAd() {
        try {

            //Google Firebase Ad-Mob
            String rewardedVideoAdAppUnitId = getContext().getString(R.string.rewarded_video_ad_unit_id_key);

            if (rewardedVideoAd == null)
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


    private void createNewChatAdapter(boolean reset) {
        messages = new ArrayList();

        if (!reset && chat != null)
            messages = chat.getChatMessages();

        chatMessageAdapter = new ChatMessageAdapter(getContext(), messages, OffsideApplication.playerAnswers);
        chatListView.setAdapter(chatMessageAdapter);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPlayerJoinedPrivateGame(JoinGameEvent joinGameEvent) {
        try {

            GameInfo gameInfo = joinGameEvent.getGameInfo();
            if (gameInfo == null) {
                return;
            }

            String gameId = gameInfo.getGameId();
            String privateGameId = gameInfo.getPrivateGameId();
            String privateGroupId = OffsideApplication.getSelectedPrivateGroupId();
            String privateGameTitle = gameInfo.getPrivateGameTitle();
            String homeTeam = gameInfo.getHomeTeam();
            String awayTeam = gameInfo.getAwayTeam();

            OffsideApplication.setGameInfo(gameInfo);

            SharedPreferences settings = getContext().getSharedPreferences(getString(R.string.preference_name), 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(getString(R.string.game_id_key), gameId);
            editor.putString(getString(R.string.private_group_id_key), privateGroupId);
            editor.putString(getString(R.string.private_game_id_key), privateGameId);
            editor.putString(getString(R.string.private_game_title_key), privateGameTitle);
            editor.putString(getString(R.string.home_team_key), homeTeam);
            editor.putString(getString(R.string.away_team_key), awayTeam);
            editor.commit();

            init();

            OffsideApplication.signalRService.requestGetChatMessages(gameId, privateGameId, playerId, androidDeviceId);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void init() {

        versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());
        androidDeviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        gameId = OffsideApplication.getGameInfo().getGameId();

        privateGameId = OffsideApplication.getGameInfo().getPrivateGameId();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        playerId = user.getUid();

        privateGameTitle = OffsideApplication.getGameInfo().getPrivateGameTitle();
        homeTeam = OffsideApplication.getGameInfo().getHomeTeam();
        awayTeam = OffsideApplication.getGameInfo().getAwayTeam();
        PlayerModel player = OffsideApplication.getGameInfo().getPlayer();
        offsideCoins = player != null ? player.getOffsideCoins() : 0;
        trophies = OffsideApplication.getGameInfo().getTrophies();
        powerItems = player.getPowerItems();


        actionsMenuRoot.setScaleX(0f);
        actionsMenuRoot.setScaleY(0f);
        actionsMenuRoot.setAlpha(0.99f);


        actionsMenuRoot.setVisibility(View.GONE);

        privateGameNameTextView.setText(privateGameTitle);
        String title = String.format("%s vs. %s", homeTeam, awayTeam);
        gameTitleTextView.setText(title);

        offsideCoinsTextView.setText(Integer.toString(offsideCoins));

        powerItemsTextView.setText(Integer.toString(powerItems));

        settings = getContext().getSharedPreferences(getString(R.string.preference_name), 0);
        ImageHelper.loadImage(getActivity(), player != null ? player.getImageUrl() : settings.getString(getString(R.string.player_profile_picture_url_key), null), playerPictureImageView, "ChatActivity");


    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChat(ChatEvent chatEvent) {

        try {

            chat = new Chat(chatEvent.getChat());

            EventBus.getDefault().post(new PositionEvent(chat.getPosition()));

            PlayerModel player = chat.getPlayer();
            if (player == null)
                return;

            OffsideApplication.getGameInfo().setPlayer(player);
            OffsideApplication.playerAnswers = player.getPlayerAnswers();

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
            OffsideApplication.signalRService.requestPostAnswer(gameId, playerId, questionId, answerId, isSkipped, betSize);
            if (!OffsideApplication.playerAnswers.containsKey(questionId))
                OffsideApplication.playerAnswers.put(questionId, new AnswerIdentifier(answerId, isSkipped, betSize, true));

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onAnswerAcceptedEvent(PostAnswerRequestInfo postAnswerRequestInfo) {

        try {
            boolean isAnswerAccepted = postAnswerRequestInfo.isAnswerAccepted();
            if (!isAnswerAccepted) {
                String msg = "Answered not accepted! details: " + "gameid: " + postAnswerRequestInfo.getGameId() + " playerid: " + postAnswerRequestInfo.getPlayerId() + " questionid: " + postAnswerRequestInfo.getQuestionId() + " answerid: " + postAnswerRequestInfo.getAnswerId() + " isSkippped: " + postAnswerRequestInfo.isSkipped() + " betsize: " + postAnswerRequestInfo.getBetSize();
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
            PlayerModel player = OffsideApplication.getGameInfo().getPlayer();
            if (player == null || rewardAmount == 0)
                return;

            player.incrementRewardVideoWatchCount();
            OffsideApplication.signalRService.setPowerItems(gameId, playerId, rewardAmount, true);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(PlayerModel updatedPlayer) {
        try {
            if (updatedPlayer == null)
                return;


            OffsideApplication.playerAnswers = updatedPlayer.getPlayerAnswers();
            //scoreTextView.setText(Integer.toString((int) updatedPlayer.getOffsideCoins()));
            //scoreTextView1.setText(Integer.toString((int) updatedPlayer.getOffsideCoins()));

            PlayerModel currentPlayer = OffsideApplication.getGameInfo().getPlayer();

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
            Score[] scores = scoreboard.getScores();

            if (scores == null || scores.length == 0)
                return;

            //check if scoreboard was changed
            Scoreboard currentScoreboard = OffsideApplication.getScoreboard();
            boolean isScoreboardsEquals = false;
            if (!scoreboard.isForceUpdate() && currentScoreboard != null && currentScoreboard.getScores() != null && currentScoreboard.getScores().length == scores.length) {
                Score[] currentScores = currentScoreboard.getScores();
                for (int i = 0; i < currentScores.length; i++) {
                    boolean isEqualScore = currentScores[i].getImageUrl().equals(scores[i].getImageUrl()) &&
                            currentScores[i].getPosition() == scores[i].getPosition() &&
                            currentScores[i].getOffsideCoins() == scores[i].getOffsideCoins();

                    if (!isEqualScore)
                        break;
                    if (i == currentScores.length - 1)
                        isScoreboardsEquals = true;
                }

            }
            if (isScoreboardsEquals)
                return;

            OffsideApplication.setScoreboard(scoreboard);


            generateScoreboard();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void generateScoreboard() {
        scoreboardRoot.removeAllViewsInLayout();

        for (Score score : OffsideApplication.getScoreboard().getScores()) {

            ViewGroup layout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_item, scoreboardRoot, false);

            TextView rankTextView = (TextView) layout.getChildAt(0);
            ImageView imageView = (ImageView) layout.getChildAt(1);
            TextView textView = (TextView) layout.getChildAt(2);

            rankTextView.setText(Integer.toString(score.getPosition()));
            ImageHelper.loadImage(((Activity) getContext()), score.getImageUrl(), imageView, "ChatActivity");
            textView.setText(Integer.toString(score.getOffsideCoins()));

            scoreboardRoot.addView(layout);
        }
    }


}
