package com.offsidegame.offside.fragments;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mikhaellopez.circularprogressbar.CircularProgressBar;
import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.ChatMessageAdapter;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.GroupInviteEvent;
import com.offsidegame.offside.events.InGamePlayerAssetsUpdateEvent;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.events.NetworkingErrorFixedEvent;
import com.offsidegame.offside.events.NotEnoughAssetsEvent;
import com.offsidegame.offside.events.PlayerModelEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.RewardEvent;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerActivity;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.Position;
import com.offsidegame.offside.models.PostAnswerRequestInfo;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.ScoreDetailedInfo;
import com.offsidegame.offside.models.Scoreboard;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Map;


/**
 * Created by user on 8/22/2017.
 */


public class ChatFragment extends Fragment {


    //<editor-fold desc="****************MEMBERS**************">

    //private TextView versionTextView;

    private ImageView chatSendImageView;
    private EditText chatMessageEditText;
    private SharedPreferences settings;


    private Chat chat;
    private ArrayList messages;
    private ChatMessageAdapter chatMessageAdapter;

    private LinearLayout root;
    private ListView chatListView;

    private ImageView backNavigationButtonImageView;
    private ImageView exitButtonImageView;

    private String privateGroupName;
    private String homeTeam;
    private String awayTeam;
    private int offsideCoins;

    private int powerItems;

    private TextView privateGameNameTextView;
    private TextView gameTitleTextView;
    private TextView positionTextView;

    //private LinearLayout actionsMenuRoot;

    private ImageView chatActionsButton;

    private boolean isActionMenuVisible = false;

    //private RewardedVideoAd rewardedVideoAd;
//    private LinearLayout rewardVideoLoadingRoot;
//    private TextView loadingVideoTextView;
//    private LinearLayout actionExitGameRoot;

    private LinearLayout scoreboardRoot;

    private ImageView inviteFriendsImageView;

    private TextView powerItemsTextView;
    private TextView offsideCoinsTextView;
    private ImageView playerPictureImageView;
    private ImageView offsideCoinsImageView;
    private ImageView powerItemsImageView;

//    private FlowLayout actionsFlowLayout;
//
//    private LinearLayout actionReloadRoot;
//
//    private LinearLayout actionWatchVideoRoot;

    //private AvailableGame selectedAvailableGame = null;
    private String gameId = null;
    private String privateGameId = null;
    private String androidDeviceId = null;
    private String playerId = null;

    private Dialog approveQuitDialog;
    private Button dialogOkButton;
    private Button dialogCancelButton;


    private LinearLayout disconnectedMessageRoot;

    private LinearLayout currentQuestionRoot;
    private TextView currentQuestionTimeRemainingTextView;
    private CircularProgressBar currentQuestionTimeRemainingCircularProgressBar;
    private TextView currentQuestionTextTextView;
    private TextView currentQuestionPlayerAnswerTextView;
    CountDownTimer countDownTimer;

    //</editor-fold>

    public static ChatFragment newInstance() {
        ChatFragment chatFragment = new ChatFragment();
        return chatFragment;
    }

    @Override
    public void onResume() {

        try {
            super.onResume();
            EventBus.getDefault().register(this);
            OffsideApplication.setScoreboard(null);
            init();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    private void init() {

        androidDeviceId = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);

        privateGroupName = OffsideApplication.getSelectedPrivateGroup().getName();

        gameId = OffsideApplication.getGameInfo().getGameId();

        privateGameId = OffsideApplication.getGameInfo().getPrivateGameId();

        playerId = OffsideApplication.getPlayerId();

        homeTeam = OffsideApplication.getGameInfo().getHomeTeam();
        awayTeam = OffsideApplication.getGameInfo().getAwayTeam();
        PlayerModel player = OffsideApplication.getGameInfo().getPlayer();
        offsideCoins = player != null ? player.getOffsideCoins() : 0;
        powerItems = player.getPowerItems();

//        actionsMenuRoot.setScaleX(0f);
//        actionsMenuRoot.setScaleY(0f);
//        actionsMenuRoot.setAlpha(0.99f);

//        actionsMenuRoot.setVisibility(View.GONE);

        String chatTitle = String.format("%s", privateGroupName);
        privateGameNameTextView.setText(chatTitle);
        String title = String.format("%s vs. %s", homeTeam, awayTeam);
        gameTitleTextView.setText(title);

        offsideCoinsTextView.setText(Integer.toString(offsideCoins));

        powerItemsTextView.setText(Integer.toString(powerItems));

        settings = getContext().getSharedPreferences(getString(R.string.preference_name), 0);
        ImageHelper.loadImage(getActivity(), player != null ? player.getImageUrl() : settings.getString(getString(R.string.player_profile_picture_url_key), null), playerPictureImageView, "ChatActivity", true);

        OffsideApplication.networkingService.requestGetChatMessages(playerId, gameId, privateGameId, androidDeviceId);

        resetVisibility();



    }

    private void resetVisibility(){
        currentQuestionRoot.setVisibility(View.GONE);

    }

    @Override
    public void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            View view = inflater.inflate(R.layout.fragment_chat, container, false);

            getIDs(view);
            setEvents();
            //versionTextView.setText(OffsideApplication.getVersion() == null ? "0.0" : OffsideApplication.getVersion());

            return view;


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return null;
        }

    }

    private void getIDs(View view) {

        //versionTextView = (TextView) view.findViewById(R.id.shared_version_text_view);
        root = (LinearLayout) view.findViewById(R.id.fc_root);
        chatListView = (ListView) view.findViewById(R.id.fc_chat_list_view);
        chatSendImageView = (ImageView) view.findViewById(R.id.fc_chat_send_image_view);
        chatMessageEditText = (EditText) view.findViewById(R.id.fc_chat_message_edit_text);
        privateGameNameTextView = (TextView) view.findViewById(R.id.fc_private_game_name_text_view);
        gameTitleTextView = (TextView) view.findViewById(R.id.fc_game_title_text_view);
        positionTextView = (TextView) view.findViewById(R.id.fc_position_text_view);
        scoreboardRoot = (LinearLayout) view.findViewById(R.id.fc_scoreboard_root);
        inviteFriendsImageView = (ImageView) view.findViewById(R.id.fc_invite_friends_image_view);

        //    actionsMenuRoot = (LinearLayout) view.findViewById(R.id.fc_actions_menu_root);

        //chatActionsButton = (ImageView) view.findViewById(R.id.fc_chatActionsButton);
        //actionsFlowLayout = (FlowLayout) view.findViewById(R.id.fc_actions_flow_layout);
        //loadingVideoTextView = (TextView) view.findViewById(R.id.fc_loading_video_text_view);
        playerPictureImageView = (ImageView) view.findViewById(R.id.fc_player_picture_image_view);
        offsideCoinsTextView = (TextView) view.findViewById(R.id.fc_offside_coins_text_view);
        offsideCoinsImageView = (ImageView) view.findViewById(R.id.fc_offside_coins_image_view);
        powerItemsTextView = (TextView) view.findViewById(R.id.fc_power_items_text_view);
        powerItemsImageView = (ImageView) view.findViewById(R.id.fc_power_item_image_view);


        //actionReloadRoot = (LinearLayout) view.findViewById(R.id.fc_action_reload_root);
        //actionWatchVideoRoot = (LinearLayout) view.findViewById(R.id.fc_action_watch_video_root);
        //rewardVideoLoadingRoot = (LinearLayout) view.findViewById(R.id.fc_reward_video_loading_root);
        chatListView = (ListView) view.findViewById(R.id.fc_chat_list_view);

        backNavigationButtonImageView = view.findViewById(R.id.fsg_back_navigation_button_image_view);
        exitButtonImageView = view.findViewById(R.id.fc_exit_button_image_view);

        disconnectedMessageRoot = view.findViewById(R.id.fc_disconnected_message_root);

        //currentQuestion
        currentQuestionRoot = view.findViewById(R.id.cf_current_question_root);
        currentQuestionTimeRemainingCircularProgressBar = view.findViewById(R.id.cf_current_question_time_remaining_circular_progress_bar);
        currentQuestionTimeRemainingTextView = view.findViewById(R.id.cf_current_question_time_remaining_text_view);
        currentQuestionTextTextView = view.findViewById(R.id.cf_current_question_text_text_view);
        currentQuestionPlayerAnswerTextView = view.findViewById(R.id.cf_current_question_player_answer_text_view);


    }


    private void setEvents() {

        backNavigationButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
            }
        });

        exitButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                showApproveQuitDialog();
            }
        });

        inviteFriendsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String groupId = OffsideApplication.getSelectedPrivateGroupId();
                String groupName = OffsideApplication.getSelectedPrivateGroup().getName();
                gameId = OffsideApplication.getSelectedGameId();
                privateGameId = OffsideApplication.getSelectedPrivateGameId();
                String playerId = OffsideApplication.getPlayerId();
                EventBus.getDefault().post(new GroupInviteEvent(groupId, groupName, gameId, privateGameId, playerId));
            }
        });


        chatMessageEditText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (isActionMenuVisible) {
//                    actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
//                    actionsMenuRoot.setVisibility(View.INVISIBLE);
//                }
            }
        });


        chatSendImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = chatMessageEditText.getText().toString();
                if (message != null && message.length() > 0) {
                    //clear text
                    chatMessageEditText.setText("");
                    //hide keypad
                    hideKeypad();

                    OffsideApplication.networkingService.requestSendChatMessage(playerId, gameId, privateGameId, message);


                }


            }
        });

        powerItemsTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NotEnoughAssetsEvent(0, 1, OffsideApplication.POWER_ITEMS, false));
            }
        });

        powerItemsImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EventBus.getDefault().post(new NotEnoughAssetsEvent(0, 1, OffsideApplication.POWER_ITEMS, false));
            }
        });

//        chatActionsButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                hideKeypad();
//                if (isActionMenuVisible) {
//                    actionsMenuRoot.animate().scaleX(0f).scaleY(0f);
//                    actionsMenuRoot.setVisibility(View.INVISIBLE);
//
//                } else {
//                    actionsMenuRoot.setVisibility(View.VISIBLE);
//                    actionsMenuRoot.animate().scaleX(1f).scaleY(1f);
//                }
//
//                isActionMenuVisible = !isActionMenuVisible;
//            }
//        });

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

        playerPictureImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Scoreboard scoreboard = OffsideApplication.getScoreboard();
                if (scoreboard == null)
                    return;
                Score score = scoreboard.getScoreByPlayerId(playerId);
                if (score == null)
                    return;

                showPlayerInGameActivityDialog(score.getScoreDetailedInfo(), score.getImageUrl(), score.getName());
            }
        });

        //</editor-fold>

        //</editor-fold>


    }

    private void showApproveQuitDialog() {

        try {
            approveQuitDialog = new Dialog(getContext());
            approveQuitDialog.setContentView(R.layout.dialog_approve_quit_game);

            dialogOkButton = approveQuitDialog.findViewById(R.id.daq_ok_button);
            dialogCancelButton = approveQuitDialog.findViewById(R.id.daq_cancel_button);

            dialogOkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OffsideApplication.networkingService.requestQuitFromPrivateGame(playerId, gameId, privateGameId, androidDeviceId);
                    approveQuitDialog.cancel();

                }
            });

            dialogCancelButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    approveQuitDialog.cancel();
                }
            });

            approveQuitDialog.show();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    private void hideKeypad() {
        try {
            InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(root.getWindowToken(), 0);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChat(ChatEvent chatEvent) {

        try {

            chat = new Chat(chatEvent.getChat());
            Scoreboard scoreboard = chat.getScoreboard();
            EventBus.getDefault().post(new ScoreboardEvent(scoreboard));

            EventBus.getDefault().post(new PositionEvent(chat.getPosition()));

            ChatMessage currentQuestionChatMessage = chat.getCurrentQuestionChatMessage();
            if(currentQuestionChatMessage==null)
                currentQuestionRoot.setVisibility(View.GONE);

            showCurrentQuestion(currentQuestionChatMessage);


            PlayerModel player = chat.getPlayer();
            if (player == null)
                return;

            OffsideApplication.getGameInfo().setPlayer(player);
            OffsideApplication.playerAnswers = player.getPlayerAnswers();

            createNewChatAdapter(false);


            root.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveChatMessage(ChatMessageEvent chatMessageEvent) {

        try {
            final ChatMessage message = chatMessageEvent.getChatMessage();
            message.startCountdownTimer();

            boolean isAdded = chat.addMessageIfNotAlreadyExists(message);

            if (!isAdded) {
                throw new Exception("Duplicate chat message. id: " + message.getId() + " content: " + message.getMessageText());
            }

            showCurrentQuestion(message);
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

        chatMessageAdapter = new ChatMessageAdapter(getContext(), messages, OffsideApplication.playerAnswers);
        chatListView.setAdapter(chatMessageAdapter);

    }

    private void showCurrentQuestion(final ChatMessage message) {

        try {
            if(message==null)
                return;
            if (message.getMessageType().equals(OffsideApplication.getMessageTypeClosedQuestion())){
                cancelTimer();
                currentQuestionRoot.setVisibility(View.GONE);
                return;
            }

            if (message.getMessageType().equals(OffsideApplication.getMessageTypeProcessedQuestion())) {

                final Gson gson = new GsonBuilder().serializeNulls().create();
                Question currentQuestion = gson.fromJson(message.getMessageText(), Question.class);
                if (currentQuestion == null)
                    return;
                currentQuestionTextTextView.setText(currentQuestion.getQuestionText());
                GameInfo gameInfo = OffsideApplication.getGameInfo();
                if (gameInfo == null)
                    return;
                Map<String, AnswerIdentifier> playerAnswers = OffsideApplication.playerAnswers;
                boolean isPlayerAnsweredQuestion =  playerAnswers!=null && playerAnswers.containsKey(currentQuestion.getId());
                if (isPlayerAnsweredQuestion) {
                    AnswerIdentifier answerIdentifier = playerAnswers.get(currentQuestion.getId());
                    String playerAnswerText = getAnswerText(currentQuestion, answerIdentifier.getAnswerId());
                    currentQuestionPlayerAnswerTextView.setText(playerAnswerText);
                }

                boolean isTimerRequired = (currentQuestion.getGamePhase().equals(OffsideApplication.getGamePlayPhaseName()) && (
                        currentQuestion.getQuestionType().equals(OffsideApplication.getQuestionTypeFun()) ||
                                currentQuestion.getQuestionType().equals(OffsideApplication.getQuestionTypePrediction()))
                );

                if (isTimerRequired) {


                    final int timeToAnswer = message.getTimeLeftToAnswer();
                    //Log.i("SIDE", "timetoanswer: " + String.valueOf(timeToAnswer));
                    final int progressBarDuration = timeToAnswer;
                    currentQuestionTimeRemainingCircularProgressBar.setProgressWithAnimation(0, progressBarDuration);

                    if(timeToAnswer==0)
                        currentQuestionTimeRemainingCircularProgressBar.setProgress(100);
                    //timer of current question
                    if (timeToAnswer > 0) {

                        cancelTimer();

                        countDownTimer = new CountDownTimer(timeToAnswer, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                message.setTimeLeftToAnswer((int) millisUntilFinished);
                                //Log.i("SIDE", "timerId: " + String.valueOf(this.hashCode()) + "  - secondsLeft: " + Math.round((int) millisUntilFinished / 1000.0f));
                                //Log.i("SIDE", "progresss: "+ String.valueOf(100 * (timeToAnswer - millisUntilFinished) / (float) timeToAnswer) );
                                currentQuestionTimeRemainingCircularProgressBar.setProgressWithAnimation(100 * (timeToAnswer - millisUntilFinished) / (float) timeToAnswer, timeToAnswer/1000);

                                String formattedTimerDisplay = formatTimerDisplay(millisUntilFinished);

                                currentQuestionTimeRemainingTextView.setText(formattedTimerDisplay);
//
                            }

                            @Override
                            public void onFinish() {
                                message.setTimeLeftToAnswer(0);
                                currentQuestionTimeRemainingCircularProgressBar.setProgress(100);

                            }
                        }.start();

                    }

                    currentQuestionRoot.setVisibility(View.VISIBLE);
                }
                else{
                    cancelTimer();
                    currentQuestionRoot.setVisibility(View.GONE);
                }

            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void cancelTimer() {

        if (countDownTimer != null) {
            //Log.i("offside", "CANCELLING!!! timerId: " + String.valueOf(viewHolder.countDownTimer.hashCode()));
            countDownTimer.cancel();
            countDownTimer = null;
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
            String privateGameId = OffsideApplication.getSelectedPrivateGameId();

            // this parameter will be null if the user does not answer
            String answerId = questionAnsweredEvent.getAnswerId();
            OffsideApplication.networkingService.requestPostAnswer(playerId, gameId, privateGameId, questionId, answerId, isSkipped, betSize);
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
                String msg = "Sorry! Answer wasn't sent! details: " + "gameid: " + postAnswerRequestInfo.getGameId() + " playerid: " + postAnswerRequestInfo.getPlayerId() + " questionid: " + postAnswerRequestInfo.getQuestionId() + " answerid: " + postAnswerRequestInfo.getAnswerId() + " isSkippped: " + postAnswerRequestInfo.isSkipped() + " betsize: " + postAnswerRequestInfo.getBetSize();
                final String msgPop = "Sorry! Answer wasn't sent!";

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Snackbar.make(root, msgPop, Snackbar.LENGTH_SHORT).show();
                    }
                }, 2000);

                ACRA.getErrorReporter().handleSilentException(new Throwable(msg));

            }
//            else{
//                OffsideApplication.getPlayerAssets().setPowerItems(OffsideApplication.getGameInfo().getPlayer().getPowerItems());
//                PlayerAssets playerAssets =OffsideApplication.getPlayerAssets();
//                EventBus.getDefault().post(playerAssets);
//
//            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePosition(PositionEvent positionEvent) {

        try {
            Position position = positionEvent.getPosition();
            String positionDisplay = String.format("%d/%d", position.getPrivateGamePosition(), position.getPrivateGameTotalPlayers());
            positionTextView.setText(positionDisplay);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveRewardEvent(RewardEvent rewardEvent) {
        try {
//            int rewardAmount = rewardEvent.getRewardAmount();
//            PlayerModel player = OffsideApplication.getGameInfo().getPlayer();
//            if (player == null || rewardAmount == 0)
//                return;
//
//            player.incrementRewardVideoWatchCount();
            //OffsideApplication.networkingService.setPowerItems(playerId, gameId, rewardAmount, true);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(PlayerModelEvent playerModelEvent) {
        try {
            PlayerModel updatedPlayer = playerModelEvent.getPlayerModel();
            if (updatedPlayer == null)
                return;


            OffsideApplication.playerAnswers = updatedPlayer.getPlayerAnswers();

            PlayerModel currentPlayer = OffsideApplication.getGameInfo().getPlayer();

            if (currentPlayer != null) {
                int oldOffsideCoinsValue = currentPlayer.getOffsideCoins();
                int newOffsideCoinsValue = updatedPlayer.getOffsideCoins();
                offsideCoins = newOffsideCoinsValue;
                OffsideApplication.getGameInfo().getPlayer().setOffsideCoins(offsideCoins);
                String formattedCoinsValue = Formatter.formatNumber(offsideCoins, Formatter.intCommaSeparator);
                offsideCoinsTextView.setText(formattedCoinsValue);
                if (newOffsideCoinsValue != oldOffsideCoinsValue) {
                    YoYo.with(Techniques.BounceIn).playOn(offsideCoinsImageView);
                }

                int oldPowerItems = currentPlayer.getPowerItems();
                int newPowerItems = updatedPlayer.getPowerItems();

                updatePowerItems(oldPowerItems, newPowerItems);
            }


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

            //not needed since playerActivities in the scoreboard is changing on any question/answer activity
            //check if scoreboard was changed
//            Scoreboard currentScoreboard = OffsideApplication.getScoreboard();

//            boolean isScoreboardsEquals = false;
//            if (!scoreboard.isForceUpdate() && currentScoreboard != null && currentScoreboard.getScores() != null && currentScoreboard.getScores().length == scores.length) {
//                Score[] currentScores = currentScoreboard.getScores();
//                for (int i = 0; i < currentScores.length; i++) {
//                    boolean isEqualScore = currentScores[i].getImageUrl().equals(scores[i].getImageUrl()) &&
//                            currentScores[i].getPosition() == scores[i].getPosition() &&
//                            currentScores[i].getOffsideCoins() == scores[i].getOffsideCoins();
//
//                    if (!isEqualScore)
//                        break;
//                    if (i == currentScores.length - 1)
//                        isScoreboardsEquals = true;
//                }
//
//            }
//            if (isScoreboardsEquals)
//                return;

            OffsideApplication.setScoreboard(scoreboard);

            generateScoreboard();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void generateScoreboard() {
        try {


            scoreboardRoot.removeAllViewsInLayout();

            int numberOfPlayers = OffsideApplication.getScoreboard().getScores().length;
            int previousRankCoins = 0;
            int awardResId = 0;

            for (final Score score : OffsideApplication.getScoreboard().getScores()) {

                ViewGroup layout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.scoreboard_item, scoreboardRoot, false);

                FrameLayout frameLayout = (FrameLayout) layout.getChildAt(0);
                ImageView playerImageView = (ImageView) frameLayout.getChildAt(0);
                TextView rankTextView = (TextView) frameLayout.getChildAt(1);
                ImageView awardImageView = (ImageView) frameLayout.getChildAt(2);
                TextView scoreTextView = (TextView) layout.getChildAt(1);

                int position = score.getPosition();
                int coins = score.getOffsideCoins();

                rankTextView.setText(String.format("%s", position));
                ImageHelper.loadImage(((Activity) getContext()), score.getImageUrl(), playerImageView, "LobbyActivity", true);

                if (position < 4 && position < numberOfPlayers) {

                    switch (position) {
                        case 1:
                            awardResId = R.mipmap.trophy_gold;
                            break;
                        case 2:
                            if (coins != previousRankCoins)
                                awardResId = R.mipmap.trophy_silver;
                            break;
                        case 3:
                            if (coins != previousRankCoins)
                                awardResId = R.mipmap.trophy_bronze;
                            break;
                        default:
                            awardResId = 0;

                    }
                    previousRankCoins = coins;

                }
                if (awardResId > 0 && position < numberOfPlayers && position < 4) {
                    ImageHelper.loadImage(getContext(), awardImageView, awardResId, false);
                    awardImageView.setVisibility(View.VISIBLE);
                } else
                    awardImageView.setVisibility(View.GONE);

                String formattedScoreValue = Formatter.formatNumber(score.getOffsideCoins(), Formatter.intCommaSeparator);
                scoreTextView.setText(formattedScoreValue);

                //<editor-fold desc="SET CLICK EVENT">
                playerImageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        showPlayerInGameActivityDialog(score.getScoreDetailedInfo(), score.getImageUrl(), score.getName());


                    }
                });
                //</editor-fold>


                scoreboardRoot.addView(layout);
            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }
    }

    public void showPlayerInGameActivityDialog(ScoreDetailedInfo scoreDetailedInfo, String playerImageUrl, String playerName) {

        final Dialog scoreDetailsDialog = new Dialog(getContext());
        scoreDetailsDialog.setContentView(R.layout.dialog_in_game_player_activity);

        if (scoreDetailedInfo != null) {

            ImageView playerImageView = scoreDetailsDialog.findViewById(R.id.digpa_player_image_view);
            ImageHelper.loadImage(((Activity) getContext()), playerImageUrl, playerImageView, "ChatFragment", true);

            TextView playerNameTextView = scoreDetailsDialog.findViewById(R.id.digpa_player_name_text_view);
            playerNameTextView.setText(playerName);

//            TextView playerAccuracyTextView = scoreDetailsDialog.findViewById(R.id.digpa_player_accuracy_text_view);
//            TextView playerTotalAnsweredToTotalQuestionsAskedTextView = scoreDetailsDialog.findViewById(R.id.digpa_player_total_answered_to_total_question_asked_ratio_text_view);

//            int totalQuestions = scoreDetailedInfo.getTotalQuestions();
//            int totalAnsweredQuestions = scoreDetailedInfo.getTotalAnsweredQuestions();
//            int totalCorrectAnswers = scoreDetailedInfo.getTotalCorrectAnswers();

//            float accuracy = totalAnsweredQuestions == 0 ? 0 : (float) totalCorrectAnswers / totalAnsweredQuestions;
//            String formattedAccuracy = Formatter.formatNumber(accuracy, Formatter.floatPercent);
//
//            playerAccuracyTextView.setText(formattedAccuracy);
//            playerTotalAnsweredToTotalQuestionsAskedTextView.setText(String.format("%d/%d", totalAnsweredQuestions, totalQuestions));

            Button closeButton = scoreDetailsDialog.findViewById(R.id.digpa_close_button);
            closeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    scoreDetailsDialog.cancel();
                }
            });

            LinearLayout playerActivitiesContainerRoot = scoreDetailsDialog.findViewById(R.id.digpa_player_activities_container_root);
            playerActivitiesContainerRoot.removeAllViewsInLayout();
            int totalPotentialEarnings = 0;
            for (PlayerActivity playerActivity : scoreDetailedInfo.getPlayerActivities()) {

                ViewGroup activitiesLayout = (ViewGroup) LayoutInflater.from(getContext()).inflate(R.layout.player_activity_item, playerActivitiesContainerRoot, false);
                ViewGroup layout = (ViewGroup) activitiesLayout.getChildAt(0);
                TextView questionTextTextView = (TextView) layout.getChildAt(0);
                TextView answerTextTextView = (TextView) layout.getChildAt(1);
                TextView betSizeTextView = (TextView) layout.getChildAt(2);
                TextView potentialEarnTextView = (TextView) layout.getChildAt(3);

                questionTextTextView.setText(playerActivity.getQuestionText());
                Answer answer = playerActivity.getAnswer();
                answerTextTextView.setText(answer != null ? answer.getAnswerText() : getString(R.string.lbl_not_answered));
                AnswerIdentifier answerIdentifier = playerActivity.getAnswerIdentifier();
                int betSize = answerIdentifier != null ? answerIdentifier.getBetSize() : 0;
                betSizeTextView.setText(String.format("%d", betSize));
                double multiplier = answer != null ? answer.getPointsMultiplier() : 0;
                int roundedPotentialEarnings = (int) Math.round(betSize * multiplier);
                totalPotentialEarnings += roundedPotentialEarnings;
                potentialEarnTextView.setText(String.valueOf(roundedPotentialEarnings));

                playerActivitiesContainerRoot.addView(activitiesLayout);

            }

            TextView playerTotalPotentialEarningsTextView = scoreDetailsDialog.findViewById(R.id.digpa_player_total_potential_earnings_text_view);
            //double totalPotentialEarnings = scoreDetailedInfo.getTotalPotentialEarnings();
            String formattedTotalPotentialEarnings = Formatter.formatNumber(Math.round(totalPotentialEarnings), Formatter.intCommaSeparator);
            playerTotalPotentialEarningsTextView.setText(formattedTotalPotentialEarnings);

            adjustDialogWidthToWindow(scoreDetailsDialog);
            scoreDetailsDialog.show();


        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceiveInGamePlayerAssetsUpdate(InGamePlayerAssetsUpdateEvent inGamePlayerAssetsUpdateEvent) {
        try {
            if (inGamePlayerAssetsUpdateEvent == null)
                return;

            int oldValue = inGamePlayerAssetsUpdateEvent.getOldValue();
            int newValue = inGamePlayerAssetsUpdateEvent.getNewValue();
            String assetType = inGamePlayerAssetsUpdateEvent.getAssetType();

            if (assetType.equals(InGamePlayerAssetsUpdateEvent.assetTypePowerItems)) { //POWER_ITEMS

                updatePowerItems(oldValue, newValue);
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkingErrorReceived(NetworkingErrorFixedEvent networkingErrorFixedEvent) {
        try {
            if (networkingErrorFixedEvent.isError()) {
                disconnectedMessageRoot.setVisibility(View.VISIBLE);

            } else {
                disconnectedMessageRoot.setVisibility(View.GONE);
                init();
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }

    private void updatePowerItems(int oldPowerItems, int newPowerItems) {
        try {
            OffsideApplication.getGameInfo().getPlayer().setPowerItems(newPowerItems);
            powerItems = newPowerItems;

            String formattedPowerItems = Formatter.formatNumber(newPowerItems, Formatter.intCommaSeparator);
            powerItemsTextView.setText(formattedPowerItems);

            if (newPowerItems != oldPowerItems) {

                Animation a = new RotateAnimation(0.0f, 360.0f,
                        Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
                        0.5f);
                a.setRepeatCount(1);
                a.setDuration(1000);
                powerItemsImageView.startAnimation(a);

            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    private String getAnswerText(Question question, String answerId) {

        try {
            String answerText = "";
            Answer[] answers = question.getAnswers();
            for (int i = 0; i < answers.length; i++) {
                Answer answer = answers[i];
                if (answer.getId().equals(answerId)) {
                    answerText = answer.getAnswerText();
                    break;
                }

            }
            return answerText;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return "";

    }

    private String formatTimerDisplay(long millisUntilFinished) {

        try {
            String timerDisplayFormat = "";

            int min = (int) Math.floor(millisUntilFinished / 1000 / 60);
            int sec = ((int) Math.floor(millisUntilFinished / 1000) % 60);
            String minString = Integer.toString(min);
            String secString = Integer.toString(sec);

            if (min < 10)
                minString = "0" + minString;
            if (sec < 10)
                secString = "0" + secString;

            timerDisplayFormat = minString + ":" + secString;
            return timerDisplayFormat;


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return Float.toString(millisUntilFinished);

        }

    }

    public void adjustDialogWidthToWindow(Dialog dialog) {

        try {
            WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
            Window window = dialog.getWindow();
            lp.copyFrom(window.getAttributes());
            //This makes the dialog take up the full width
            lp.width = WindowManager.LayoutParams.MATCH_PARENT;
            lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
            window.setAttributes(lp);

            int dialogWidth = lp.width;
            int dialogHeight = lp.height;
            int MAX_HEIGHT = 200;

            if (dialogHeight > MAX_HEIGHT) {
                dialog.getWindow().setLayout(dialogWidth, MAX_HEIGHT);
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }


}
