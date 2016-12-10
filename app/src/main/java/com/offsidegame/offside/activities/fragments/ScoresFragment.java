package com.offsidegame.offside.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ListFragment;
import android.support.v4.app.Fragment;
import android.view.View;
import android.widget.ListView;

import com.offsidegame.offside.adapters.AnswerAdapter;
import com.offsidegame.offside.adapters.ScoreAdapter;
import com.offsidegame.offside.models.Answer;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;
import com.offsidegame.offside.models.ScoreboardEvent;
import com.offsidegame.offside.models.interfaces.IQuestionHolder;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionAnsweredEvent;
import com.offsidegame.offside.models.QuestionEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;


public class ScoresFragment extends ListFragment {

    private Context context;
    private Scoreboard scoreboard;


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


    public void updateData(Scoreboard scoreboard) {
        ArrayList<Score> values = new ArrayList<>(Arrays.asList(scoreboard.getScores()));
        ScoreAdapter scoreAdapter = new ScoreAdapter(context, values);
        setListAdapter(scoreAdapter);
        //scoreAdapter.notifyDataSetChanged();
    }
}

