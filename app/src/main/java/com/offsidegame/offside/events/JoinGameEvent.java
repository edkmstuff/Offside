package com.offsidegame.offside.events;

/**
 * Created by KFIR on 11/17/2016.
 */

public class JoinGameEvent {
    private String gameId;

    public JoinGameEvent(String gameId) {
        this.gameId = gameId;
    }


    public String getGameId() {
        return gameId;
    }
}
