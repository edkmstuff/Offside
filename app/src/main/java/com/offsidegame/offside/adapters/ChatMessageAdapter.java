package com.offsidegame.offside.adapters;

import android.content.Context;
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
    private Map<String, String> playerAnswers;


    public ChatMessageAdapter(Context context, ArrayList<ChatMessage> chatMessages, Map<String, String> playerAnswers) {
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
                generateQuestionChatMessage(convertView, chatMessage );

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeProcessedQuestion())) { //"PROCESSED_QUESTION"

                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
                generateQuestionChatMessage(convertView, chatMessage );

            } else if (chatMessageType.equals(OffsideApplication.getMessageTypeClosedQuestion())) { //"CLOSED_QUESTION"
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.chat_message_asked_question_item, parent, false);
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

        root.setLayoutDirection(isRightToLeft? View.LAYOUT_DIRECTION_RTL : View.LAYOUT_DIRECTION_LTR );

        Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());
        loadFbImage(profilePictureImageView, profilePictureUri);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));
        TextView messageTextView = (TextView) convertView.findViewById(R.id.cm_message_text_view);
        messageTextView.setText(chatMessage.getMessageText());
    }

    private void generateQuestionChatMessage(View convertView, ChatMessage chatMessage) {

        //chat message properties
        ImageView profilePictureImageView = (ImageView) convertView.findViewById(R.id.cmaq_profile_picture_image_view);
        TextView timeSentTextView = (TextView) convertView.findViewById(R.id.cmaq_time_sent_text_view);
        Uri profilePictureUri = Uri.parse(chatMessage.getImageUrl());
        loadFbImage(profilePictureImageView, profilePictureUri);
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
        timeSentTextView.setText(timeFormat.format(chatMessage.getSentTime()));

        //bind Question object to the ui elements
        Gson gson = new GsonBuilder().create();
        final Question question = gson.fromJson(chatMessage.getMessageText(), Question.class);

        String questionId = question.getId();
        if (playerAnswers.containsKey(questionId) && chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeAskedQuestion())) {
            String userAnswerId = playerAnswers.get(questionId);
            Answer answerOfTheUser = new Answer();
            for (Answer answer : question.getAnswers()) {
                if (answer.getId().equals(userAnswerId) ) {
                    answerOfTheUser = answer;
                    break;
                }
            }

            LinearLayout processingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_processing_question_root);
            LinearLayout questionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_question_root);
            TextView selectedAnswerTextView = (TextView) convertView.findViewById(R.id.cmaq_selected_answer_text_view);
            TextView selectedAnswerReturnValueTextView = (TextView) convertView.findViewById(R.id.cmaq_selected_answer_return_text_view);

            //set the user answer
            selectedAnswerTextView.setText(answerOfTheUser.getAnswerText());
            selectedAnswerReturnValueTextView.setText("100");  //todo: update with the right one
            processingQuestionRoot.setVisibility(View.VISIBLE);
            questionRoot.setVisibility(View.GONE);
            return;
        }

        boolean isAskedQuestion= chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeAskedQuestion());
        boolean isProcessedQuestion= chatMessage.getMessageType().equals(OffsideApplication.getMessageTypeProcessedQuestion());

        //ASKED_QUESTION elements

        TextView betSizeTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_bet_size_text_view): null;
        SeekBar betSizeSeekBar = isAskedQuestion ? (SeekBar) convertView.findViewById(R.id.cmaq_bet_size_seekBar): null;
        final TextView timeToAnswerTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_time_to_answer_text_view): null;
        final TextView selectedAnswerTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_selected_answer_text_view): null;
        final TextView selectedAnswerReturnValueTextView = isAskedQuestion ? (TextView) convertView.findViewById(R.id.cmaq_selected_answer_return_text_view): null;


        //COMMON elements
        final LinearLayout betPanelRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_bet_panel_root);

        TextView answer1ReturnValueTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_1_return_text_view);
        TextView answer2ReturnValueTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_2_return_text_view);
        TextView answer3ReturnValueTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_3_return_text_view);
        TextView answer4ReturnValueTextView = (TextView) convertView.findViewById(R.id.cmaq_answer_4_return_text_view);


        TextView answer1PercentTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_1_percent_text_view);
        TextView answer2PercentTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_2_percent_text_view) ;
        TextView answer3PercentTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_3_percent_text_view);
        TextView answer4PercentTextView =  (TextView) convertView.findViewById(R.id.cmaq_answer_4_percent_text_view);

        TextView questionTextView = (TextView) convertView.findViewById(R.id.cmaq_question_text_view);
        final TextView answer1TextView = (TextView) convertView.findViewById(R.id.cmaq_answer_1_text_view);
        final TextView answer2TextView = (TextView) convertView.findViewById(R.id.cmaq_answer_2_text_view);
        final TextView answer3TextView = (TextView) convertView.findViewById(R.id.cmaq_answer_3_text_view);
        final TextView answer4TextView = (TextView) convertView.findViewById(R.id.cmaq_answer_4_text_view);

        final LinearLayout answer1Root = (LinearLayout) convertView.findViewById(R.id.cmaq_answers_1_root);
        final LinearLayout answer2Root = (LinearLayout) convertView.findViewById(R.id.cmaq_answers_2_root);
        final LinearLayout answer3Root = (LinearLayout) convertView.findViewById(R.id.cmaq_answers_3_root);
        final LinearLayout answer4Root = (LinearLayout) convertView.findViewById(R.id.cmaq_answers_4_root);

        final LinearLayout processingQuestionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_processing_question_root);
        final LinearLayout questionRoot = (LinearLayout) convertView.findViewById(R.id.cmaq_question_root);

        questionRoot.setVisibility(View.VISIBLE);
        processingQuestionRoot.setVisibility(View.GONE);

        questionTextView.setText(question.getQuestionText());

        answer1Root.setVisibility(View.INVISIBLE);
        answer2Root.setVisibility(View.INVISIBLE);
        answer3Root.setVisibility(View.INVISIBLE);
        answer4Root.setVisibility(View.INVISIBLE);

//        if(isProcessedQuestion){
//            answer1PercentTextView.setVisibility(View.GONE);
//            answer2PercentTextView.setVisibility(View.GONE);
//            answer3PercentTextView.setVisibility(View.GONE);
//            answer4PercentTextView.setVisibility(View.GONE);
//        }

        Answer[] answers = question.getAnswers();
        double[] multipliers = new double[]{1.5, 2.5, 3, 5};
        int minBetSize = 10;
        for (int i = 0; i < answers.length; i++) {
            String answerText = answers[i].getAnswerText();
            String percentUserAnswered = String.valueOf(answers[i].getPercentUsersAnswered());
            double defaultReturnValue = multipliers[i] * minBetSize;

            if (i == 0) {
                answer1TextView.setText(answerText);
                answer1TextView.setTag(answers[i]);
                if(isAskedQuestion){
                    answer1ReturnValueTextView.setText(String.valueOf(defaultReturnValue));
                    answer1ReturnValueTextView.setVisibility(View.VISIBLE);
                    answer1PercentTextView.setVisibility(View.GONE);
                }
                if(isProcessedQuestion){
                    answer1PercentTextView.setText(percentUserAnswered);
                    answer1PercentTextView.setVisibility(View.VISIBLE);
                    answer1ReturnValueTextView.setVisibility(View.GONE);
                }
                answer1Root.setVisibility(View.VISIBLE);

            } else if (i == 1) {
                answer2TextView.setText(answerText);
                answer2TextView.setTag(answers[i]);
                if(isAskedQuestion){
                    answer2ReturnValueTextView.setText(String.valueOf(defaultReturnValue));
                    answer2ReturnValueTextView.setVisibility(View.VISIBLE);
                    answer2PercentTextView.setVisibility(View.GONE);
                }
                if(isProcessedQuestion){
                    answer2PercentTextView.setText(percentUserAnswered);
                    answer2PercentTextView.setVisibility(View.VISIBLE);
                    answer2ReturnValueTextView.setVisibility(View.GONE);
                }
                answer2Root.setVisibility(View.VISIBLE);
            } else if (i == 2) {
                answer3TextView.setText(answerText);
                answer3TextView.setTag(answers[i]);
                if(isAskedQuestion){
                    answer3ReturnValueTextView.setText(String.valueOf(defaultReturnValue));
                    answer3ReturnValueTextView.setVisibility(View.VISIBLE);
                    answer3PercentTextView.setVisibility(View.GONE);
                }
                if(isProcessedQuestion){
                    answer3PercentTextView.setText(percentUserAnswered);
                    answer3PercentTextView.setVisibility(View.VISIBLE);
                    answer3ReturnValueTextView.setVisibility(View.GONE);
                }

                answer3Root.setVisibility(View.VISIBLE);
            } else if (i == 3) {
                answer4TextView.setText(answerText);
                answer4TextView.setTag(answers[i]);
                if(isAskedQuestion){
                    answer4ReturnValueTextView.setText(String.valueOf(defaultReturnValue));
                    answer4ReturnValueTextView.setVisibility(View.VISIBLE);
                    answer4PercentTextView.setVisibility(View.GONE);
                }
                if(isProcessedQuestion){
                    answer4PercentTextView.setText(percentUserAnswered);
                    answer4PercentTextView.setVisibility(View.VISIBLE);
                    answer4ReturnValueTextView.setVisibility(View.GONE);
                }
                answer4Root.setVisibility(View.VISIBLE);
            }
        }

        //ASKED_QUESTION SECTION
        if(isAskedQuestion){

            betSizeTextView.setText(String.valueOf(minBetSize));
            betSizeSeekBar.setProgress(minBetSize);

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
                    for (Answer answer:question.getAnswers()){
                        if (answer.isTheAnswerOfTheUser()){
                            isAnswered = true;
                            break;
                        }
                    }
                    if (!isAnswered) {
                        int answersCount = question.getAnswers().length;
                        int selectedAnswerIndex = (int) (Math.floor(Math.random() * answersCount));
                        Answer randomAnswer = question.getAnswers()[selectedAnswerIndex];
                        postAnswer(question, randomAnswer, null, selectedAnswerTextView,  selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot);
                    }
                }
            }.start();

            //set on click event to answers
            answer1Root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Answer answer = (Answer) answer1TextView.getTag();
                    postAnswer(question, answer, view, selectedAnswerTextView,  selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot);

                }
            });

            answer2Root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Answer answer = (Answer) answer2TextView.getTag();
                    postAnswer(question, answer, view, selectedAnswerTextView,  selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot);
                }
            });

            answer3Root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Answer answer = (Answer) answer3TextView.getTag();
                    postAnswer(question,answer, view, selectedAnswerTextView,  selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot);
                }
            });

            answer4Root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Answer answer = (Answer) answer4TextView.getTag();
                    postAnswer(question, answer, view, selectedAnswerTextView,  selectedAnswerReturnValueTextView, processingQuestionRoot, questionRoot);
                }
            });
        }

        //PROCESSED_QUESTION SECTION

        if(isProcessedQuestion){
            processingQuestionRoot.setVisibility(View.GONE);
            questionRoot.setVisibility(View.VISIBLE);
            betPanelRoot.setVisibility(View.GONE);


            answer2Root.getBackground().setAlpha(90);
            answer3Root.getBackground().setAlpha(90);
            answer4Root.getBackground().setAlpha(90);


        }


    }

    private void postAnswer(Question question, Answer answer, View view,TextView selectedAnswerTextView, TextView selectedAnswerReturnValueTextView, LinearLayout processingQuestionRoot, LinearLayout questionRoot) {
        answer.setTheAnswerOfTheUser(true);
        selectedAnswerTextView.setText(answer.getAnswerText());
        selectedAnswerReturnValueTextView.setText("100");  //todo: update with the right one
        processingQuestionRoot.setVisibility(View.VISIBLE);
        questionRoot.setVisibility(View.GONE);

        final String gameId = question.getGameId();
        final String questionId = question.getId();

        final String answerId = answer.getId();
        playerAnswers.put(questionId, answerId);
        if (view != null) //null when random answer was selected
            view.animate().rotationX(360.0f).setDuration(200).start();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, false));
            }
        }, 500);

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
