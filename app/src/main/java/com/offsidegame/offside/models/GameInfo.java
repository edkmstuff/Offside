package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class GameInfo {

    @com.google.gson.annotations.SerializedName("P")
    private Player player;

    @com.google.gson.annotations.SerializedName("TTGBTP")
    private int timeToGoBackToPlayerScore;


    public int getTimeToGoBackToPlayerScore() {
        return timeToGoBackToPlayerScore;
    }

    public void setTimeToGoBackToPlayerScore(int timeToGoBackToPlayerScore) {
        this.timeToGoBackToPlayerScore = timeToGoBackToPlayerScore;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
