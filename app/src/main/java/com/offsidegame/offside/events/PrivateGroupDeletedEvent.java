package com.offsidegame.offside.events;

/**
 * Created by user on 10/16/2017.
 */

public class PrivateGroupDeletedEvent {
    private int numberOfDeletedGroups;
    public PrivateGroupDeletedEvent(int numberOfDeletedGroups) {
        this.numberOfDeletedGroups = numberOfDeletedGroups;
    }
}
