package com.offsidegame.offside.models;

import java.security.PublicKey;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Scoreboard {
    @com.google.gson.annotations.SerializedName("Scores")
    private Score[] scores;

    public Scoreboard(){

    }


    public Score[] getScores() {
        return scores;
    }


}
