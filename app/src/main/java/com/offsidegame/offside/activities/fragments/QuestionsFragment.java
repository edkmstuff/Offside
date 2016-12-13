package com.offsidegame.offside.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.offsidegame.offside.adapters.QuestionAdapter;
import com.offsidegame.offside.adapters.ScoreAdapter;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;

import java.util.ArrayList;
import java.util.Arrays;


public class QuestionsFragment extends ListFragment {

    private Context context;
    private Question[] questions;


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        context = getContext();
    }

    @Override
    public void onStart(){
        super.onStart();
        //EventBus.getDefault().register(context);
    }

    @Override
    public void onStop(){
        //EventBus.getDefault().unregister(context);
        super.onStop();
    }


    public void updateData(Question[] questions) {
        ArrayList<Question> values = new ArrayList<>(Arrays.asList(questions));
        QuestionAdapter questionAdapter = new QuestionAdapter(context, values);
        setListAdapter(questionAdapter);
    }
}

