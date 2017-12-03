package com.offsidegame.offside.models;

import com.offsidegame.offside.R;

import java.util.Date;

/**
 * Created by user on 8/1/2017.
 */

public class Reward {


    @com.google.gson.annotations.SerializedName("PGN")
    private String groupName;

    @com.google.gson.annotations.SerializedName("P")
    private int position;

    @com.google.gson.annotations.SerializedName("TP")
    private int totalPlayers;

    @com.google.gson.annotations.SerializedName("RTN")
    private String rewardTypeName;

    @com.google.gson.annotations.SerializedName("RN")
    private Integer rewardNumber;

    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public String getRewardTypeName() {
        return rewardTypeName;
    }

    public void setRewardTypeName(String rewardTypeName) {
        this.rewardTypeName = rewardTypeName;
    }

    public Integer getRewardNumber() {
        return rewardNumber;
    }

    public void setRewardNumber(Integer rewardNumber) {
        this.rewardNumber = rewardNumber;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Date getGameStartDate() {
        return gameStartDate;
    }

    public void setGameStartDate(Date gameStartDate) {
        this.gameStartDate = gameStartDate;
    }

    @com.google.gson.annotations.SerializedName("GST")
    private Date gameStartDate;


    private static String rewardTypeGoldTrophy = "GOLD_TROPHY";
    private static String rewardTypeSilverTrophy = "SILVER_TROPHY";
    private static String rewardTypeBronzeTrophy = "BRONZE_TROPHY";
    //private static String rewardTypeTrophy = "TROPHY";
    private static String rewardTypeCoins = "COINS";


    public int getRewardImageResourceIdByRewardType(){
        int resourceId=0;
        if(rewardTypeName.equals(rewardTypeGoldTrophy))
            resourceId = R.drawable.trophy_gold_big;
        if(rewardTypeName.equals(rewardTypeSilverTrophy))
            resourceId = R.drawable.trophy_silver_big;
        if(rewardTypeName.equals(rewardTypeBronzeTrophy))
            resourceId = R.drawable.trophy_bronze_big;
//        if(rewardTypeName.equals(rewardTypeCoins))
//            resourceId = R.drawable.gold_coin;

        return resourceId;

    }
}


