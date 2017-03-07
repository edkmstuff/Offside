package com.offsidegame.offside.models;

import java.util.Map;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Player {

    @com.google.gson.annotations.SerializedName("UN")
    private String userName;
    @com.google.gson.annotations.SerializedName("GI")
    private String gameId ;
    @com.google.gson.annotations.SerializedName("PGC")
    private String privateGameCode;
    @com.google.gson.annotations.SerializedName("PA")
    private Map<String, AnswerIdentifier> playerAnswers ;
    @com.google.gson.annotations.SerializedName("P")
    private double points;
    @com.google.gson.annotations.SerializedName("I")
    private String id ;
    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;
    @com.google.gson.annotations.SerializedName("PF")
    private GameFeature[] premiumFeatures;
    @com.google.gson.annotations.SerializedName("OC")
    private int offsideCoins;
    @com.google.gson.annotations.SerializedName("B")
    private int balance;

    @com.google.gson.annotations.SerializedName("POS")
    private int position;

//    @com.google.gson.annotations.SerializedName("P")
//    private int position;
//

//    public int getPosition() {
//        return position;
//    }
//
//    public void setPosition(int position) {
//        this.position = position;
//    }

//    public String getName() {
//        return name;
//    }
//
//    public void setName(String name) {
//        this.name = name;
//    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public double getPoints() {
        return points;
    }

    public void setPoints(double points) {
        this.points = points;
    }

    public String getPrivateGameCode() {
        return privateGameCode;
    }

    public void setPrivateGameCode(String privateGameCode) {
        this.privateGameCode = privateGameCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public Map<String, AnswerIdentifier> getPlayerAnswers() {
        return playerAnswers;
    }

    public void setPlayerAnswers(Map<String, AnswerIdentifier> playerAnswers) {
        this.playerAnswers = playerAnswers;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public GameFeature[] getPremiumFeatures() {
        return premiumFeatures;
    }

    public void setPremiumFeatures(GameFeature[] premiumFeatures) {
        this.premiumFeatures = premiumFeatures;
    }

    public int getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(int offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
