package com.offsidegame.offside.events;

/**
 * Created by KFIR on 9/26/2017.
 */

public class NetworkingErrorFixedEvent {
    private boolean isError = false;

    public NetworkingErrorFixedEvent(boolean isError) {
        this.isError = isError;
    }


    public boolean isError() {
        return isError;
    }

    public void setError(boolean error) {
        isError = error;
    }
}
