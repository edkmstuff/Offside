package com.offsidegame.offside.events;

/**
 * Created by user on 10/16/2017.
 */

public class PlayerJoinPrivateGroupEvent {
    private int numberOfPlayersAdded;
    public PlayerJoinPrivateGroupEvent(int numberOfPlayerAdded) {
        this.numberOfPlayersAdded = numberOfPlayerAdded;
    }
}
