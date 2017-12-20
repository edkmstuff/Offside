package com.offsidegame.offside.models;

/**
 * Created by user on 12/19/2017.
 */

public class RewardedPlayer {

    @com.google.gson.annotations.SerializedName("PA")
    private PlayerAssets playerAssets;

    @com.google.gson.annotations.SerializedName("RT")
    private String rewardType;

    @com.google.gson.annotations.SerializedName("RV")
    private int rewardValue;

    public RewardedPlayer(PlayerAssets playerAssets, String rewardType) {
        this.playerAssets = playerAssets;
        this.rewardType = rewardType;
    }

    public PlayerAssets getPlayerAssets() {
        return playerAssets;
    }

    public void setPlayerAssets(PlayerAssets playerAssets) {
        this.playerAssets = playerAssets;
    }

    public String getRewardType() {
        return rewardType;
    }

    public void setRewardType(String rewardType) {
        this.rewardType = rewardType;
    }

    public int getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(int rewardValue) {
        this.rewardValue = rewardValue;
    }
}
