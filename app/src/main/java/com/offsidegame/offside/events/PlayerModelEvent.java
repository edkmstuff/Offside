package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PlayerModel;

/**
 * Created by user on 10/3/2017.
 */

public class PlayerModelEvent {
    private PlayerModel playerModel;

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }

    public PlayerModelEvent(PlayerModel playerModel) {

        this.playerModel = playerModel;
    }
}
