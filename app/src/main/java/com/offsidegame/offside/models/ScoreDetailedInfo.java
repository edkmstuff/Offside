package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 12/27/2017.
 */

public class ScoreDetailedInfo {

    @com.google.gson.annotations.SerializedName("PAC")
    private ArrayList<PlayerActivity> playerActivities;

    public ScoreDetailedInfo(ArrayList<PlayerActivity> playerActivities) {
        this.playerActivities = playerActivities;
    }

    public ArrayList<PlayerActivity> getPlayerActivities() {

        if(playerActivities==null)
            playerActivities = new ArrayList<PlayerActivity>();


        Collections.sort(playerActivities, new Comparator<PlayerActivity>() {
            @Override
            public int compare(PlayerActivity playerActivity, PlayerActivity other) {
                if (playerActivity.getQuestionStartTime().before(other.getQuestionStartTime()))
                    return -1;
                if (playerActivity.getQuestionStartTime().after(other.getQuestionStartTime()))
                    return 1;

                return 0;
            }
        });


        return playerActivities;
    }

    public void setPlayerActivities(ArrayList<PlayerActivity> playerActivities) {
        this.playerActivities = playerActivities;
    }
}
