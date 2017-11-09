package com.offsidegame.offside.events;

/**
 * Created by user on 9/13/2017.
 */

public class PlayerRewardedReceivedEvent {

    private int rewardValue;

    public PlayerRewardedReceivedEvent(int rewardValue) {
        this.rewardValue = rewardValue;
    }

    public int getRewardValue() {
        return rewardValue;
    }

    public void setRewardValue(int rewardValue) {
        this.rewardValue = rewardValue;
    }
}
