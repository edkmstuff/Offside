package com.offsidegame.offside.events;

import com.offsidegame.offside.models.AvailableGame;

/**
 * Created by KFIR on 11/20/2016.
 */

public class AvailableGamesEvent {
    private AvailableGame[] availableGames;

    public AvailableGamesEvent(AvailableGame[] availableGames){

        this.availableGames= availableGames;
    }


    public AvailableGame[] getAvailableGames() {
        return availableGames;
    }

    public void setAvailableGames(AvailableGame[] availableGames) {
        this.availableGames = availableGames;
    }
}
