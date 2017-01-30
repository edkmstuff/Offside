package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class GameInfo {


    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;

    @com.google.gson.annotations.SerializedName("GC")
    private String gameCode;

    @com.google.gson.annotations.SerializedName("TTGBTP")
    private int timeToGoBackToPlayerScore;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getTimeToGoBackToPlayerScore() {
        return timeToGoBackToPlayerScore;
    }

    public void setTimeToGoBackToPlayerScore(int timeToGoBackToPlayerScore) {
        this.timeToGoBackToPlayerScore = timeToGoBackToPlayerScore;
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }
}
