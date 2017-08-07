package com.offsidegame.offside.models;

import com.offsidegame.offside.R;

import java.util.Date;

/**
 * Created by user on 8/1/2017.
 */

public class Reward {
    private String rewardType;
    private String privateGroupName;
    private Position position;
    private String gameTitle;
    private Date gameStartDate;

    private static String rewardTypeGoldTrophy = "GOLD_TROPHY";
    private static String rewardTypeSilverTrophy = "SILVER_TROPHY";
    private static String rewardTypeBronzeTrophy = "BRONZE_TROPHY";
    //private static String rewardTypeTrophy = "TROPHY";
    private static String rewardTypeCoins = "COINS";

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
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

    public String getPrivateGroupName() {
        return privateGroupName;
    }

    public void setPrivateGroupName(String privateGroupName) {
        this.privateGroupName = privateGroupName;
    }

    public int getRewardImageResourceIdByRewardType(){
        int resourceId=0;
        if(rewardType.equals(rewardTypeGoldTrophy))
            resourceId = R.drawable.trophy_gold_big;
        if(rewardType.equals(rewardTypeSilverTrophy))
            resourceId = R.drawable.trophy_silver_big;
        if(rewardType.equals(rewardTypeBronzeTrophy))
            resourceId = R.drawable.trophy_bronze_big;
        if(rewardType.equals(rewardTypeCoins))
            resourceId = R.drawable.gold_coin;

        return resourceId;

    }
}


