package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/20/2016.
 */

public class ActiveGameEvent {
    private Boolean isGameActive;

    public ActiveGameEvent(Boolean isGameActive){
        this.isGameActive= isGameActive;
    }

    public Boolean getIsGameActive() {
        return isGameActive;
    }
}
