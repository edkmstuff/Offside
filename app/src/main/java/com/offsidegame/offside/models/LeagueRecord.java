package com.offsidegame.offside.models;

/**
 * Created by user on 8/1/2017.
 */

public class LeagueRecord {


    @com.google.gson.annotations.SerializedName("P")
    private int position;

    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("PN")
    private String playerName;

    @com.google.gson.annotations.SerializedName("PO")
    private int points;

    @com.google.gson.annotations.SerializedName("FP")
    private int factorizedPoints;

    @com.google.gson.annotations.SerializedName("PI")
    private String playerId;


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getFactorizedPoints() {
        return factorizedPoints;
    }

    public void setFactorizedPoints(int factorizedPoints) {
        this.factorizedPoints = factorizedPoints;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }
}


