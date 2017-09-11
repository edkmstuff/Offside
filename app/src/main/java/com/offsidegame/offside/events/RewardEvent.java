package com.offsidegame.offside.events;

/**
 * Created by KFIR on 11/17/2016.
 */

public class RewardEvent {

    private int rewardAmount;


    public RewardEvent (int rewardAmount){
        this.rewardAmount= rewardAmount;
    }

    public int getRewardAmount() {
        return rewardAmount;
    }

    public void setRewardAmount(int rewardAmount) {
        this.rewardAmount = rewardAmount;
    }
}
