package com.offsidegame.offside.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.CountDownTimer;
import android.os.Handler;
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


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ChatMessage chatMessage = getItem(position);
        String chatMessageType = chatMessage.getMessageType();

        //if (chatMessage.isIncoming()) {
        if (chatMessageType.equals(OffsideApplication.getMessageTypeText())) { //"TEXT"

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_list_item, parent, false);
            generateTextChatMessage(convertView, chatMessage, !chatMessage.isIncoming());

        } else if (chatMessageType.equals(OffsideApplication.getMessageTypeAskedQuestion())) { //"ASKED_QUESTION"

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
            generateQuestionChatMessage(convertView, chatMessage);

        } else if (chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion())) { //"PROCESSED_QUESTION"

            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
            generateQuestionChatMessage(convertView, chatMessage);

        } else if (chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion())) { //"CLOSED_QUESTION"
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
            generateQuestionChatMessage(convertView, chatMessage);
        }


//        } else {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_list_item, parent, false);
//            generateTextChatMessage(convertView, chatMessage, true);
//        }


        return convertView;

    }

    private void generateTextChatMessage(View convertView, ChatMessage chatMessage, boolean isRightToLeft) {
        LinearLayout root = (LinearLayout) convertView.findViewById(R.id.cm_root);
        ImageView profilePictureImageView = (ImageView) convertView.findViewById(R.id.cm_profile_picture_image_view);
        TextView timeSentTextView = (TextView) convertView.findViewById(R.id.cm_time_sent_text_view);

        root.setLayoutDirection(isRightToLeft ? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR);

        Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());
        loadFbImage(profilePictureImageView, profilePictureUri);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));
        TextView messageTextView = (TextView) convertView.findViewById(R.id.cm_message_text_view);
        messageTextView.setText(chatMessage.getMessageText());
    }

    private void generateQuestionChatMessage(View convertView, ChatMessage chatMessage) {

        //chat message properties
        final ImageView profilePictureImageView = (ImageView) convertView.findViewById(R.id.cmaq_profile_picture_image_view);
        final TextView timeSentTextView = (TextView) convertView.findViewById(R.id.cmaq_time_sent_text_view);
        final Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());
        loadFbImage(profilePictureImageView, profilePictureUri);
        final SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));

        //bind Question object to the ui elements
        final Gson gson = new GsonBuilder().create();
        final Question question = gson.fromJson(chatMessage.getMessageText(), Question.class);
        final String questionId = question.getId();

        final boolean isAskedQuestion = chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeAskedQuestion());
        final boolean isProcessedQuestion = chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeProcessedQuestion());
        final boolean isClosedQuestion = chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeClosedQuestion());
        final boolean isPlayerAnsweredQuestion = playerAnswers.containsKey(questionId);

        //open question but user already answered it
        if (isAskedQuestion && isPlayerAnsweredQuestion) {
            final String userAnswerId = playerAnswers.get(questionId).getAnswerId();
            int betSize = playerAnswers.get(questionId).getBetSize();
            int answerNumber = getAnswerNumber(question, userAnswerId);
            if (answerNumber == 0)
                return;

            final Answer answerOfTheUser = question.getAnswers()[answerNumber - 1];

            //find elements
            final LinearLayout processingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_processing_question_root);
            final LinearLayout questionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_question_root);
            final LinearLayout closedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_closed_question_root);
            final TextView processedQuestionTextView = (TextView) convertView.findViewById(R.id.cmaq_processed_question_text_view);
            final TextView selectedAnswerTextView = (TextView) convertView.findViewById(R.id.cmaq_selected_answer_text_view);
            final TextView selectedAnswerReturnValueTextView = (TextView) convertView.findViewById(R.id.cmaq_selected_answer_return_text_view);
            final TextView selectedAnswerTitleTextView = (TextView) convertView.findViewById(R.id.cmaq_selected_answer_title_text_view);

            //set values to elements
            processedQuestionTextView.setText(question.getQuestionText());
            selectedAnswerTextView.setText(answerOfTheUser.getAnswerText());

            int returnValue = (int) (betSize*answerOfTheUser.getPointsMultiplier());
            selectedAnswerReturnValueTextView.setText(String.valueOf(returnValue));

            final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
            selectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);

            if (playerAnswers.get(questionId).isRandomlySelected())
                selectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
            else
                selectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);

            //show relevant layout
            processingQuestionRoot.setVisibility(View.VISIBLE);
            questionRoot.setVisibility(View.GONE);
            closedQuestionRoot.setVisibility(View.GONE);

            return;
        }

        //ASKED_QUESTION elements

        final TextView betSizeTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_bet_size_text_view) : null;
        final SeekBar betSizeSeekBar = isAskedQuestion ? (SeekBar) convertView.findViewById(R.id.cmaq_bet_size_seekBar) : null;
        final TextView timeToAnswerTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_time_to_answer_text_view) : null;
        final TextView selectedAnswerTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_selected_answer_text_view) : null;
        final TextView selectedAnswerReturnValueTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_selected_answer_return_text_view) : null;
        final TextView selectedAnswerTitleTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_selected_answer_title_text_view) : null;

        //COMMON elements
        final TextView processedQuestionTextView = (TextView) convertView.findViewById(R.id.cmaq_processed_question_text_view);
        final LinearLayout betPanelRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_bet_panel_root);
        final TextView questionTextView = (TextView) convertView.findViewById(R.id.cmaq_question_text_view);
        final LinearLayout processingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_processing_question_root);
        final LinearLayout questionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_question_root);
        final LinearLayout closedQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_closed_question_root);


        final TextView[] answerReturnTextViews = new TextView[4];
        final TextView[] answerPercentTextViews = new TextView[4];
        final TextView[] answerTextViews = new TextView[4];
        final LinearLayout[] answerRoots = new LinearLayout[4];
        for (int i = 0; i < 4; i++) {
            final int answerNumber = i + 1;
            final int returnTextViewResourceId = context.getResources().getIdentifier("cmaq_answer_" + answerNumber + "_return_text_view", "id", context.getPackageName());
            final int percentTextViewResourceId = context.getResources().getIdentifier("cmaq_answer_" + answerNumber + "_percent_text_view", "id", context.getPackageName());
            final int answerTextViewResourceId = context.getResources().getIdentifier("cmaq_answer_" + answerNumber + "_text_view", "id", context.getPackageName());
            final int answerRootResourceId = context.getResources().getIdentifier("cmaq_answer_" + answerNumber + "_root", "id", context.getPackageName());
            answerReturnTextViews[i] = (TextView) convertView.findViewById(returnTextViewResourceId);
            answerPercentTextViews[i] = (TextView) convertView.findViewById(percentTextViewResourceId);
            answerTextViews[i] = (TextView) convertView.findViewById(answerTextViewResourceId);

            answerRoots[i] = (LinearLayout) convertView.findViewById(answerRootResourceId);
            //init values
            answerRoots[i].setVisibility(View.INVISIBLE);
            answerRoots[i].getBackground().mutate().setAlpha(255);

        }

        //set values
        questionRoot.setVisibility(View.GONE);
        processingQuestionRoot.setVisibility(View.GONE);
        closedQuestionRoot.setVisibility(View.GONE);
        betPanelRoot.setVisibility(View.GONE);
        questionTextView.setText(question.getQuestionText());
        processedQuestionTextView.setText(question.getQuestionText());

        final Answer[] answers = question.getAnswers();
        final int minBetSize = 100;
        int maxBetSize = 4;
        for (int i = 0; i < answers.length; i++) {
            final String answerText = answers[i].getAnswerText();
            final String percentUserAnswered = String.valueOf((int) answers[i].getPercentUsersAnswered()) + "%";
            final double defaultReturnValue = answers[i].getPointsMultiplier() * minBetSize;

            answerTextViews[i].setText(answerText);
            //answerTextViews[i].setTag(answers[i]);
            if (isAskedQuestion) {
                answerReturnTextViews[i].setText(String.valueOf(defaultReturnValue));
                answerReturnTextViews[i].setVisibility(View.VISIBLE);
                answerPercentTextViews[i].setVisibility(View.GONE);

            } else if (isProcessedQuestion) {
                answerPercentTextViews[i].setText(percentUserAnswered);
                answerPercentTextViews[i].setVisibility(View.VISIBLE);
                answerReturnTextViews[i].setVisibility(View.GONE);
            }

            answerRoots[i].setVisibility(View.VISIBLE);
        }

        //ASKED_QUESTION SECTION
        if (isAskedQuestion) {
            SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
            int balance = settings.getInt(context.getString(R.string.balance_key), 0);

            int maxSeekBarValue = (int) (Math.floor(balance/100)-1);
            maxBetSize = maxSeekBarValue > 4 ? 4: maxSeekBarValue < 0 ? 0 : maxSeekBarValue; //limit range to 0-4

            betSizeTextView.setText(String.valueOf(minBetSize));

            betSizeSeekBar.setProgress(0);
            betSizeSeekBar.setMax(maxBetSize);
            betSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener(){

                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    int adjustedProgress = 100 + progress*100;
                    betSizeTextView.setText(String.valueOf(adjustedProgress));
                    for (int i = 0; i < answers.length; i++) {
                        final double defaultReturnValue = answers[i].getPointsMultiplier() * adjustedProgress;
                        answerReturnTextViews[i].setText(String.valueOf(defaultReturnValue));

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
                        timeToAnswerTextView.setText(Integer.toString(secondsLeft));
                        if (secondsLeft < 7 && secondsLeft > 3)
                            timeToAnswerTextView.setBackgroundColor(Color.parseColor("#FFAB00"));
                        if (secondsLeft < 4)
                            timeToAnswerTextView.setBackgroundColor(Color.RED);
                    }
                }

                @Override
                public void onFinish() {
                    //user did not answer this question, we select random answer
                    boolean isAnswered = false;
                    for (Answer answer : question.getAnswers()) {
                        if (answer.isTheAnswerOfTheUser()) {
                            isAnswered = true;
                            break;
                        }
                    }
                    if (!isAnswered) {
                        int answersCount = question.getAnswers().length;
                        int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
                        Answer randomAnswer = question.getAnswers()[selectedAnswerIndex];
                        postAnswer(question, randomAnswer, null, selectedAnswerTextView, selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot, selectedAnswerTitleTextView, processedQuestionTextView, betSizeTextView);
                    }
                }
            }.start();

            //set on click event to answers
            for (int i = 0; i < answers.length; i++) {
                final Answer clickedAnswer = answers[i];
                answerRoots[i].setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        postAnswer(question, clickedAnswer, view, selectedAnswerTextView, selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot, selectedAnswerTitleTextView, processedQuestionTextView, betSizeTextView);
                    }
                });
            }

            questionRoot.setVisibility(View.VISIBLE);
            betPanelRoot.setVisibility(View.VISIBLE);
            timeToAnswerTextView.setVisibility(View.VISIBLE);


        }

        //PROCESSED_QUESTION SECTION
        if (isProcessedQuestion) {
            questionRoot.setVisibility(View.VISIBLE);

            for (int i = 0; i < 4; i++)
                answerRoots[i].getBackground().mutate().setAlpha(90);

            if (playerAnswers.containsKey(questionId)) {
                String userAnswerId = playerAnswers.get(questionId).getAnswerId();

                for (int i = 0; i < answers.length; i++) {
                    if (answers[i].getId().equals(userAnswerId)) {
                        final int answerNumber = i + 1;
                        final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
                        answerRoots[i].getBackground().mutate().setAlpha(255);
                        answerRoots[i].setBackgroundResource(backgroundColorResourceId);
                        break;
                    }

                }
            }
        }


        //CLOSED_QUESTION SECTION
        if (isClosedQuestion) {
            closedQuestionRoot.setVisibility(View.VISIBLE);
            Answer correctAnswer = null;
            for (Answer answer:question.getAnswers() ){
                if (answer.isCorrect())
                    correctAnswer = answer;
            }
            if (correctAnswer == null)
                return;

            boolean isUserAnswerCorrect = correctAnswer.isTheAnswerOfTheUser();
            int answerNumber = getAnswerNumber(question, correctAnswer.getId());
            final int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());


            final TextView correctWrongTitleTextView = (TextView) convertView.findViewById(R.id.cmaq_correct_wrong_title_text_view);
            final TextView correctAnswerTextView = (TextView) convertView.findViewById(R.id.cmaq_correct_answer_text_view);
            final TextView correctAnswerReturnTextView = (TextView) convertView.findViewById(R.id.cmaq_correct_answer_return_text_view);
            final TextView feedbackPlayerTextView = (TextView) convertView.findViewById(R.id.cmaq_feedback_player_text_view);


            correctWrongTitleTextView.setText(isUserAnswerCorrect? "Correct :)" : "Wrong :-(");
            correctAnswerTextView.setText(correctAnswer.getAnswerText());
            correctAnswerTextView.setBackgroundResource(backgroundColorResourceId);
            correctAnswerReturnTextView.setText(isUserAnswerCorrect? "you earned " + correctAnswer.getScore() + " points" : "You didn't earn points");
            feedbackPlayerTextView.setText(isUserAnswerCorrect? "Good job!" : "Don't worry, you'll nail it next time!");

            return;


        }

    }

    private void postAnswer(Question question, Answer answer, View view, TextView selectedAnswerTextView, TextView selectedAnswerReturnValueTextView, LinearLayout processingQuestionRoot, LinearLayout questionRoot, TextView selectedAnswerTitleTextView, TextView processingQuestionTextView, TextView betSizeTextView) {
        final boolean isRandomlySelected = view == null;
        if (isRandomlySelected)
            selectedAnswerTitleTextView.setText(R.string.lbl_randomly_selected_answer_title);
        else
            selectedAnswerTitleTextView.setText(R.string.lbl_user_selected_answer_title);


        answer.setTheAnswerOfTheUser(true);
        processingQuestionTextView.setText(question.getQuestionText());
        selectedAnswerTextView.setText(answer.getAnswerText());

        int answerNumber = getAnswerNumber(question, answer.getId());
        int backgroundColorResourceId = context.getResources().getIdentifier("answer" + answerNumber + "backgroundColor", "color", context.getPackageName());
        selectedAnswerTextView.setBackgroundResource(backgroundColorResourceId);

        processingQuestionRoot.setVisibility(View.VISIBLE);
        questionRoot.setVisibility(View.GONE);

        final String gameId = question.getGameId();
        final String questionId = question.getId();
        final String answerId = answer.getId();
        final int betSize = Integer.parseInt(betSizeTextView.getText().toString());

        int returnValue = (int) (betSize*answer.getPointsMultiplier());
        selectedAnswerReturnValueTextView.setText(String.valueOf(returnValue));

        playerAnswers.put(questionId, new AnswerIdentifier(answerId, isRandomlySelected, betSize));
        if (view != null) //null when random answer was selected
            view.animate().rotationX(360.0f).setDuration(200).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, isRandomlySelected, betSize));
            }
        }, 500);

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
