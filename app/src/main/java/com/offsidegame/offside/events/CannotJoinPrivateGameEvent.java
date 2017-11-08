package com.offsidegame.offside.events;

/**
 * Created by user on 11/8/2017.
 */

public class CannotJoinPrivateGameEvent {
    private Integer reason;

    public CannotJoinPrivateGameEvent(Integer reason){
        this.reason= reason;
    }

    public Integer getReason() {
        return reason;
    }

    public void setReason(Integer reason) {
        this.reason = reason;
    }
}
