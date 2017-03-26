package com.offsidegame.offside.adapters;

import android.content.Context;
import android.graphics.Bitmap;

import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.v4.content.ContextCompat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.AnswerIdentifier;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.Question;
import com.squareup.picasso.Picasso;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by KFIR on 11/21/2016.
 */

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {


    private Context context;

    private Map<String, AnswerIdentifier> playerAnswers;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages, Map<String, AnswerIdentifier> playerAnswers) {
        super(context, 0, chatMessages);
        this.context = context;
        this.playerAnswers = playerAnswers;
    }

    private class ViewHolder {

        //<editor-fold desc="VIEWHOLDER PROPERTIES">

        public LinearLayout incomingMessagesRoot;
        public ImageView incomingProfilePictureImageView;
        public TextView incomingTextMessageTextView;

        public LinearLayout incomingQuestionRoot;
        public TextView incomingQuestionTextView;
        public TextView incomingQuestionProcessedQuestionTitleTextView;
        public TextView incomingQuestionAskedNotEnoughCoinsTextView;
        public LinearLayout incomingAnswersRoot;

        public LinearLayout IncomingAnswers12Root;

        public TextView[] answerReturnTextViews = new TextView[4];
        public TextView[] answerPercentTextViews = new TextView[4];
        public TextView[] answerTextViews = new TextView[4];
        public LinearLayout[] answerRoots = new LinearLayout[4];

        public LinearLayout incomingBetPanelRoot;

        public TextView[] betSizeOptionsTextViews = new TextView[3];
        public TextView incomingBalanceTextView;

        public LinearLayout incomingTimeToAnswerRoot;
        public TextView incomingTimeToAnswerTextDisplayTextView;
        public ProgressBar incomingTimeToAnswerProgressBar;
        public TextView incomingQuestionProcessedQuestionTimeExpiredMessageTextView;

        public LinearLayout incomingProcessingQuestionRoot;
        public TextView incomingProcessingQuestionTextView;
        public TextView incomingSelectedAnswerTitleTextView;
        public TextView incomingSelectedAnswerTextView;
        public TextView incomingSelectedAnswerReturnTextView;


        public LinearLayout incomingClosedQuestionRoot;
        public TextView incomingClosedQuestionTextView;
        public TextView incomingCorrectWrongTitleTextView;
        public TextView incomingCorrectAnswerTextView;
        public TextView incomingCorrectAnswerReturnTextView;
        public TextView incomingFeedbackPlayerTextView;
        public TextView incomingTimeSentTextView;

        public LinearLayout outgoingMessagesRoot;
        public ImageView outgoingProfilePictureImageView;
        public TextView outgoingTextMessageTextView;
        public TextView outgoingTimeSentTextView;

        public Question question;
        public int timeToAnswer;
        public ChatMessage chatMessage;
        public CountDownTimer countDownTimer;
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

                //text message
                viewHolder.incomingMessagesRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_messages_root);
                viewHolder.incomingProfilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_incoming_profile_picture_image_view);
                viewHolder.incomingTextMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_text_message_text_view);

                viewHolder.incomingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_question_root);
                viewHolder.incomingQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_text_view);

                viewHolder.incomingAnswersRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_answers_root);

                viewHolder.IncomingAnswers12Root = (LinearLayout) convertView.findViewById(R.id.cm_incoming_answers_1_2_root);

                for (int i = 0; i < 4; i++) {
                    final int answerNumber = i + 1;
                    final int returnTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_return_text_view", "id", context.getPackageName());
                    final int percentTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_percent_text_view", "id", context.getPackageName());
                    final int answerTextViewResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_text_view", "id", context.getPackageName());
                    final int answerRootResourceId = context.getResources().getIdentifier("cm_incoming_answer_" + answerNumber + "_root", "id", context.getPackageName());
                    viewHolder.answerReturnTextViews[i] = (TextView) convertView.findViewById(returnTextViewResourceId);
                    viewHolder.answerPercentTextViews[i] = (TextView) convertView.findViewById(percentTextViewResourceId);
                    viewHolder.answerTextViews[i] = (TextView) convertView.findViewById(answerTextViewResourceId);
                    viewHolder.answerRoots[i] = (LinearLayout) convertView.findViewById(answerRootResourceId);

                }


                viewHolder.incomingBetPanelRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_bet_panel_root);

                for (int i = 0; i < 3; i++) {
                    final int optionNumber = i + 1;
                    final int incomingBetOptionTextViewId = context.getResources().getIdentifier("cm_incoming_bet_option" + optionNumber + "_text_view", "id", context.getPackageName());
                    viewHolder.betSizeOptionsTextViews[i] = (TextView) convertView.findViewById(incomingBetOptionTextViewId);

                }

                viewHolder.incomingBalanceTextView = (TextView) convertView.findViewById(R.id.cm_incoming_balance_text_view);

                viewHolder.incomingTimeToAnswerRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_time_to_answer_root);

                viewHolder.incomingTimeToAnswerTextDisplayTextView = (TextView) convertView.findViewById(R.id.cm_incoming_time_to_answer_text_display_text_view);
                viewHolder.incomingTimeToAnswerProgressBar = (ProgressBar) convertView.findViewById(R.id.cm_incoming_time_to_answer_progress_bar);
                viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView = (TextView) convertView.findViewById(R.id.cm_incoming_question_processed_question_time_expired_message_text_view);

                viewHolder.incomingProcessingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_processing_question_root);
                viewHolder.incomingSelectedAnswerTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_title_text_view);
                viewHolder.incomingSelectedAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_text_view);
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


                viewHolder.outgoingMessagesRoot = (LinearLayout) convertView.findViewById(R.id.cm_outgoing_messages_root);
                viewHolder.outgoingProfilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_outgoing_profile_picture_image_view);
                viewHolder.outgoingTextMessageTextView = (TextView) convertView.findViewById(R.id.cm_outgoing_text_message_text_view);
                viewHolder.outgoingTimeSentTextView = (TextView) convertView.findViewById(R.id.cm_outgoing_time_sent_text_view);

                //</editor-fold>

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.chatMessage = getItem(position);
            if (viewHolder.chatMessage == null)
                return convertView;

            String chatMessageType = viewHolder.chatMessage.getMessageType();

            if (chatMessageType == null)
                return convertView;


            if (chatMessageType.equals(OffsideApplication.getMessageTypeText()))  //"TEXT"
            {
                generateTextChatMessage(viewHolder);

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion()) || //"ASKED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion()) ||  //"PROCESSED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion())) //"CLOSED_QUESTION"
            {
                generateQuestionChatMessage(viewHolder);
            }

            return convertView;
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


        return null;
    }

    private void generateTextChatMessage(ViewHolder viewHolder) {

        try {

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            Uri profilePictureUri = Uri.parse(viewHolder.chatMessage.getImageUrl());

            //visibility reset
            resetWidgetsVisibility(viewHolder);

            if (viewHolder.chatMessage.isIncoming()) {
                loadFbImage(viewHolder.incomingProfilePictureImageView, profilePictureUri);
                viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
                viewHolder.incomingTextMessageTextView.setText(viewHolder.chatMessage.getMessageText());

                //visibility set
                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingTextMessageTextView.setVisibility(View.VISIBLE);

            } else {
                loadFbImage(viewHolder.outgoingProfilePictureImageView, profilePictureUri);
                viewHolder.outgoingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
                viewHolder.outgoingTextMessageTextView.setText(viewHolder.chatMessage.getMessageText());

                //visibility set
                viewHolder.outgoingMessagesRoot.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    private void generateQuestionChatMessage(final ViewHolder viewHolder) {

        try {

            //chat message properties
            final Uri profilePictureUri = Uri.parse(viewHolder.chatMessage.getImageUrl());
            loadFbImage(viewHolder.incomingProfilePictureImageView, profilePictureUri);
            final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(viewHolder.chatMessage.getSentTime()));
            String chatMessageType = viewHolder.chatMessage.getMessageType();

            //bind Question object to the ui elements
            final Gson gson = new GsonBuilder().create();
            viewHolder.question = gson.fromJson(viewHolder.chatMessage.getMessageText(), Question.class);
            final String questionId = viewHolder.question.getId();

            final boolean isAskedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion());
            final boolean isProcessedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion());
            final boolean isClosedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion());
            final boolean isPlayerAnsweredQuestion = playerAnswers.containsKey(questionId);

            resetWidgetsVisibility(viewHolder);

            //open question but user already answered it
            if (isAskedQuestion && isPlayerAnsweredQuestion) {
                final String userAnswerId = playerAnswers.get(questionId).getAnswerId();
                int betSize = playerAnswers.get(questionId).getBetSize();
                int answerNumber = getAnswerNumber(viewHolder.question, userAnswerId);
                if (answerNumber == 0)
                    return;

                final Answer answerOfTheUser = viewHolder.question.getAnswers()[answerNumber - 1];

                //set values to widgets
                viewHolder.incomingProcessingQuestionTextView.setText(viewHolder.question.getQuestionText());
                viewHolder.incomingSelectedAnswerTextView.setText(answerOfTheUser.getAnswerText());

                int returnValue = (int) (betSize * answerOfTheUser.getPointsMultiplier());
                viewHolder.incomingSelectedAnswerReturnTextView.setText(String.valueOf(returnValue));

                final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
                viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);

                if (playerAnswers.get(questionId).isRandomlySelected())
                    viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
                else
                    viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);

                //visibility set

                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);


                return;
            }

            final int minBetSize = OffsideApplication.getOffsideCoins() < OffsideApplication.getGameInfo().getMinBetSize() ? 0 : OffsideApplication.getGameInfo().getMinBetSize();

            final Answer[] answers = viewHolder.question.getAnswers();

            if (isAskedQuestion || isProcessedQuestion) {

                //<editor-fold desc="ASKED OR PROCESSED QUESTION">

                viewHolder.incomingQuestionTextView.setText(viewHolder.question.getQuestionText());

                for (int i = 0; i < answers.length; i++) {
                    final String answerText = answers[i].getAnswerText();
                    final String percentUserAnswered = String.valueOf((int) answers[i].getPercentUsersAnswered()) + "%";
                    final int initialReturnValue = (int) answers[i].getPointsMultiplier() * minBetSize;

                    viewHolder.answerTextViews[i].setText(answerText);
                    if (isAskedQuestion) {
                        viewHolder.answerReturnTextViews[i].setText(String.valueOf(initialReturnValue));
                        viewHolder.answerReturnTextViews[i].setVisibility(View.VISIBLE);
                        viewHolder.answerPercentTextViews[i].setVisibility(View.GONE);

                    } else if (isProcessedQuestion) {
                        viewHolder.answerPercentTextViews[i].setText(percentUserAnswered);
                        viewHolder.answerReturnTextViews[i].setVisibility(View.GONE);
                        viewHolder.answerPercentTextViews[i].setVisibility(View.VISIBLE);
                    }

                    viewHolder.answerRoots[i].setVisibility(View.VISIBLE);
                }


                if (isAskedQuestion) {

                    //<editor-fold desc="ASKED QUESTION">

                    //set on click event to bet options
                    for (int i = 0; i < viewHolder.betSizeOptionsTextViews.length; i++) {
                        viewHolder.betSizeOptionsTextViews[i].setTag(i);
                        viewHolder.betSizeOptionsTextViews[i].setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                int index = (int) view.getTag();
                                //update answers return value based on betSize
                                int betSize = minBetSize * (index + 1);
                                for (int i = 0; i < answers.length; i++) {
                                    final int returnValue = (int) Math.round(answers[i].getPointsMultiplier() * betSize);
                                    viewHolder.answerReturnTextViews[i].setText(String.valueOf(returnValue));
                                    answers[i].setScore(returnValue);
                                    answers[i].setSelectedBetSize(betSize);
                                }

                                //update betSize button background
                                for (int j = 0; j < viewHolder.betSizeOptionsTextViews.length; j++) {
                                    if (j == index) {
                                        viewHolder.betSizeOptionsTextViews[j].setBackgroundResource(R.drawable.shape_bg_circle_selected);
                                        viewHolder.betSizeOptionsTextViews[j].setTextColor(ContextCompat.getColor(context, R.color.privateGameTitle));
                                    } else {
                                        viewHolder.betSizeOptionsTextViews[j].setBackgroundResource(R.drawable.shape_bg_circle);
                                        viewHolder.betSizeOptionsTextViews[j].setTextColor(ContextCompat.getColor(context, R.color.colorWhite));
                                    }

                                }

                                //update balance
                                int postBetOffsideCoins = OffsideApplication.getOffsideCoins() - betSize;
                                viewHolder.incomingBalanceTextView.setText(String.valueOf(postBetOffsideCoins));

                            }
                        });

                        viewHolder.betSizeOptionsTextViews[i].setVisibility(View.INVISIBLE);

                        if (OffsideApplication.getOffsideCoins() >= (i + 1) * OffsideApplication.getGameInfo().getMinBetSize())
                            viewHolder.betSizeOptionsTextViews[i].setVisibility(View.VISIBLE);
                        else {
                            if (OffsideApplication.getOffsideCoins() < OffsideApplication.getGameInfo().getMinBetSize()) {
                                for (Answer answer : viewHolder.question.getAnswers()) {
                                    answer.selectedBetSize = 0;

                                }
                            }


                        }

                    }

                    viewHolder.betSizeOptionsTextViews[0].performClick();


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
                            Log.i("offside", "CANCELLING!!! timerId: " + String.valueOf(viewHolder.countDownTimer.hashCode()));
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
                                    int answersCount = viewHolder.question.getAnswers().length;
                                    int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
                                    Answer randomAnswer = viewHolder.question.getAnswers()[selectedAnswerIndex];
                                    //final int returnValue = Integer.parseInt( viewHolder.answerReturnTextViews[selectedAnswerIndex].getText().toString());
                                    postAnswer(viewHolder.question, randomAnswer, null, viewHolder);
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
                    if (OffsideApplication.getOffsideCoins() < OffsideApplication.getGameInfo().getMinBetSize()) {
                        viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
                        viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setVisibility(View.VISIBLE);
                        for (int j = 0; j < 4; j++) {
                            viewHolder.answerRoots[j].getBackground().mutate().setAlpha(90);
                            viewHolder.answerRoots[j].setOnClickListener(null);
                        }


                    } else {
                        viewHolder.incomingBetPanelRoot.setVisibility(View.VISIBLE);

                    }
                    //</editor-fold>

                }

                if (isProcessedQuestion) {

                    //<editor-fold desc="PROCESSED QUESTION">
                    viewHolder.incomingQuestionProcessedQuestionTitleTextView.setVisibility(View.VISIBLE);

                    for (int i = 0; i < 4; i++) {
                        viewHolder.answerRoots[i].getBackground().mutate().setAlpha(90);
                        viewHolder.answerRoots[i].setOnClickListener(null);
                    }

                    if (playerAnswers.containsKey(questionId)) {
                        String userAnswerId = playerAnswers.get(questionId).getAnswerId();

                        for (int i = 0; i < answers.length; i++) {
                            if (answers[i].getId().equals(userAnswerId)) {
                                final int answerNumber = i + 1;
                                final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
                                viewHolder.answerRoots[i].getBackground().mutate().setAlpha(255);
                                viewHolder.answerRoots[i].setBackgroundResource(backgroundColorResourceId);
                                break;
                            }

                        }
                    }


                    viewHolder.timeToAnswer = viewHolder.chatMessage.getTimeLeftToAnswer();
                    int progressBarMaxValue = viewHolder.timeToAnswer;
                    viewHolder.incomingTimeToAnswerProgressBar.setMax(progressBarMaxValue);

                    //timer of current question
                    if (viewHolder.timeToAnswer > 0) {
                        if (viewHolder.countDownTimer != null) {
                            Log.i("offside", "CANCELLING!!! timerId: " + String.valueOf(viewHolder.countDownTimer.hashCode()));
                            viewHolder.countDownTimer.cancel();
                            viewHolder.countDownTimer = null;
                        }

                        viewHolder.countDownTimer = new CountDownTimer(viewHolder.timeToAnswer, 100) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                String questionId = viewHolder.question.getId();
                                if(playerAnswers.containsKey(questionId) && !playerAnswers.get(questionId).getQuestionIsActive()){
                                    this.onFinish();
                                }
                                viewHolder.chatMessage.setTimeLeftToAnswer((int) millisUntilFinished);
                                Log.i("offside","timerId: "+String.valueOf(this.hashCode())+" question text: "+ viewHolder.question.getQuestionText()+ " - secondsLeft: "+ Math.round((int)millisUntilFinished/1000.0f) );
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
                                viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
                                viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView.setVisibility(View.VISIBLE);

                                //cancel();


                            }
                        }.start();
                    }


                    //</editor-fold>

                }

                viewHolder.incomingTimeToAnswerRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingQuestionRoot.setVisibility(View.VISIBLE);
                //</editor-fold>
            }

            if (isClosedQuestion) {

                //<editor-fold desc="CLOSED QUESTION">


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

                boolean isUserAnswerCorrect = correctAnswer.getId().equals(userAnswerIdentifier.getAnswerId());
                int userBetSize = userAnswerIdentifier.getBetSize();
                int answerNumber = getAnswerNumber(viewHolder.question, correctAnswer.getId());
                int userReturnValue = (int) (correctAnswer.getPointsMultiplier() * userBetSize);

                final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());

                viewHolder.incomingClosedQuestionTextView.setText(viewHolder.question.getQuestionText());

                viewHolder.incomingCorrectWrongTitleTextView.setText(isUserAnswerCorrect ? context.getString(R.string.lbl_correct_answer_feedback) : context.getString(R.string.lbl_wrong_answer_feedback));
                viewHolder.incomingCorrectAnswerTextView.setText(correctAnswer.getAnswerText());
                viewHolder.incomingCorrectAnswerTextView.setBackgroundResource(backgroundColorResourceId);
                viewHolder.incomingCorrectAnswerReturnTextView.setText(isUserAnswerCorrect ? context.getString(R.string.lbl_you_earned) + " " + userReturnValue + " " + context.getString(R.string.lbl_points) : context.getString(R.string.lbl_you_didnt_earn_points));
                if (isUserAnswerCorrect) {
                    viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.GONE);
                } else {
                    viewHolder.incomingFeedbackPlayerTextView.setText(context.getString(R.string.lbl_wrong_answer_encourage_feedback));
                    viewHolder.incomingFeedbackPlayerTextView.setVisibility(View.VISIBLE);
                }

                viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingClosedQuestionRoot.setVisibility(View.VISIBLE);

                return;

                //</editor-fold>
            }


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void postAnswer(Question question, Answer answer, View view, ViewHolder viewHolder) {

        try {
            final ViewHolder myViewHolder = viewHolder;
            viewHolder.countDownTimer.cancel();
            viewHolder.countDownTimer = null;
            final boolean isRandomlySelected = view == null;
            if (isRandomlySelected)
                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
            else
                viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);

            viewHolder.incomingProcessingQuestionTextView.setText(question.getQuestionText());
            viewHolder.incomingSelectedAnswerTextView.setText(answer.getAnswerText());

            int answerNumber = getAnswerNumber(question, answer.getId());
            int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
            viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);

            final String gameId = question.getGameId();
            final String questionId = question.getId();
            final String answerId = answer.getId();
            final int returnValue = (int) answer.getScore();

            final int betSize = answer.getSelectedBetSize();

            viewHolder.incomingSelectedAnswerReturnTextView.setText(String.valueOf(returnValue));

            playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomlySelected, betSize, true));
            if (view != null) //null when random answer was selected
                view.animate().rotationX(360.0f).setDuration(200).start();

            int delay = isRandomlySelected ? 0 : 500;

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    myViewHolder.incomingQuestionRoot.setVisibility(View.GONE);
                    myViewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);
                    EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, isRandomlySelected, betSize));
                }
            }, delay);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }


    private void resetWidgetsVisibility(ViewHolder viewHolder) {

        try {
            //reset all to gone
            viewHolder.incomingMessagesRoot.setVisibility(View.GONE);
            viewHolder.outgoingMessagesRoot.setVisibility(View.GONE);
            viewHolder.incomingTextMessageTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingProcessingQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingClosedQuestionRoot.setVisibility(View.GONE);
            viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
            viewHolder.incomingTimeToAnswerRoot.setVisibility(View.GONE);
            viewHolder.incomingQuestionProcessedQuestionTitleTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionProcessedQuestionTimeExpiredMessageTextView.setVisibility(View.GONE);
            viewHolder.incomingQuestionAskedNotEnoughCoinsTextView.setVisibility(View.GONE);

            for (int i = 0; i < 4; i++) {
                viewHolder.answerRoots[i].setVisibility(View.INVISIBLE);
                viewHolder.answerRoots[i].getBackground().mutate().setAlpha(255);
            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private int getAnswerNumber(Question question, String answerId) {

        try {
            int answerNumber = 0;
            Answer[] answers = question.getAnswers();
            for (int i = 0; i < answers.length; i++) {
                Answer answer = answers[i];
                if (answer.getId().equals(answerId)) {
                    answerNumber = i + 1;
                    break;
                }

            }
            return answerNumber;

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

        return 0;

    }

    private void loadFbImage(final ImageView fbProfilePicture, Uri fbImageUri) {
        Picasso.with(context).load(fbImageUri).into(fbProfilePicture, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                Bitmap bm = ((BitmapDrawable) fbProfilePicture.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                fbProfilePicture.setImageDrawable(roundedImage);
                //fbProfilePicture.animate().alpha(1.1f).setDuration(200).start();
            }

            @Override
            public void onError() {

            }
        });
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


}


