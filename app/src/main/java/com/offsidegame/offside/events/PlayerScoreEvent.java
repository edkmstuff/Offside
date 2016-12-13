package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PlayerScore;

/**
 * Created by KFIR on 11/17/2016.
 */

public class PlayerScoreEvent {
    private PlayerScore playerScore;

    public PlayerScoreEvent(PlayerScore playerScore) {

        this.playerScore = playerScore;
    }


    public PlayerScore getPlayerScore() {

        return playerScore;
    }
}
