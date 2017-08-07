package com.offsidegame.offside.models;

import com.offsidegame.offside.R;

import java.util.Map;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Player {

    @com.google.gson.annotations.SerializedName("N")
    private String userName;
    @com.google.gson.annotations.SerializedName("GI")
    private String gameId ;
    @com.google.gson.annotations.SerializedName("PGC")
    private String privateGameCode;
    @com.google.gson.annotations.SerializedName("PA")
    private Map<String, AnswerIdentifier> playerAnswers ;

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
    @com.google.gson.annotations.SerializedName("GP")
    private int generalPosition;
    @com.google.gson.annotations.SerializedName("PGP")
    private int privateGamePosition;
    @com.google.gson.annotations.SerializedName("FP")
    private int firstPrize;
    @com.google.gson.annotations.SerializedName("SP")
    private int secondPrize;
    @com.google.gson.annotations.SerializedName("TP")
    private int thirdPrize;
    @com.google.gson.annotations.SerializedName("RVWC")
    private int rewardVideoWatchCount;
    @com.google.gson.annotations.SerializedName("PI")
    private int powerItems;
    @com.google.gson.annotations.SerializedName("LOE")
    private String levelOfExperience;

    private static String levelOfExperience1 = "Begginer";
    private static String levelOfExperience2 = "Wonderboy";
    private static String levelOfExperience3 = "Pro";
    private static String levelOfExperience4 = "Star";
    private static String levelOfExperience5 = "Legend";





    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
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

    public int getRewardVideoWatchCount() {
        return rewardVideoWatchCount;
    }

    public void setRewardVideoWatchCount(int rewardVideoWatchCount) {
        this.rewardVideoWatchCount = rewardVideoWatchCount;
    }

    public void incrementRewardVideoWatchCount() {
        this.rewardVideoWatchCount++;
    }

    public int getPowerItems() {
        return powerItems;
    }

    public void setPowerItems(int powerItems) {
        this.powerItems = powerItems;
    }

    public int getFirstPrize() {
        return firstPrize;
    }

    public void setFirstPrize(int firstPrize) {
        this.firstPrize = firstPrize;
    }

    public int getSecondPrize() {
        return secondPrize;
    }

    public void setSecondPrize(int secondPrize) {
        this.secondPrize = secondPrize;
    }

    public int getThirdPrize() {
        return thirdPrize;
    }

    public void setThirdPrize(int thirdPrize) {
        this.thirdPrize = thirdPrize;
    }

    public String getLevelOfExperience() {
        return levelOfExperience;
    }

    public void setLevelOfExperience(String levelOfExperience) {
        this.levelOfExperience = levelOfExperience;
    }

    public int getLevelOfExperienceImageResourceIdByLevelOfExperience(){
        int resourceId=0;
        if(levelOfExperience.equals(levelOfExperience1))
            resourceId = R.drawable.level_baby;
        if(levelOfExperience.equals(levelOfExperience2))
            resourceId = R.drawable.level_kid;
        if(levelOfExperience.equals(levelOfExperience3))
            resourceId = R.drawable.level_pro;
        if(levelOfExperience.equals(levelOfExperience4))
            resourceId = R.drawable.level_star;
        if(levelOfExperience.equals(levelOfExperience5))
            resourceId = R.drawable.level_legend;


        return resourceId;

    }
}


