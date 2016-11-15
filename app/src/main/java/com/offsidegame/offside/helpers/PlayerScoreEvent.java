package com.offsidegame.offside.helpers;

import com.offsidegame.offside.models.PlayerScore;

public class PlayerScoreEvent {
    public final PlayerScore playerScore;

    public PlayerScoreEvent(PlayerScore playerScore) {
        this.playerScore = playerScore;
    }
}
