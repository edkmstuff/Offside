package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PlayerModel;

/**
 * Created by user on 12/11/2017.
 */

public class PlayerSettingsChangedEvent {
    private PlayerModel playerModel;

    public PlayerSettingsChangedEvent(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }

    public PlayerModel getPlayerModel() {
        return playerModel;
    }

    public void setPlayerModel(PlayerModel playerModel) {
        this.playerModel = playerModel;
    }
}
