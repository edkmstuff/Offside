package com.offsidegame.offside.models;

/**
 * Created by user on 4/25/2017.
 */

public class Winner {

    @com.google.gson.annotations.SerializedName("PI")
    private String playerId;
    @com.google.gson.annotations.SerializedName("UN")
    private String userName;
    @com.google.gson.annotations.SerializedName("P")
    private double points;
    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;
    @com.google.gson.annotations.SerializedName("OC")
    private int offsideCoins;
    @com.google.gson.annotations.SerializedName("GP")
    private int generalPosition;
    @com.google.gson.annotations.SerializedName("PGP")
    private int privateGamePosition;
    @com.google.gson.annotations.SerializedName("T")
    private int trophies;




    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(int offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public int getGeneralPosition() {
        return generalPosition;
    }

    public void setGeneralPosition(int generalPosition) {
        this.generalPosition = generalPosition;
    }

    public int getPrivateGamePosition() {
        return privateGamePosition;
    }

    public void setPrivateGamePosition(int privateGamePosition) {
        this.privateGamePosition = privateGamePosition;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public String getPlayerId() {
        return playerId;
    }
}
