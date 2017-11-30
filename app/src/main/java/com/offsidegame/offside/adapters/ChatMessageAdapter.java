package com.offsidegame.offside.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.facebook.share.model.ShareHashtag;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareButton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.SlotActivity;
import com.offsidegame.offside.events.InGamePlayerAssetsUpdateEvent;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.RewardEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Winner;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by KFIR on 11/21/2016.
 */

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {


    private Context context;
    private Map<String, AnswerIdentifier> playerAnswers;
    private RewardedVideoAd rewardedVideoAd;
    private String rewardedVideoAdAppUnitId;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages, Map<String, AnswerIdentifier> playerAnswers) {
        super(context, 0, chatMessages);
        this.context = context;
        this.playerAnswers = playerAnswers;
    }

    private class ViewHolder {

        //<editor-fold desc="VIEWHOLDER PROPERTIES">

        public LinearLayout incomingMessagesRoot;

        public ImageView incomingClosedQuestionBotImageView;
        public ImageView incomingClosedQuestionPlayerProfilePictureImageView;

        public TextView incomingTextMessageTextView;
        public TextView outgoingUserSentTextView;
        public TextView incomingUserSentTextView;

        public LinearLayout incomingQuestionStatRoot;
        public TextView incomingQuestionStatTextView;

        public LinearLayout incomingQuestionRoot;
        public TextView incomingQuestionTextView;

        public TextView incomingQuestionProcessedQuestionTitleTextView;
        public TextView incomingQuestionAskedNotEnoughCoinsTextView;
        public LinearLayout incomingAnswersRoot;

        public LinearLayout IncomingAnswers12Root;

        public TextView[] answerReturnTextViews = new TextView[4];
        //public TextView[] answerPercentTextViews = new TextView[4];
        public TextView[] answerTextViews = new TextView[4];
        public LinearLayout[] answerRoots = new LinearLayout[4];

        public LinearLayout incomingBetPanelRoot;

        public LinearLayout[] betSizeOptionsRoots = new LinearLayout[2];
        public TextView incomingBalanceTextView;

        public LinearLayout incomingDoubleupBetRoot;

        public LinearLayout incomingTimeToAnswerRoot;
        public TextView incomingTimeToAnswerTextDisplayTextView;
        public ProgressBar incomingTimeToAnswerProgressBar;
        public TextView incomingQuestionProcessedQuestionTimeExpiredMessageTextView;

        public LinearLayout incomingProcessingQuestionRoot;
        public TextView incomingProcessingQuestionTextView;
        public TextView incomingSelectedAnswerTitleTextView;
        public TextView incomingSelectedAnswerTextView;
        public LinearLayout incomingProcessingQuestionPossibleReturnValueMessageRoot;
        public TextView incomingSelectedAnswerReturnTextView;


        public LinearLayout incomingClosedQuestionRoot;
        public TextView incomingClosedQuestionTextView;
        public TextView incomingCorrectWrongTitleTextView;
        public TextView incomingCorrectAnswerTextView;
        public TextView incomingCorrectAnswerReturnTextView;
        public TextView incomingFeedbackPlayerTextView;
        public TextView incomingTimeSentTextView;
        public LinearLayout incomingPlayerAnswerRoot;
        public TextView incomingPlayerAnswerTextView;


        public LinearLayout outgoingMessagesRoot;
        public ImageView outgoingProfilePictureImageView;
        public TextView outgoingTextMessageTextView;
        public TextView outgoingTimeSentTextView;

        public Question question;
        public int timeToAnswer;
        public ChatMessage chatMessage;
        public CountDownTimer countDownTimer;
        public boolean isMessageFromBot;
        //get coins message type
        public LinearLayout incomingGetCoinsMessageRoot;
        public TextView incomingGetCoinsNotEnoughCoinsMessageTextView;
        public TextView incomingGetCoinsWatchRewardVideoActionTextView;
        public TextView incomingGetCoinsBuyCoinsActionTextView;
        public TextView incomingGetCoinsSlotMachineActionTextView;
        public LinearLayout incomingGetCoinsLoadingRoot;
        public LinearLayout incomingGetCoinsPlayerOptionsRoot;
        public TextView incomingGetCoinsLoadingMessageTextView;


        //missed question
        public LinearLayout incomingMissedQuestionRoot;
        public TextView incomingMissedQuestionTextView;

        //winner message type
        public LinearLayout incomingWinnerMessageRoot;
        public TextView incomingWinnerPointsTextView;
        public LinearLayout incomingWinnersRoot;
        //public GifImageView incomingWinnerTrophyGifImageView;

        //bars display for processed question
        public LinearLayout incomingProcessedQuestionAnswersBarsRoot;

        //social feed
        public LinearLayout incomingSocialFeedRoot;
        public TextView incomingSocialFeedTextMessageTextView;

        public ImageView incomingSocialFeedPictureImageView;
        public ShareButton facebookShareButton;


        //</editor-fold>

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item, parent, false);
                viewHolder = new ViewHolder();

                //<editor-fold desc="FIND WIDGETS">
                getIDs(viewHolder, convertView);
                //</editor-fold>
                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
                if (viewHolder.countDownTimer != null) {
                    viewHolder.countDownTimer.cancel();
                    viewHolder.countDownTimer = null;
                    viewHolder.incomingTimeToAnswerTextDisplayTextView.setText("00:00");
                }
            }

            viewHolder.chatMessage = getItem(position);
            if (viewHolder.chatMessage == null)
                return convertView;

            String chatMessageType = viewHolder.chatMessage.getMessageType();

            if (chatMessageType == null)
                return convertView;

            viewHolder.isMessageFromBot = viewHolder.chatMessage.getSentByUserName().equals("Sidekick");


            if (chatMessageType.equals(OffsideApplication.getMessageTypeText()))  //"TEXT"
            {
                generateTextChatMessage(viewHolder);

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion()) || //"ASKED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion()) ||  //"PROCESSED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion())) //"CLOSED_QUESTION"
            {
                generateQuestionChatMessage(viewHolder);

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeGetCoins()))  //"GET_COINS"
            {
                generateGetCoinsChatMessage(viewHolder);

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeWinner()))  //"WINNER"
            {
                generateWinnerChatMessage(viewHolder);

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeSocialFeed()))  //"SOCIAL_FEED"
            {
                generateSocialFeedChatMessage(viewHolder);

            }

            return convertView;
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


        return null;
    }

    private void getIDs(ViewHolder viewHolder, View convertView) {
        //text message
        viewHolder.incomingMessagesRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_messages_root);
        viewHolder.incomingTextMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_text_message_text_view);
        viewHolder.outgoingUserSentTextView = (TextView) convertView.findViewById(R.id.cm_outgoing_user_sent_text_view);
        viewHolder.incomingUserSentTextView = (TextView) convertView.findViewById(R.id.cm_incoming_user_sent_text_view);

        viewHolder.incomingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_question_root);
        viewHolder.incomingQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_text_view);

        viewHolder.incomingQuestionStatRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_question_stat_root);
        viewHolder.incomingQuestionStatTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_stat_text_view);


        viewHolder.incomingAnswersRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_answers_root);

        viewHolder.IncomingAnswers12Root = (LinearLayout) convertView.findViewById(R.id.cm_incoming_answers_1_2_root);

        for (int i = 0; i < 4; i++) {
            final int answerNumber = i + 1;
            final int returnTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_return_text_view", "id", context.getPackageName());
            //final int percentTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_percent_text_view", "id", context.getPackageName());
            final int answerTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_text_view", "id", context.getPackageName());
            final int answerRootResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_root", "id", context.getPackageName());
            viewHolder.answerReturnTextViews[i] = (TextView) convertView.findViewById(returnTextViewResourceId);
            //viewHolder.answerPercentTextViews[i] = (TextView) convertView.findViewById(percentTextViewResourceId);
            viewHolder.answerTextViews[i] = (TextView) convertView.findViewById(answerTextViewResourceId);
            viewHolder.answerRoots[i] = (LinearLayout) convertView.findViewById(answerRootResourceId);

        }


        viewHolder.incomingBetPanelRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_bet_panel_root);
        viewHolder.incomingDoubleupBetRoot = (LinearLayout) convertView.findViewById(R.id.incoming_double_up_bet_root);


//        for (int i = 0; i < 2; i++) {
//            final int optionNumber = i;
//            final int incomingBetOptionTextViewId = context.getResources().getIdentifier("cm_incoming_bet_option_" + optionNumber + "_root", "id", context.getPackageName());
//            viewHolder.betSizeOptionsRoots[i] = (LinearLayout) convertView.findViewById(incomingBetOptionTextViewId);
//
//        }

        viewHolder.incomingBalanceTextView = (TextView) convertView.findViewById(R.id.cm_incoming_balance_text_view);

        viewHolder.incomingTimeToAnswerRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_time_to_answer_root);

        viewHolder.incomingTimeToAnswerTextDisplayTextView = (TextView) convertView.findViewById(R.id.cm_incoming_time_to_answer_text_display_text_view);
        viewHolder.incomingTimeToAnswerProgressBar = (ProgressBar) convertView.findViewById(R.id.cm_incoming_time_to_answer_progress_bar);
        viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_processed_question_time_expired_message_text_view);

        viewHolder.incomingProcessingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_processing_question_root);
        viewHolder.incomingSelectedAnswerTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_title_text_view);
        viewHolder.incomingSelectedAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_text_view);
        viewHolder.incomingProcessingQuestionPossibleReturnValueMessageRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_processing_question_possible_return_value_message_root);
        viewHolder.incomingSelectedAnswerReturnTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_return_text_view);
        viewHolder.incomingQuestionProcessedQuestionTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_processed_question_title_text_view);
        viewHolder.incomingQuestionAskedNotEnoughCoinsTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_asked_not_enough_coins_text_view);

        viewHolder.incomingProcessingQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_processing_question_text_view);

        viewHolder.incomingClosedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_closed_question_root);
        viewHolder.incomingClosedQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_closed_question_text_view);
        viewHolder.incomingCorrectWrongTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_correct_wrong_title_text_view);
        viewHolder.incomingCorrectAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_correct_answer_text_view);
        viewHolder.incomingCorrectAnswerReturnTextView = (TextView) convertView.findViewById(R.id.cm_incoming_correct_answer_return_text_view);
        viewHolder.incomingFeedbackPlayerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_feedback_player_text_view);
        viewHolder.incomingTimeSentTextView = (TextView) convertView.findViewById(R.id.cm_incoming_time_sent_text_view);

        viewHolder.incomingPlayerAnswerRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_player_answer_root);
        viewHolder.incomingPlayerAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_player_answer_text_view);


        viewHolder.outgoingMessagesRoot = (LinearLayout) convertView.findViewById(R.id.cm_outgoing_messages_root);
        viewHolder.outgoingProfilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_outgoing_profile_picture_image_view);
        viewHolder.outgoingTextMessageTextView = (TextView) convertView.findViewById(R.id.cm_outgoing_text_message_text_view);
        viewHolder.outgoingTimeSentTextView = (TextView) convertView.findViewById(R.id.cm_outgoing_time_sent_text_view);

        //get coins message
        viewHolder.incomingGetCoinsMessageRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_get_coins_message_root);
        viewHolder.incomingGetCoinsWatchRewardVideoActionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_get_coins_watch_reward_video_action_text_view);
        viewHolder.incomingGetCoinsBuyCoinsActionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_get_coins_buy_coins_action_text_view);
        viewHolder.incomingGetCoinsSlotMachineActionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_get_coins_slot_machine_action_text_view);
        viewHolder.incomingGetCoinsNotEnoughCoinsMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_get_coins_not_enough_coins_message_text_view);

        viewHolder.incomingGetCoinsLoadingRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_get_coins_loading_root);
        viewHolder.incomingGetCoinsPlayerOptionsRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_get_coins_player_options_root);
        viewHolder.incomingGetCoinsLoadingMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_get_coins_loading_message_text_view);

        viewHolder.incomingMissedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_missed_question_root);
        viewHolder.incomingMissedQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_missed_question_text_view);

        //winner message
        viewHolder.incomingWinnerMessageRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_winner_message_root);
        //viewHolder.incomingWinnerPointsTextView = (TextView) convertView.findViewById(R.id.cm_incoming_winner_points_text_view);
        viewHolder.incomingWinnersRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_winners_root);

        viewHolder.incomingProcessedQuestionAnswersBarsRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_processed_question_answers_bars_root);

        //social feed
        viewHolder.incomingSocialFeedRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_social_feed_root);

        viewHolder.incomingSocialFeedTextMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_social_feed_text_message_text_view);
        viewHolder.incomingSocialFeedPictureImageView = (ImageView) convertView.findViewById(R.id.cm_incoming_social_feed_image_view);
        viewHolder.facebookShareButton = (ShareButton) convertView.findViewById(R.id.cm_facebook_share_button);

        viewHolder.incomingClosedQuestionBotImageView = convertView.findViewById(R.id.cm_incoming_closed_question_bot_image_view);
        viewHolder.incomingClosedQuestionPlayerProfilePictureImageView = convertView.findViewById(R.id.cm_incoming_closed_question_player_profile_picture_image_view);



    }

    private void generateTextChatMessage(ViewHolder viewHolder) {

        try {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            //visibility reset
            resetWidgetsVisibility(viewHolder);
            if(!viewHolder.chatMessage.getSentByUserId().equals(OffsideApplication.getPlayerId()))
                viewHolder.chatMessage.setIncoming(true);

            if (viewHolder.chatMessage.isIncoming()) {

                viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
                viewHolder.incomingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());

                if(viewHolder.isMessageFromBot){
                    int playerColorId = getContext().getResources().getColor(R.color.senderNameColor);
                    viewHolder.incomingUserSentTextView.setTextColor(playerColorId);
                }
                else {
                    String playerColor = OffsideApplication.getPlayerAssets().getPlayerColor();
                    int playerColorId = Color.parseColor(playerColor);
                    viewHolder.incomingUserSentTextView.setTextColor(playerColorId);
                }


                viewHolder.incomingTextMessageTextView.setText(viewHolder.chatMessage.getMessageText());

                //background set
//                if (viewHolder.isMessageFromBot)
//                    viewHolder.incomingTextMessageTextView.setBackgroundResource(R.drawable.shape_bg_incoming_bubble_from_bot);
//                else
//                    viewHolder.incomingTextMessageTextView.setBackgroundResource(R.drawable.shape_bg_incoming_bubble);

                //visibility set
                viewHolder.incomingMessagesRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingTextMessageTextView.setVisibility(View.VISIBLE);

            } else {
                viewHolder.outgoingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
                viewHolder.outgoingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());
                viewHolder.outgoingTextMessageTextView.setText(viewHolder.chatMessage.getMessageText());

                //visibility set

                viewHolder.outgoingMessagesRoot.setVisibility(View.VISIBLE);

            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void generateGetCoinsChatMessage(final ViewHolder viewHolder) {

        try {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Uri profilePictureUri = Uri.parse(viewHolder.chatMessage.getImageUrl());

            //visibility reset
            resetWidgetsVisibility(viewHolder);

            viewHolder.incomingGetCoinsPlayerOptionsRoot.setVisibility(View.VISIBLE);

            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
            viewHolder.incomingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());

            viewHolder.incomingGetCoinsNotEnoughCoinsMessageTextView.setText(viewHolder.chatMessage.getMessageText());

            final PlayerModel player = OffsideApplication.getGameInfo().getPlayer();

            if (player != null && player.getRewardVideoWatchCount() < OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame()) {

                viewHolder.incomingGetCoinsWatchRewardVideoActionTextView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        viewHolder.incomingGetCoinsPlayerOptionsRoot.setVisibility(View.GONE);
                        viewHolder.incomingGetCoinsLoadingRoot.setVisibility(View.VISIBLE);
                        viewHolder.incomingGetCoinsLoadingMessageTextView.setText("Loading " + Integer.toString(player.getRewardVideoWatchCount() + 1) + " of " + Integer.toString(OffsideApplication.getGameInfo().getMaxAllowedRewardVideosWatchPerGame()) + " allowed videos");

                        loadRewardedVideoAd();
                    }
                });

                // Use an activity context to get the rewarded video instance.
                rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(context);
                rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {

                    //<editor-fold desc="RewardedVideoAdListener Methods">
                    @Override
                    public void onRewarded(RewardItem reward) {

                        int rewardAmount = reward.getAmount();

                        viewHolder.incomingGetCoinsPlayerOptionsRoot.setVisibility(View.GONE);
                        viewHolder.incomingGetCoinsLoadingRoot.setVisibility(View.GONE);

                        //Toast.makeText(context, "onRewarded! currency: " + reward.getType() + "  amount: " +  reward.getAmount(), Toast.LENGTH_SHORT).show();

                        EventBus.getDefault().post(new RewardEvent(rewardAmount));

                    }

                    @Override
                    public void onRewardedVideoAdLeftApplication() {

                        //Toast.makeText(context, "onRewardedVideoAdLeftApplication",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoAdClosed() {
                        // Toast.makeText(context, "onRewardedVideoAdClosed", Toast.LENGTH_SHORT).show();
                        //rewardedVideoAd = null;


                    }

                    @Override
                    public void onRewardedVideoAdFailedToLoad(int errorCode) {
                        //Toast.makeText(context, "onRewardedVideoAdFailedToLoad", Toast.LENGTH_SHORT).show();
                        //rewardedVideoAd = null;

                        //viewHolder.incomingGetCoinsMessageRoot.setVisibility(View.GONE);
                    }

                    @Override
                    public void onRewardedVideoAdLoaded() {
                        try {

                            if (rewardedVideoAd.isLoaded())
                                rewardedVideoAd.show();
                            //Toast.makeText(context, "onRewardedVideoAdLoaded", Toast.LENGTH_SHORT).show();

                        } catch (Exception ex) {

                            ACRA.getErrorReporter().handleException(ex);

                        }

                    }

                    @Override
                    public void onRewardedVideoAdOpened() {
                        //Toast.makeText(context, "onRewardedVideoAdOpened", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onRewardedVideoStarted() {
                        //Toast.makeText(context, "onRewardedVideoStarted", Toast.LENGTH_SHORT).show();
                    }


                    //</editor-fold>

                });

            } else  //user exceeds number of allowed videos to watch in a single game
            {
                viewHolder.incomingGetCoinsWatchRewardVideoActionTextView.setVisibility(View.GONE);
            }

            viewHolder.incomingGetCoinsBuyCoinsActionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    viewHolder.incomingGetCoinsPlayerOptionsRoot.setVisibility(View.GONE);
                    viewHolder.incomingGetCoinsLoadingRoot.setVisibility(View.VISIBLE);

                    //ToDo: add IAP logic here
                }
            });

            viewHolder.incomingGetCoinsSlotMachineActionTextView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, SlotActivity.class);
                    getContext().startActivity(intent);
                }
            });

            //visibility set
            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingGetCoinsMessageRoot.setVisibility(View.VISIBLE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void loadRewardedVideoAd() {
        try {

            rewardedVideoAdAppUnitId = context.getString(R.string.rewarded_video_ad_unit_id_key);

            if (rewardedVideoAd == null)
                return;

            if (!rewardedVideoAd.isLoaded())
                rewardedVideoAd.loadAd(rewardedVideoAdAppUnitId, new AdRequest.Builder().build());

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void generateWinnerChatMessage(final ViewHolder viewHolder) {

        try {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            //visibility reset
            resetWidgetsVisibility(viewHolder);

            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
            viewHolder.incomingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());

            //viewHolder.incomingWinnerPointsTextView.setText(viewHolder.chatMessage.getMessageText());

            List<Winner> winners = viewHolder.chatMessage.getWinners();
            viewHolder.incomingWinnersRoot.removeAllViews();

            for (Winner winner : winners) {

                ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.winner_item, viewHolder.incomingWinnersRoot, false);
                TextView winnerScoreTextView = (TextView) layout.getChildAt(0);
                ImageView winnerPrizeImageView = (ImageView) layout.getChildAt(1);
                ImageView winnerPictureImageView = (ImageView) layout.getChildAt(2);
                TextView winnerNameTextView = (TextView) layout.getChildAt(3);

                Uri winnerProfilePictureUri = Uri.parse(winner.getImageUrl());
                winnerScoreTextView.setText(Integer.toString(winner.getOffsideCoins()));
                int resourceId = winner.getPosition() == 1 ? R.mipmap.trophy_gold : winner.getPosition() == 2 ? R.mipmap.trophy_silver : R.mipmap.trophy_bronze;
                if (winner.getPosition() == 2)
                    layout.setPadding(0, 30, 0, 0);


                if (winner.getPosition() == 3)
                    layout.setPadding(0, 60, 0, 0);

                winnerPrizeImageView.setImageResource(resourceId);

                ImageHelper.loadImage(context,winnerPictureImageView,winnerProfilePictureUri,true);
                winnerNameTextView.setText(winner.getPlayerName());
                viewHolder.incomingWinnersRoot.addView(layout);
            }




            //visibility set
            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingWinnerMessageRoot.setVisibility(View.VISIBLE);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void generateQuestionChatMessage(final ViewHolder viewHolder) {

        try {

            //chat message properties
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
            viewHolder.incomingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());
            String chatMessageType = viewHolder.chatMessage.getMessageType();

            //bind Question object to the ui elements
            final Gson gson = new GsonBuilder().create();
            viewHolder.question = gson.fromJson(viewHolder.chatMessage.getMessageText(), Question.class);
            final String questionId = viewHolder.question.getId();

            final boolean isAskedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion());
            final boolean isProcessedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion());
            final boolean isClosedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion());
            final boolean isPlayerAnsweredQuestion = playerAnswers.containsKey(questionId);
            final boolean isDebate = viewHolder.question.getQuestionType().equals(OffsideApplication.getQuestionTypeDebate());
            final boolean isOpenForAnswering = viewHolder.chatMessage.getTimeLeftToAnswer() > 0;
            final boolean isTimerRequired = (viewHolder.question.getGamePhase().equals(OffsideApplication.getInGamePhaseName()) && (
                    viewHolder.question.getQuestionType().equals(OffsideApplication.getQuestionTypeFun()) ||
                            viewHolder.question.getQuestionType().equals(OffsideApplication.getQuestionTypePrediction()))
            );

            resetWidgetsVisibility(viewHolder);

            int gameMinBetSize = OffsideApplication.getGameInfo().getMinBetSize();

            final int playerMinBetSize = OffsideApplication.getGameInfo().getPlayer().getOffsideCoins() < gameMinBetSize ? 0 : gameMinBetSize;


            //open question but user already answered it
            if (isAskedQuestion) {

                viewHolder.incomingQuestionTextView.setText(viewHolder.question.getQuestionText());
                if (isPlayerAnsweredQuestion) {
                    removeClickListenerFromAnswers(viewHolder);
                    String userAnswerId = "";
                    final AnswerIdentifier answerIdentifier = playerAnswers.get(questionId);
                    if (!answerIdentifier.isSkipped()) {
                        userAnswerId = answerIdentifier.getAnswerId();
                    }

                    setAnswersVisibility(viewHolder, viewHolder.question.getAnswers(), playerMinBetSize, isAskedQuestion, isProcessedQuestion);

                    setStyleForSelectedAnswer(viewHolder, userAnswerId);
                    viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
                    viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
                    viewHolder.incomingQuestionTextView.setVisibility(View.VISIBLE);
                    viewHolder.incomingAnswersRoot.setVisibility(View.VISIBLE);


//                    int betSize = answerIdentifier.getBetSize();
//
//                    final Answer answerOfTheUser;
//                    int returnValue = 0;
//
//                    if (answerNumber != 0) {
//                        answerOfTheUser = viewHolder.question.getAnswers()[answerNumber - 1];
//                        viewHolder.incomingSelectedAnswerTextView.setText(answerOfTheUser.getAnswerText());
//                        returnValue = (int) (betSize * answerOfTheUser.getPointsMultiplier());
//                        final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
//                        viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);
//                        viewHolder.incomingSelectedAnswerTextView.setVisibility(View.VISIBLE);
//                    }
//
//                    //set values to widgets
//                    viewHolder.incomingProcessingQuestionTextView.setText(viewHolder.question.getQuestionText());
//
//                    viewHolder.incomingSelectedAnswerReturnTextView.setText(Integer.toString(returnValue));

                    if (answerIdentifier.isSkipped())
                        //viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
                        viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_seems_like_you_skipped_this_question);

//                    else {
//                        viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);
//                        viewHolder.incomingSelectedAnswerTextView.setVisibility(View.VISIBLE);
//                    }


                    //visibility set

                    //viewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);
                    viewHolder.incomingQuestionRoot.setVisibility(View.VISIBLE);
                    viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                    return;

                } else if (!isOpenForAnswering) {
                    viewHolder.incomingMissedQuestionTextView.setText(viewHolder.question.getQuestionText());
                    viewHolder.incomingMissedQuestionRoot.setVisibility(View.VISIBLE);
                    viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                    return;

                }

            }

            final Answer[] answers = viewHolder.question.getAnswers();

            if (isAskedQuestion || isProcessedQuestion) {

                //<editor-fold desc="ASKED OR PROCESSED QUESTION">

                viewHolder.incomingQuestionTextView.setText(viewHolder.question.getQuestionText());

                setAnswersVisibility(viewHolder, answers, playerMinBetSize, isAskedQuestion, isProcessedQuestion);

                if (isAskedQuestion) {

                    //<editor-fold desc="ASKED QUESTION">
                    String questionStatText = viewHolder.question.getQuestionStatText();
                    if (questionStatText.length() > 0) {
                        viewHolder.incomingQuestionStatTextView.setText(questionStatText);
                        viewHolder.incomingQuestionStatRoot.setVisibility(View.VISIBLE);
                    }

                    //set click event to doubleup button
                    viewHolder.incomingDoubleupBetRoot.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                            //update answers return value based on betSize
                            int betSize = playerMinBetSize * 2;
                            for (int i = 0; i < answers.length; i++) {
                                final int returnValue = (int) Math.round(answers[i].getPointsMultiplier() * betSize);
                                viewHolder.answerReturnTextViews[i].setText(String.valueOf(returnValue));
                                answers[i].setScore(returnValue);
                                answers[i].setSelectedBetSize(betSize);
                            }

                            viewHolder.incomingDoubleupBetRoot.setOnClickListener(null);
                            viewHolder.incomingDoubleupBetRoot.setBackgroundResource(R.drawable.shape_bg_rectangle_answer_unselected);

                            int oldValue = OffsideApplication.getGameInfo().getPlayer().getPowerItems();
                            int newValue = oldValue==0 ? 0 :oldValue-1;

                            EventBus.getDefault().post(new InGamePlayerAssetsUpdateEvent(InGamePlayerAssetsUpdateEvent.assetTypePowerItems,oldValue,newValue));

                        }
                    });

                    //decide if to display a bet option, based on number of balls.
                    boolean isAllowToDoubleup = OffsideApplication.getGameInfo().getPlayer().getPowerItems() > 0 && OffsideApplication.getGameInfo().getPlayer().getOffsideCoins() >= 2 * gameMinBetSize;
                    boolean isHasMinRequiredCoinsToBet = playerMinBetSize >= gameMinBetSize;

                    if (!isAllowToDoubleup) {
                        viewHolder.incomingDoubleupBetRoot.setOnClickListener(null);
                        viewHolder.incomingDoubleupBetRoot.setBackgroundResource(R.drawable.shape_bg_rectangle_answer_unselected);
                    }
                    else{
                        viewHolder.incomingDoubleupBetRoot.setBackgroundResource(R.drawable.shape_bg_button_double_up);
                    }


                    if (isHasMinRequiredCoinsToBet) {

                        int betSize = playerMinBetSize;
                        for (int i = 0; i < answers.length; i++) {
                            final int returnValue = (int) Math.round(answers[i].getPointsMultiplier() * betSize);
                            viewHolder.answerReturnTextViews[i].setText(String.valueOf(returnValue));
                            answers[i].setScore(returnValue);
                            answers[i].setSelectedBetSize(betSize);
                        }

                    }




////                   //set on click event to bet options
//                    for (int i = 0; i < viewHolder.betSizeOptionsRoots.length; i++) {
//                        viewHolder.betSizeOptionsRoots[i].setTag(i);
//                        viewHolder.betSizeOptionsRoots[i].setOnClickListener(new View.OnClickListener() {
//                            @Override
//                            public void onClick(View view) {
//                                int index = (int) view.getTag();
//                                //update answers return value based on betSize
//                                int betSize = playerMinBetSize * (index + 1);
//                                for (int i = 0; i < answers.length; i++) {
//                                    final int returnValue = (int) Math.round(answers[i].getPointsMultiplier() * betSize);
//                                    viewHolder.answerReturnTextViews[i].setText(String.valueOf(returnValue));
//                                    answers[i].setScore(returnValue);
//                                    answers[i].setSelectedBetSize(betSize);
//                                }
//
//                                //update betSize button background
////                                for (int j = 0; j < viewHolder.betSizeOptionsRoots.length; j++) {
////                                    if (j == index) {
////                                        viewHolder.betSizeOptionsRoots[j].setBackgroundResource(R.drawable.shape_bg_rectangle_bordered);
////                                        //viewHolder.betSizeOptionsRoots[j].setTextColor(ContextCompat.getColor(context, R.color.chatPrimary));
////                                    } else {
////                                        viewHolder.betSizeOptionsRoots[j].setBackgroundResource(R.drawable.shape_bg_rectangle_gray_no_border);
////                                        //viewHolder.betSizeOptionsRoots[j].setTextColor(ContextCompat.getColor(context, R.color.chatPrimary));
////                                    }
////
////                                }
//
//                                //update balance
////                                int postBetOffsideCoins = OffsideApplication.getAssetType() - betSize;
////                                viewHolder.incomingBalanceTextView.setText(String.valueOf(postBetOffsideCoins));
//
//                            }
//                        });
//
//                        //viewHolder.betSizeOptionsRoots[i].setVisibility(View.INVISIBLE);
//
//                        //decide if to display a bet option, based on number of balls.
//
//                       boolean isHasMinRequiredCoinsToBet = playerMinBetSize >= gameMinBetSize;
//
////                        if (isHasMinRequiredCoinsToBet) {
////                            viewHolder.betSizeOptionsRoots[i].setBackgroundResource(i == 0 ? R.drawable.shape_bg_rectangle_bordered : R.drawable.shape_bg_rectangle_gray_no_border);
////                            viewHolder.betSizeOptionsRoots[i].setVisibility(View.VISIBLE);
////                            if (i == 0)
////                                viewHolder.betSizeOptionsRoots[0].performClick();
////                            else if (!isAllowToDoubleup) {
////                                viewHolder.betSizeOptionsRoots[i].setVisibility(View.GONE);
////                                viewHolder.betSizeOptionsRoots[i].setOnClickListener(null);
////                            }
////
////
////                        } else {
////                            viewHolder.betSizeOptionsRoots[i].setOnClickListener(null);
////                        }
//
//
//                    }


                    //set the timeToAskQuestion timer
                    //time to answer was attached to chat message and is updated in the server using timer
                    viewHolder.timeToAnswer = viewHolder.chatMessage.getTimeLeftToAnswer();
                    //Log.i("offside","questionId : "+ viewHolder.question.getQuestionText() + "- timeToAnswer: "+String.valueOf(viewHolder.timeToAnswer));

//                //in case user opened app in the middle of asked question
//                // time to answer is taken from the chat
//                int updatedTimeToAnswerFromChat = chatMessage.getTimeLeftToAnswer();
//                if (updatedTimeToAnswerFromChat > 0)
//                    timeToAnswer = updatedTimeToAnswerFromChat;

                    int progressBarMaxValue = viewHolder.timeToAnswer;
                    viewHolder.incomingTimeToAnswerProgressBar.setMax(progressBarMaxValue);

                    //viewHolder.messageId = viewHolder.question.getId();

                    if (viewHolder.timeToAnswer > 0) {
                        if (viewHolder.countDownTimer != null) {
                            //Log.i("offside", "CANCELLING!!! timerId: " + String.valueOf(viewHolder.countDownTimer.hashCode()));
                            viewHolder.countDownTimer.cancel();
                            viewHolder.countDownTimer = null;
                        }

                        viewHolder.countDownTimer = new CountDownTimer(viewHolder.timeToAnswer, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                viewHolder.chatMessage.setTimeLeftToAnswer((int) millisUntilFinished);
                                //Log.i("offside","timerId: "+String.valueOf(this.hashCode())+" - questionId : "+ viewHolder.messageId+ " question text: "+ viewHolder.question.getQuestionText()+ " - secondsLeft: "+ Math.round((int)millisUntilFinished/1000.0f) );
                                viewHolder.incomingTimeToAnswerProgressBar.setProgress(Math.round((float) millisUntilFinished));

                                String formattedTimerDisplay = formatTimerDisplay(millisUntilFinished);

                                viewHolder.incomingTimeToAnswerTextDisplayTextView.setText(formattedTimerDisplay);
//
                            }

                            @Override
                            public void onFinish() {
                                //user did not answer this question, we select random answer
                                viewHolder.chatMessage.setTimeLeftToAnswer(0);
                                viewHolder.incomingTimeToAnswerProgressBar.setProgress(0);
                                boolean isAnswered = playerAnswers.containsKey(questionId);
                                if (!isAnswered) {
//                                    int answersCount = viewHolder.question.getAnswers().length;
//                                    int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
//                                    Answer randomAnswer = viewHolder.question.getAnswers()[selectedAnswerIndex];
                                    postAnswer(viewHolder.question, null, null, viewHolder);
                                }
                            }
                        }.start();
                    }

                    //set on click event to answers
                    for (int i = 0; i < answers.length; i++) {
                        final Answer clickedAnswer = answers[i];
                        //final int returnValue = Integer.parseInt( viewHolder.answerReturnTextViews[i].getText().toString());
                        viewHolder.answerRoots[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                postAnswer(viewHolder.question, clickedAnswer, view, viewHolder);
                            }
                        });
                    }


                    //we don't have enough coins to play
                    if ((OffsideApplication.getGameInfo().getPlayer().getOffsideCoins() < gameMinBetSize) && !isDebate) {

                        viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
                        viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setText(R.string.lbl_you_are_out_of_coins);
                        viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setVisibility(View.VISIBLE);


                    } else {
                        viewHolder.incomingBetPanelRoot.setVisibility(View.VISIBLE);

                    }

                    //hiding elements irrelevant to Debate question
                    if (isDebate) {
                        viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setVisibility(View.GONE);
                        viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);

                        for (int i = 0; i < viewHolder.answerReturnTextViews.length; i++) {
                            viewHolder.answerReturnTextViews[i].setVisibility(View.GONE);
                        }
                        viewHolder.incomingProcessingQuestionPossibleReturnValueMessageRoot.setVisibility(View.GONE);
                    }

                    viewHolder.incomingAnswersRoot.setVisibility(View.VISIBLE);
                    viewHolder.incomingTimeToAnswerRoot.setVisibility(View.VISIBLE);


                    //</editor-fold>

                }

                if (isProcessedQuestion) {

                    //<editor-fold desc="PROCESSED QUESTION">
                    viewHolder.incomingQuestionProcessedQuestionTitleTextView.setVisibility(View.VISIBLE);

                    for (int i = 0; i < viewHolder.answerRoots.length; i++) {
//                        if(viewHolder.answerRoots[i].getBackground() != null)
//                            viewHolder.answerRoots[i].getBackground().mutate().setAlpha(90);
//                        int backgroundColorResourceId = context.getResources().getIdentifier("shape_bg_rectangle_bordered", "drawable", context.getPackageName());
//                        viewHolder.answerRoots[i].setBackgroundResource(backgroundColorResourceId);
                        viewHolder.answerRoots[i].setOnClickListener(null);
                    }

                    String userAnswerId = "";

                    if (playerAnswers.containsKey(questionId)) {
                        userAnswerId = playerAnswers.get(questionId).getAnswerId();

//                        for (int i = 0; i < answers.length; i++) {
//                            if (answers[i].getId().equals(userAnswerId)) {
//                                final int answerNumber = i + 1;
//                                final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
////                                if(viewHolder.answerRoots[i].getBackground() != null)
////                                    viewHolder.answerRoots[i].getBackground().mutate().setAlpha(255);
//                                viewHolder.answerRoots[i].setBackgroundResource(backgroundColorResourceId);
//                                break;
//                            }
//
//                        }

                    }

                    /********************new processed question layout: answers  as bars*********************/
                    viewHolder.incomingProcessedQuestionAnswersBarsRoot.removeAllViews();

                    for (Answer answer : answers) {
                        ViewGroup layout = (ViewGroup) LayoutInflater.from(context).inflate(R.layout.processed_answer_item, viewHolder.incomingProcessedQuestionAnswersBarsRoot, false);
                        LinearLayout answerTextRoot = (LinearLayout) layout.getChildAt(2);
                        LinearLayout progressBarRoot = (LinearLayout) layout.getChildAt(1);
                        LinearLayout percentValueRoot = (LinearLayout) layout.getChildAt(0);

                        TextView answerTextTextView = (TextView) answerTextRoot.getChildAt(0);
                        ProgressBar percentAnsweredDisplayBar = (ProgressBar) progressBarRoot.getChildAt(0);
                        TextView answerPercentAnsweredTextView = (TextView) percentValueRoot.getChildAt(0);

//                        int answerNumber = getAnswerNumber(viewHolder.question, answer.getId());

                        //int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber+1 + "backgroundColor", "color", context.getPackageName());
                        int backgroundColorResourceId = context.getResources().getIdentifier("shape_bg_rectangle_answer", "drawable", context.getPackageName());

                        answerTextRoot.setBackgroundResource(backgroundColorResourceId);
                        progressBarRoot.setBackgroundResource(backgroundColorResourceId);
                        percentValueRoot.setBackgroundResource(backgroundColorResourceId);

                        answerTextTextView.setText(answer.getAnswerText());
                        final String percentUserAnsweredDisplay = Integer.toString((int) answer.getPercentUsersAnswered()) + "%";
                        answerPercentAnsweredTextView.setText(percentUserAnsweredDisplay);
                        percentAnsweredDisplayBar.setMax(100);
                        int percentUserAnswered = (int) answer.getPercentUsersAnswered();
                        percentAnsweredDisplayBar.setProgress(percentUserAnswered);

                        Drawable drawable;

                        if (answer.getId().equals(userAnswerId)) {
                            drawable = ContextCompat.getDrawable(context, R.drawable.shape_bg_rectangle_processed_answer_selected);
                            answerTextTextView.setTextColor(ContextCompat.getColor(context, R.color.answerTextColor));
                            answerPercentAnsweredTextView.setTextColor(ContextCompat.getColor(context, R.color.answerTextColor));
                        } else {
                            drawable = ContextCompat.getDrawable(context, R.drawable.shape_bg_rectangle_processed_answer);
                        }

                        percentAnsweredDisplayBar.setProgressDrawable(drawable);

                        if (answerTextRoot.getBackground() != null)
                            answerTextRoot.getBackground().mutate().setAlpha(20);
                        if (progressBarRoot.getBackground() != null)
                            progressBarRoot.getBackground().mutate().setAlpha(20);
                        if (percentValueRoot.getBackground() != null)
                            percentValueRoot.getBackground().mutate().setAlpha(20);

                        if (answer.getId().equals(userAnswerId)) {
                            if (answerTextRoot.getBackground() != null)
                                answerTextRoot.getBackground().mutate().setAlpha(255);
                            if (progressBarRoot.getBackground() != null)
                                progressBarRoot.getBackground().mutate().setAlpha(255);
                            if (percentValueRoot.getBackground() != null)
                                percentValueRoot.getBackground().mutate().setAlpha(255);
                        }


                        viewHolder.incomingProcessedQuestionAnswersBarsRoot.addView(layout);
                    }

                    viewHolder.incomingProcessedQuestionAnswersBarsRoot.setVisibility(View.VISIBLE);

                    /*--------------------*/


                    if (isTimerRequired) {


                        viewHolder.timeToAnswer = viewHolder.chatMessage.getTimeLeftToAnswer();
                        int progressBarMaxValue = viewHolder.timeToAnswer;
                        viewHolder.incomingTimeToAnswerProgressBar.setMax(progressBarMaxValue);

                        //timer of current question
                        if (viewHolder.timeToAnswer > 0) {
                            if (viewHolder.countDownTimer != null) {
                                //Log.i("offside", "CANCELLING!!! timerId: " + String.valueOf(viewHolder.countDownTimer.hashCode()));
                                viewHolder.countDownTimer.cancel();
                                viewHolder.countDownTimer = null;
                            }

                            viewHolder.countDownTimer = new CountDownTimer(viewHolder.timeToAnswer, 100) {
                                @Override
                                public void onTick(long millisUntilFinished) {
                                    String questionId = viewHolder.question.getId();
                                    if (playerAnswers.containsKey(questionId) && !playerAnswers.get(questionId).getQuestionIsActive()) {
                                        this.onFinish();
                                    }
                                    viewHolder.chatMessage.setTimeLeftToAnswer((int) millisUntilFinished);
                                    //Log.i("offside", "timerId: " + String.valueOf(this.hashCode()) + " question text: " + viewHolder.question.getQuestionText() + " - secondsLeft: " + Math.round((int) millisUntilFinished / 1000.0f));
                                    viewHolder.incomingTimeToAnswerProgressBar.setProgress(Math.round((float) millisUntilFinished));

                                    String formattedTimerDisplay = formatTimerDisplay(millisUntilFinished);

                                    viewHolder.incomingTimeToAnswerTextDisplayTextView.setText(formattedTimerDisplay);
//
                                }

                                @Override
                                public void onFinish() {
                                    viewHolder.chatMessage.setTimeLeftToAnswer(0);
                                    viewHolder.incomingTimeToAnswerProgressBar.setProgress(0);
                                    viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
                                    viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView.setVisibility(View.VISIBLE);


                                }
                            }.start();
                        }

                        viewHolder.incomingTimeToAnswerRoot.setVisibility(View.VISIBLE);

                    }


                    //</editor-fold>
                }

                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingQuestionRoot.setVisibility(View.VISIBLE);

                //hiding timer
                if (isDebate && isProcessedQuestion) {
                    viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
                    viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView.setVisibility(View.GONE);

                }


                //</editor-fold>
            }

            if (isClosedQuestion) {

                //<editor-fold desc="CLOSED QUESTION">
                Uri botProfilePictureUri = Uri.parse(viewHolder.chatMessage.getImageUrl());
                Uri playerProfileImageUri = Uri.parse(OffsideApplication.getPlayerAssets().getImageUrl());

                ImageHelper.loadImage(context,viewHolder.incomingClosedQuestionBotImageView,botProfilePictureUri,true);
                ImageHelper.loadImage(context,viewHolder.incomingClosedQuestionPlayerProfilePictureImageView,playerProfileImageUri,true);



                Answer correctAnswer = null;
                for (Answer answer : viewHolder.question.getAnswers()) {
                    if (answer.isCorrect())
                        correctAnswer = answer;
                }
                if (correctAnswer == null)
                    return;

                AnswerIdentifier userAnswerIdentifier = playerAnswers.containsKey(viewHolder.question.getId()) ? playerAnswers.get(viewHolder.question.getId()) : null;
                if (userAnswerIdentifier == null)
                    return;

                userAnswerIdentifier.setQuestionIsActive(false);

                boolean isSkipped = userAnswerIdentifier.isSkipped();
                boolean isUserAnswerCorrect;

                int userBetSize = userAnswerIdentifier.getBetSize();
                int userReturnValue = (int) (correctAnswer.getPointsMultiplier() * userBetSize);

                if (isSkipped) {
                    viewHolder.incomingCorrectWrongTitleTextView.setText(context.getString(R.string.lbl_seems_like_you_skipped_this_question));
                    viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.GONE);
                    //viewHolder.incomingClosedQuestionRoot.setBackgroundResource(R.drawable.shape_bg_incoming_bubble_wrong);
                    viewHolder.incomingFeedbackPlayerTextView.setText(context.getString(R.string.lbl_wrong_answer_encourage_feedback));
                    viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.VISIBLE);
                    viewHolder.incomingCorrectAnswerReturnTextView.setText(context.getString(R.string.lbl_you_didnt_earn_points));
                    viewHolder.incomingPlayerAnswerRoot.setVisibility(View.GONE);

                } else {
                    isUserAnswerCorrect = correctAnswer.getId().equals(userAnswerIdentifier.getAnswerId());
                    viewHolder.incomingCorrectWrongTitleTextView.setText(isUserAnswerCorrect ? context.getString(R.string.lbl_correct_answer_feedback) : context.getString(R.string.lbl_wrong_answer_feedback));
                    viewHolder.incomingCorrectAnswerReturnTextView.setText(isUserAnswerCorrect ? context.getString(R.string.lbl_you_earned) + " " + userReturnValue + " " + context.getString(R.string.lbl_points) : context.getString(R.string.lbl_you_didnt_earn_points));

                    if (isUserAnswerCorrect) {
                        viewHolder.incomingCorrectWrongTitleTextView.setTextColor(ContextCompat.getColor(context,R.color.correctAnswerColor));
                        viewHolder.incomingCorrectAnswerTextView.setBackgroundResource(R.drawable.shape_bg_rectangle_answer_correct);
                        viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.GONE);

                    } else {


                        viewHolder.incomingCorrectWrongTitleTextView.setTextColor(ContextCompat.getColor(context,R.color.wrongAnswerColor));
                        viewHolder.incomingCorrectAnswerTextView.setBackgroundResource(R.drawable.shape_bg_rectangle_answer_correct);
                        viewHolder.incomingPlayerAnswerTextView.setBackgroundResource(R.drawable.shape_bg_rectangle_answer_wrong);
                        viewHolder.incomingPlayerAnswerTextView.setText(getAnswerText(viewHolder.question, userAnswerIdentifier.getAnswerId()));
                        //viewHolder.incomingFeedbackPlayerTextView.setText(context.getString(R.string.lbl_wrong_answer_encourage_feedback));
                        viewHolder.incomingPlayerAnswerRoot.setVisibility(View.VISIBLE);
                        //viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.VISIBLE);
                    }
                }

                viewHolder.incomingClosedQuestionTextView.setText(viewHolder.question.getQuestionText());
                viewHolder.incomingCorrectAnswerTextView.setText(correctAnswer.getAnswerText());

                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingClosedQuestionRoot.setVisibility(View.VISIBLE);

                return;

                //</editor-fold>
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void setAnswersVisibility(ViewHolder viewHolder, Answer[] answers, int playerMinBetSize, boolean isAskedQuestion, boolean isProcessedQuestion) {

        for (int i = 0; i < answers.length; i++) {
            final String answerText = answers[i].getAnswerText();
            final int initialReturnValue = (int) answers[i].getPointsMultiplier() * playerMinBetSize;

            viewHolder.answerTextViews[i].setText(answerText);
            if (isAskedQuestion) {
                viewHolder.answerReturnTextViews[i].setText(String.valueOf(initialReturnValue));
                viewHolder.answerReturnTextViews[i].setVisibility(View.VISIBLE);


            } else if (isProcessedQuestion) {
                viewHolder.answerReturnTextViews[i].setVisibility(View.GONE);
            }

            viewHolder.answerRoots[i].setVisibility(View.VISIBLE);
        }
    }

    private void generateSocialFeedChatMessage(ViewHolder viewHolder) {

        try {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

            //visibility reset
            resetWidgetsVisibility(viewHolder);

            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
            viewHolder.incomingUserSentTextView.setText(viewHolder.chatMessage.getSentByUserName());

            String messageText = viewHolder.chatMessage.getMessageText();
            viewHolder.incomingSocialFeedTextMessageTextView.setText(messageText);

            Bitmap bitmapImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.app_logo_10);
            shareOnFacebook(viewHolder.facebookShareButton, bitmapImage, messageText);

            //background set
//            if (viewHolder.isMessageFromBot)
//                viewHolder.incomingMessagesRoot.setBackgroundResource(R.drawable.shape_bg_incoming_bubble_from_bot);
//            else
//                viewHolder.incomingMessagesRoot.setBackgroundResource(R.drawable.shape_bg_incoming_bubble);


            //visibility set
            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingSocialFeedRoot.setVisibility(View.VISIBLE);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    public void shareOnFacebook(ShareButton facebookShareButton, Bitmap bitmapImage, String quote) {

        StringBuilder sb = new StringBuilder();

        sb.append(quote);
        sb.append("\r\n");
        sb.append("\r\n");
        sb.append("think you can bit me? come join!!!");
        ShareLinkContent content = new ShareLinkContent.Builder()
                .setContentUrl(Uri.parse(OffsideApplication.getAppLogoPictureUrl()))
                .setShareHashtag(new ShareHashtag.Builder()
                        .setHashtag("#Sidekick#soccer#livegame")
                        .build())
                //.setQuote(quote+"\r\n"+ "think you can bit me? come join!!!" )
                .setQuote(sb.toString())
                .build();


//        SharePhoto photo = new SharePhoto.Builder()
//                .setBitmap(bitmapImage)
//                .build();
//
//        SharePhotoContent content = new SharePhotoContent.Builder()
//                .addPhoto(photo)
//                .build();

        //ShareDialog.show(thisActivity, content);
        facebookShareButton.setShareContent(content);

    }

    private void postAnswer(Question question, Answer answer, View view, ViewHolder viewHolder) {

        try {
            removeClickListenerFromAnswers(viewHolder);
            final ViewHolder myViewHolder = viewHolder;
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;

            //final boolean isRandomlySelected = view == null;
            final boolean isSkipped = answer == null;
            final boolean isDebate = question.isDebate();

            final String gameId = question.getGameId();
            final String questionId = question.getId();
            final String answerId = isSkipped ? null : answer.getId();
            //final int returnValue = isSkipped ? 0 : (int) answer.getScore();

            final int betSize = (isSkipped || isDebate) ? 0 : answer.getSelectedBetSize();

            viewHolder.incomingProcessingQuestionTextView.setText(question.getQuestionText());


            if (isSkipped) {
                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_seems_like_you_skipped_this_question);
                //viewHolder.incomingSelectedAnswerTextView.setVisibility(View.GONE);

            }
//            else {
//                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);
//                viewHolder.incomingSelectedAnswerTextView.setText(answer.getAnswerText());
//                int answerNumber = getAnswerNumber(question, answer.getId());
//                //int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
//                int backgroundColorResourceId = context.getResources().getIdentifier("shape_bg_rectangle_answer_selected", "color", context.getPackageName());
//                viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);
//                viewHolder.incomingSelectedAnswerTextView.setVisibility(View.VISIBLE);
//
//            }

//            viewHolder.incomingSelectedAnswerReturnTextView.setText(Integer.toString(returnValue));

            playerAnswers.put(questionId, new AnswerIdentifier(answerId, isSkipped, betSize, true));
            if (view != null) //null when question was skipped
                //view.animate().rotationX(360.0f).setDuration(200).start();
                YoYo.with(Techniques.FlipInY).playOn(view);


            setStyleForSelectedAnswer(viewHolder, answerId);

            int delay = isSkipped ? 0 : 500;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    //myViewHolder.incomingQuestionRoot.setVisibility(View.GONE);
                    //myViewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);
                    myViewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
                    myViewHolder.incomingBetPanelRoot.setVisibility(View.GONE);


                    EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, isSkipped, betSize));
                }
            }, delay);


//            final boolean isRandomlySelected = view == null;
//
//            if (isRandomlySelected)
//                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
//            else
//                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);
//
//            viewHolder.incomingProcessingQuestionTextView.setText(question.getQuestionText());
//            viewHolder.incomingSelectedAnswerTextView.setText(answer.getAnswerText());
//            int answerNumber = getAnswerNumber(question, answer.getId());
//            int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
//            viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);

//            playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomlySelected, betSize, true));
//            if (view != null) //null when random answer was selected
//                view.animate().rotationX(360.0f).setDuration(200).start();
//
//            int delay = isRandomlySelected ? 0 : 500;
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    myViewHolder.incomingQuestionRoot.setVisibility(View.GONE);
//                    myViewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);
//                    EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, isRandomlySelected, betSize));
//                }
//            }, delay);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void setStyleForSelectedAnswer(ViewHolder viewHolder, String answerId) {

        for (int i = 0; i < viewHolder.answerRoots.length; i++) {
            viewHolder.answerRoots[i].setBackgroundResource(R.drawable.shape_bg_rectangle_answer_unselected);
            viewHolder.answerTextViews[i].setTextColor(ContextCompat.getColor(context, R.color.answerTextUnSelectedColor));
            viewHolder.answerReturnTextViews[i].setTextColor(ContextCompat.getColor(context, R.color.answerTextUnSelectedColor));
        }

        int answerNumber = getAnswerNumber(viewHolder.question, answerId);
        viewHolder.answerRoots[answerNumber].setBackgroundResource(R.drawable.shape_bg_rectangle_answer_selected);
        viewHolder.answerTextViews[answerNumber].setTextColor(ContextCompat.getColor(context, R.color.answerTextSelectedColor));
        viewHolder.answerReturnTextViews[answerNumber].setTextColor(ContextCompat.getColor(context, R.color.answerTextSelectedColor));


    }

    private int getAnswerNumber(Question question, String answerId) {

        try {
            int answerNumber = 0;
            Answer[] answers = question.getAnswers();
            for (int i = 0; i < answers.length; i++) {
                Answer answer = answers[i];
                if (answer.getId().equals(answerId)) {
                    answerNumber = i;
                    break;
                }

            }
            return answerNumber;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return 0;

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
    }

    private void resetWidgetsVisibility(ViewHolder viewHolder) {

        try {
            //reset all to gone
            viewHolder.incomingMessagesRoot.setVisibility(View.GONE);
            viewHolder.outgoingMessagesRoot.setVisibility(View.GONE);
            viewHolder.incomingTextMessageTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingQuestionStatRoot.setVisibility(View.GONE);
            viewHolder.incomingProcessingQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingProcessingQuestionPossibleReturnValueMessageRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingClosedQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
            viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
            viewHolder.incomingQuestionProcessedQuestionTitleTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setVisibility(View.GONE);
            viewHolder.incomingGetCoinsMessageRoot.setVisibility(View.GONE);
            viewHolder.incomingGetCoinsPlayerOptionsRoot.setVisibility(View.GONE);
            viewHolder.incomingGetCoinsLoadingRoot.setVisibility(View.GONE);
            viewHolder.incomingMissedQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingPlayerAnswerRoot.setVisibility(View.GONE);
            viewHolder.incomingWinnerMessageRoot.setVisibility(View.GONE);
            viewHolder.incomingSelectedAnswerTextView.setVisibility(View.GONE);
            viewHolder.incomingAnswersRoot.setVisibility(View.GONE);
            viewHolder.incomingProcessedQuestionAnswersBarsRoot.setVisibility(View.GONE);
            viewHolder.incomingSocialFeedRoot.setVisibility(View.GONE);

            for (int i = 0; i < viewHolder.answerRoots.length; i++) {
                viewHolder.answerRoots[i].setBackgroundResource(R.drawable.shape_bg_rectangle_answer);
                viewHolder.answerTextViews[i].setTextColor(Color.WHITE);
                viewHolder.answerReturnTextViews[i].setTextColor(Color.WHITE);
                viewHolder.answerRoots[i].setVisibility(View.INVISIBLE);
            }

            viewHolder.incomingMessagesRoot.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void removeClickListenerFromAnswers(ViewHolder viewHolder) {
        for (int i = 0; i < viewHolder.answerRoots.length; i++) {
            viewHolder.answerRoots[i].setOnClickListener(null);
        }
    }


}


