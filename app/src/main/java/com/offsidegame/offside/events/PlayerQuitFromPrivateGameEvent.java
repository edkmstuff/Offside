package com.offsidegame.offside.events;

/**
 * Created by user on 11/16/2017.
 */

public class PlayerQuitFromPrivateGameEvent {

    private boolean playerWasRemovedFromPrivateGame;

    public PlayerQuitFromPrivateGameEvent(boolean wasRemoved){
        playerWasRemovedFromPrivateGame = wasRemoved;

    }


    public boolean isPlayerWasRemovedFromPrivateGame() {
        return playerWasRemovedFromPrivateGame;
    }

    public void setPlayerWasRemovedFromPrivateGame(boolean playerWasRemovedFromPrivateGame) {
        this.playerWasRemovedFromPrivateGame = playerWasRemovedFromPrivateGame;
    }
}
