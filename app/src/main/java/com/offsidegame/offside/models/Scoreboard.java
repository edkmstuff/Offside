package com.offsidegame.offside.models;

import java.security.PublicKey;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Scoreboard {
    @com.google.gson.annotations.SerializedName("S")
    private Score[] scores;

    public Score[] getScores() {
        return scores;
    }

    public void setScores(Score[] scores) {
        this.scores = scores;
    }
}
