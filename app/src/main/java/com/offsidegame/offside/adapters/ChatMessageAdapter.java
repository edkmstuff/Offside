package com.offsidegame.offside.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
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

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by KFIR on 11/21/2016.
 */

public class ChatMessageAdapter extends ArrayAdapter<ChatMessage> {


    private Context context;

    private CountDownTimer timeToAnswerTimer;
    private int timeToAnswer;
    private int secondsLeft = 0;
    private Map<String, AnswerIdentifier> playerAnswers;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages, Map<String, AnswerIdentifier> playerAnswers) {
        super(context, 0, chatMessages);
        this.context = context;
        this.playerAnswers = playerAnswers;
    }

    private class ViewHolder {

        public LinearLayout incomingMessagesRoot;
        public ImageView incomingProfilePictureImageView;
        public TextView incomingTextMessageTextView;

        public LinearLayout incomingQuestionRoot;
        public TextView incomingQuestionTextView;
        public LinearLayout incomingAnswersRoot;

        public LinearLayout IncomingAnswers12Root;

        public TextView[] answerReturnTextViews = new TextView[4];
        public TextView[] answerPercentTextViews = new TextView[4];
        public TextView[] answerTextViews = new TextView[4];
        public LinearLayout[] answerRoots = new LinearLayout[4];

        public LinearLayout incomingBetPanelRoot;
        public TextView incomingBetSizeTextView;
        public SeekBar incomingBetSizeSeekBar;
        public TextView incomingTimeToAnswerTextView;

        public LinearLayout incomingProcessingQuestionRoot;
        public TextView incomingProcessingQuestionTextView;
        public TextView incomingSelectedAnswerTitleTextView;
        public TextView incomingSelectedAnswerTextView;
        public TextView incomingSelectedAnswerReturnTextView;
        public TextView incomingProcessingQuestionTitleTextView;

        public LinearLayout incomingClosedQuestionRoot;
        public TextView incomingCorrectWrongTitleTextView;
        public TextView incomingCorrectAnswerTextView;
        public TextView incomingCorrectAnswerReturnTextView;
        public TextView incomingFeedbackPlayerTextView;
        public TextView incomingTimeSentTextView;

        public LinearLayout outgoingMessagesRoot;
        public ImageView outgoingProfilePictureImageView;
        public TextView outgoingTextMessageTextView;
        public TextView outgoingTimeSentTextView;

    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        try {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_item, parent, false);
                viewHolder = new ViewHolder();


                //<editor-fold desc="findViewById">

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
                viewHolder.incomingBetSizeTextView = (TextView) convertView.findViewById(R.id.cm_incoming_bet_size_text_view);
                viewHolder.incomingBetSizeSeekBar = (SeekBar) convertView.findViewById(R.id.cm_incoming_bet_size_seekBar);
                viewHolder.incomingTimeToAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_time_to_answer_text_view);

                viewHolder.incomingProcessingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_processing_question_root);
                viewHolder.incomingSelectedAnswerTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_title_text_view);
                viewHolder.incomingSelectedAnswerTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_text_view);
                viewHolder.incomingSelectedAnswerReturnTextView = (TextView) convertView.findViewById(R.id.cm_incoming_selected_answer_return_text_view);
                viewHolder.incomingProcessingQuestionTitleTextView = (TextView) convertView.findViewById(R.id.cm_incoming_processing_question_title_text_view);

                viewHolder.incomingProcessingQuestionTextView = (TextView) convertView.findViewById(R.id.cm_incoming_processing_question_text_view);

                viewHolder.incomingClosedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cm_incoming_closed_question_root);
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

            ChatMessage chatMessage = getItem(position);
            String chatMessageType = chatMessage.getMessageType();

            if (chatMessageType.equals(OffsideApplication.getMessageTypeText()))  //"TEXT"
            {
                generateTextChatMessage(viewHolder, chatMessage, chatMessage.isIncoming());

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion()) || //"ASKED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion()) ||  //"PROCESSED_QUESTION"
                    chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion())) //"CLOSED_QUESTION"
            {
                generateQuestionChatMessage(viewHolder, chatMessage);
            }

            return convertView;
        } catch (Exception ex) {
            Log.e("OFFSIDE", ex.getMessage());

        }


        return null;
    }

    private void generateTextChatMessage(ViewHolder viewHolder, ChatMessage chatMessage, boolean isIncoming) {

        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());

        //visibility reset
        resetWidgetsVisibility(viewHolder);

        if (isIncoming) {
            loadFbImage(viewHolder.incomingProfilePictureImageView, profilePictureUri);
            viewHolder.incomingTimeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));
            viewHolder.incomingTextMessageTextView.setText(chatMessage.getMessageText());

            //visibility set
            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingTextMessageTextView.setVisibility(View.VISIBLE);

        } else {
            loadFbImage(viewHolder.outgoingProfilePictureImageView, profilePictureUri);
            viewHolder.outgoingTimeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));
            viewHolder.outgoingTextMessageTextView.setText(chatMessage.getMessageText());

            //visibility set
            viewHolder.outgoingMessagesRoot.setVisibility(View.VISIBLE);
        }

    }

    private void generateQuestionChatMessage(final ViewHolder viewHolder, ChatMessage chatMessage) {

        //chat message properties
        final Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());
        loadFbImage(viewHolder.incomingProfilePictureImageView, profilePictureUri);
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        viewHolder.incomingTimeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));
        String chatMessageType = chatMessage.getMessageType();

        //bind Question object to the ui elements
        final Gson gson = new GsonBuilder().create();
        final Question question = gson.fromJson(chatMessage.getMessageText(), Question.class);
        final String questionId = question.getId();

        final boolean isAskedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion());
        final boolean isProcessedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion());
        final boolean isClosedQuestion = chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion());
        final boolean isPlayerAnsweredQuestion = playerAnswers.containsKey(questionId);

        resetWidgetsVisibility(viewHolder);

        //open question but user already answered it
        if (isAskedQuestion && isPlayerAnsweredQuestion) {
            final String userAnswerId = playerAnswers.get(questionId).getAnswerId();
            int betSize = playerAnswers.get(questionId).getBetSize();
            int answerNumber = getAnswerNumber(question, userAnswerId);
            if (answerNumber == 0)
                return;

            final Answer answerOfTheUser = question.getAnswers()[answerNumber - 1];

            //set values to widgets
            viewHolder.incomingProcessingQuestionTextView.setText(question.getQuestionText());
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

        //ASKED_QUESTION elements
        final int betSizeUnit = 100;
        final int seekBarMinValue = 1;
        int seekBarMaxValue = 4;
        final Answer[] answers = question.getAnswers();
        if (isAskedQuestion || isProcessedQuestion) {

            viewHolder.incomingQuestionTextView.setText(question.getQuestionText());

            for (int i = 0; i < answers.length; i++) {
                final String answerText = answers[i].getAnswerText();
                final String percentUserAnswered = String.valueOf((int) answers[i].getPercentUsersAnswered()) + "%";
                final double initialReturnValue = answers[i].getPointsMultiplier() * seekBarMinValue * betSizeUnit;

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


            //ASKED_QUESTION SECTION
            if (isAskedQuestion) {
                SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
                int balance = settings.getInt(context.getString(R.string.balance_key), 0);

                int maxSeekBarValue = (int) (Math.floor(balance / betSizeUnit) - 1);
                seekBarMaxValue = maxSeekBarValue > 4 ? 4 : maxSeekBarValue < 0 ? 0 : maxSeekBarValue; //limit range to 0-4

                viewHolder.incomingBetSizeTextView.setText(String.valueOf(seekBarMinValue*betSizeUnit));

                viewHolder.incomingBetSizeSeekBar.setProgress(0);
                viewHolder.incomingBetSizeSeekBar.setMax(seekBarMaxValue);
                viewHolder.incomingBetSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        int adjustedProgress = betSizeUnit + progress * betSizeUnit;
                        viewHolder.incomingBetSizeTextView.setText(String.valueOf(adjustedProgress));
                        for (int i = 0; i < answers.length; i++) {
                            final double defaultReturnValue = answers[i].getPointsMultiplier() * adjustedProgress;
                            viewHolder.answerReturnTextViews[i].setText(String.valueOf(defaultReturnValue));
                        }
                    }

                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {

                    }

                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {

                    }
                });

                //set the timeToAskQuestion timer
                timeToAnswer = question.getTimeToAnswerQuestion();
                timeToAnswerTimer = new CountDownTimer(timeToAnswer, 100) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        if (Math.round((float) millisUntilFinished / 1000.0f) != secondsLeft) {
                            secondsLeft = Math.round((float) millisUntilFinished / 1000.0f);
                            viewHolder.incomingTimeToAnswerTextView.setText(Integer.toString(secondsLeft));
                            if (secondsLeft < 7 && secondsLeft > 3)
                                viewHolder.incomingTimeToAnswerTextView.setBackgroundColor(Color.parseColor("#FFAB00"));
                            if (secondsLeft < 4)
                                viewHolder.incomingTimeToAnswerTextView.setBackgroundColor(Color.RED);
                        }
                    }

                    @Override
                    public void onFinish() {
                        //user did not answer this question, we select random answer
                        boolean isAnswered = playerAnswers.containsKey(question.getId());
                        if (!isAnswered) {
                            int answersCount = question.getAnswers().length;
                            int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
                            Answer randomAnswer = question.getAnswers()[selectedAnswerIndex];
                            postAnswer(question, randomAnswer, null, viewHolder);
                        }
                    }
                }.start();

                //set on click event to answers
                for (int i = 0; i < answers.length; i++) {
                    final Answer clickedAnswer = answers[i];
                    viewHolder.answerRoots[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            postAnswer(question, clickedAnswer, view, viewHolder);
                        }
                    });
                }

                viewHolder.incomingBetPanelRoot.setVisibility(View.VISIBLE);
                viewHolder.incomingTimeToAnswerTextView.setVisibility(View.VISIBLE);

            }

            //PROCESSED_QUESTION SECTION
            if (isProcessedQuestion) {

                for (int i = 0; i < 4; i++)
                    viewHolder.answerRoots[i].getBackground().mutate().setAlpha(90);

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


            }

            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingQuestionRoot.setVisibility(View.VISIBLE);

        }


        //CLOSED_QUESTION SECTION
        if (isClosedQuestion) {

            Answer correctAnswer = null;
            for (Answer answer : question.getAnswers()) {
                if (answer.isCorrect())
                    correctAnswer = answer;
            }
            if (correctAnswer == null)
                return;

            AnswerIdentifier userAnswerIdentifier = playerAnswers.containsKey(question.getId()) ? playerAnswers.get(question.getId()) : null;
            if (userAnswerIdentifier == null)
                return;

            boolean isUserAnswerCorrect = correctAnswer.getId().equals(userAnswerIdentifier.getAnswerId());
            int userBetSize = userAnswerIdentifier.getBetSize();
            int answerNumber = getAnswerNumber(question, correctAnswer.getId());
            final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());

            viewHolder.incomingCorrectWrongTitleTextView.setText(isUserAnswerCorrect ? "Correct :)" : "Wrong :-(");
            viewHolder.incomingCorrectAnswerTextView.setText(correctAnswer.getAnswerText());
            viewHolder.incomingCorrectAnswerTextView.setBackgroundResource(backgroundColorResourceId);
            viewHolder.incomingCorrectAnswerReturnTextView.setText(isUserAnswerCorrect ? "you earned " + (correctAnswer.getPointsMultiplier()* userBetSize) + " points" : "You didn't earn points");
            viewHolder.incomingFeedbackPlayerTextView.setText(isUserAnswerCorrect ? "Good job!" : "Don't worry, you'll nail it next time!");

            viewHolder.incomingMessagesRoot.setVisibility(View.VISIBLE);
            viewHolder.incomingClosedQuestionRoot.setVisibility(View.VISIBLE);

            return;
        }

    }

    private void postAnswer(Question question, Answer answer, View view, ViewHolder viewHolder) {
        final ViewHolder myViewHolder = viewHolder;
        final boolean isRandomlySelected = view == null;
        if (isRandomlySelected)
            viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
        else
            viewHolder.incomingSelectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);


        //answer.setTheAnswerOfTheUser(true);
        //viewHolder.incomingProcessingQuestionTitleTextView.setText(question.getQuestionText());
        viewHolder.incomingSelectedAnswerTextView.setText(answer.getAnswerText());

        int answerNumber = getAnswerNumber(question, answer.getId());
        int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
        viewHolder.incomingSelectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);



        final String gameId = question.getGameId();
        final String questionId = question.getId();
        final String answerId = answer.getId();
        final int betSize = Integer.parseInt(viewHolder.incomingBetSizeTextView.getText().toString());

        int returnValue = (int) (betSize * answer.getPointsMultiplier());
        viewHolder.incomingSelectedAnswerReturnTextView.setText(String.valueOf(returnValue));

        playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomlySelected, betSize));
        if (view != null) //null when random answer was selected
            view.animate().rotationX(360.0f).setDuration(200).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                myViewHolder.incomingQuestionRoot.setVisibility(View.GONE);
                myViewHolder.incomingProcessingQuestionRoot.setVisibility(View.VISIBLE);
                EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, isRandomlySelected, betSize));
            }
        }, 500);

    }

    private void resetWidgetsVisibility(ViewHolder viewHolder) {

        //reset all to gone
        viewHolder.incomingMessagesRoot.setVisibility(View.GONE);
        viewHolder.outgoingMessagesRoot.setVisibility(View.GONE);
        viewHolder.incomingTextMessageTextView.setVisibility(View.GONE);
        viewHolder.incomingQuestionRoot.setVisibility(View.GONE);
        viewHolder.incomingProcessingQuestionRoot.setVisibility(View.GONE);
        viewHolder.incomingClosedQuestionRoot.setVisibility(View.GONE);
        viewHolder.incomingBetPanelRoot.setVisibility(View.GONE);
        viewHolder.incomingTimeToAnswerTextView.setVisibility(View.GONE);

        for (int i = 0; i < 4; i++) {
            viewHolder.answerRoots[i].setVisibility(View.INVISIBLE);
            viewHolder.answerRoots[i].getBackground().mutate().setAlpha(255);
        }

    }


    private int getAnswerNumber(Question question, String answerId) {

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


}
