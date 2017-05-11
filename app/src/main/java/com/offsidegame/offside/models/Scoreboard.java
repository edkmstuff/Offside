package com.offsidegame.offside.models;

import java.security.PublicKey;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Scoreboard {
    @com.google.gson.annotations.SerializedName("S")
    private Score[] scores;

    @com.google.gson.annotations.SerializedName("FU")
    private boolean forceUpdate;

    public Score[] getScores() {
        return scores;
    }

    public boolean isForceUpdate() {
        return forceUpdate;
    }
}
