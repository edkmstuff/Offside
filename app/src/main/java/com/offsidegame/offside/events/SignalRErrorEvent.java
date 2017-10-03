package com.offsidegame.offside.events;

/**
 * Created by KFIR on 9/26/2017.
 */

public class SignalRErrorEvent {
    private String error = "";

    public SignalRErrorEvent(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
