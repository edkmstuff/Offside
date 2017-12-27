package com.offsidegame.offside.models;

/**
 * Created by user on 7/16/2017.
 */

public class PlayerAssets {

    @com.google.gson.annotations.SerializedName("B")
    private int balance;

    @com.google.gson.annotations.SerializedName("PI")
    private int powerItems;

    @com.google.gson.annotations.SerializedName("IU")
    private String ImageUrl;

    @com.google.gson.annotations.SerializedName("PLI")
    private String playerId;

    @com.google.gson.annotations.SerializedName("PN")
    private String playerName;

    @com.google.gson.annotations.SerializedName("PC")
    private String playerColor;

    @com.google.gson.annotations.SerializedName("WSC")
    private int wheelSpinCredits;



    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPowerItems() {
        return powerItems;
    }

    public void setPowerItems(int powerItems) {
        this.powerItems = powerItems;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerColor() {
        return playerColor;
    }

    public void setPlayerColor(String playerColor) {
        this.playerColor = playerColor;
    }


    public int getWheelSpinCredits() {
        return wheelSpinCredits;
    }

    public void setWheelSpinCredits(int wheelSpinCredits) {
        this.wheelSpinCredits = wheelSpinCredits;
    }
}
