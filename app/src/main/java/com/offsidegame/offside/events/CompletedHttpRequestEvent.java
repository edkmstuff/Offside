package com.offsidegame.offside.events;

/**
 * Created by user on 10/19/2017.
 */

public class CompletedHttpRequestEvent {
    private boolean isUrlValid;

    public CompletedHttpRequestEvent(boolean isUrlValid) {
        this.isUrlValid = isUrlValid;
    }

    public boolean isUrlValid() {
        return isUrlValid;
    }

    public void setUrlValid(boolean urlValid) {
        isUrlValid = urlValid;
    }
}
