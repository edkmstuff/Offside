package com.offsidegame.offside.events;

/**
 * Created by KFIR on 11/20/2016.
 */

public class GameCreationEvent {
    private String gameCode;

    public GameCreationEvent(String gameCode) {
        this.gameCode = gameCode;
    }


    public String getGameCode() {
        return gameCode;
    }


}
