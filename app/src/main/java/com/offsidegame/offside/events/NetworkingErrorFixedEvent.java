package com.offsidegame.offside.events;

/**
 * Created by KFIR on 9/26/2017.
 */

public class NetworkingErrorFixedEvent {
    private String error = "";

    public NetworkingErrorFixedEvent(String error) {
        this.error = error;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
