package com.offsidegame.offside.events;

import com.offsidegame.offside.models.AvailableGame;

/**
 * Created by KFIR on 11/20/2016.
 */

public class AvailableGameEvent {
    private AvailableGame availableGame;

    public AvailableGameEvent(AvailableGame availableGame){

        this.availableGame= availableGame;
    }


    public AvailableGame getAvailableGame() {
        return availableGame;
    }

    public void setAvailableGame(AvailableGame availableGame) {
        this.availableGame = availableGame;
    }
}
