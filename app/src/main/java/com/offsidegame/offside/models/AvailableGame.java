package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class AvailableGame {


    @com.google.gson.annotations.SerializedName("I")
    private String gameId;

    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }
}
