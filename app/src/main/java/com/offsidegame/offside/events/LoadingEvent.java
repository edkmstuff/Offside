package com.offsidegame.offside.events;

/**
 * Created by user on 11/16/2017.
 */

public class LoadingEvent {

    private boolean isLoading;

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public String getLoadingMessage() {
        return loadingMessage;
    }

    public void setLoadingMessage(String loadingMessage) {
        this.loadingMessage = loadingMessage;
    }

    public LoadingEvent(boolean isLoading, String loadingMessage) {

        this.isLoading = isLoading;
        this.loadingMessage = loadingMessage;
    }

    private String loadingMessage;




}
