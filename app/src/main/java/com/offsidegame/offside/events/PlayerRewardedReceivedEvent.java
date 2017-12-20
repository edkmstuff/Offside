package com.offsidegame.offside.events;

import com.offsidegame.offside.models.RewardedPlayer;

/**
 * Created by user on 9/13/2017.
 */

public class PlayerRewardedReceivedEvent {
    private RewardedPlayer rewardedPlayer;

    public PlayerRewardedReceivedEvent(RewardedPlayer rewardedPlayer) {
        this.rewardedPlayer = rewardedPlayer;
    }

    public RewardedPlayer getRewardedPlayer() {
        return rewardedPlayer;
    }

    public void setRewardedPlayer(RewardedPlayer rewardedPlayer) {
        this.rewardedPlayer = rewardedPlayer;
    }
}
