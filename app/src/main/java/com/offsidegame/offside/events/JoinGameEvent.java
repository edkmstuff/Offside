package com.offsidegame.offside.events;

import com.offsidegame.offside.models.GameInfo;

/**
 * Created by KFIR on 11/17/2016.
 */

public class JoinGameEvent {
    private GameInfo gameInfo;

    public JoinGameEvent(GameInfo gameInfo) {
        this.gameInfo = gameInfo;
    }


    public GameInfo getGameInfo() {
        return gameInfo;
    }
}
