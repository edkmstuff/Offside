package com.offsidegame.offside.activities.fragments;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ListFragment;

import com.offsidegame.offside.adapters.ScoreAdapter;
import com.offsidegame.offside.models.Score;
import com.offsidegame.offside.models.Scoreboard;

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
