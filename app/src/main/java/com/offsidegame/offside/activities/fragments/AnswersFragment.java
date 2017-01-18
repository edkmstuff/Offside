package com.offsidegame.offside.activities.fragments;


import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.RoundImage;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionAnsweredEvent;
import com.offsidegame.offside.events.QuestionEvent;
import com.offsidegame.offside.adapters.AnswerAdapter;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Arrays;


public class AnswersFragment extends ListFragment {
    Context context;
    public Question question;
    public Answer[] answers;
    public String questionState;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        Activity parentActivity = getActivity();
//
//        IQuestionHolder activity = (IQuestionHolder) parentActivity;
//        context = parentActivity;
//
//
//        question = activity.getQuestion();
//        questionState = activity.getQuestionState();
//        answers = question.getAnswers();
//
//        ArrayList<Answer> values = new ArrayList<Answer>(Arrays.asList(answers));
//        Context context = getActivity();
//        AnswerAdapter answerAdapter = new AnswerAdapter(context, values, questionState, question.getId());
//        setListAdapter(answerAdapter);
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (questionState.equals(QuestionEvent.QuestionStates.NEW_QUESTION)) {


            final String gameId = question.getGameId();
            final String questionId = question.getId();
            final String answerId = answers[position].getId();



            ImageView userImageView =(ImageView) v.findViewById(R.id.answer_question_user_answer_image_view);
            v.animate().rotationX(360.0f).setDuration(200).start();
            loadFbImage(userImageView);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new QuestionAnsweredEvent(gameId, questionId, answerId, false));
                }
            }, 500);


        }
    }

    private void loadFbImage(final ImageView imageView) {
        SharedPreferences settings = context.getSharedPreferences(context.getString(R.string.preference_name), 0);
        String userPictureUrl = settings.getString(context.getString(R.string.user_profile_picture_url_key), "");
        Uri imageUri = Uri.parse(userPictureUrl);

        Picasso.with(context).load(imageUri).into(imageView, new com.squareup.picasso.Callback() {
            @Override
            public void onSuccess() {
                imageView.setAlpha(0.0f);
                Bitmap bm = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
                RoundImage roundedImage = new RoundImage(bm);
                imageView.setImageDrawable(roundedImage);
                imageView.animate().alpha(1.1f).setDuration(500).start();
            }

            @Override
            public void onError() {

            }
        });
    }

    public void  updateData(Question question, String questionState, Context context){
        this.question = question;
        this.questionState = questionState;
        this.context = context;
        answers = question.getAnswers();
        ArrayList<Answer> values = new ArrayList<Answer>(Arrays.asList(answers));
        AnswerAdapter answerAdapter = new AnswerAdapter(context, values, questionState, question.getId());
        setListAdapter(answerAdapter);
    }
}

