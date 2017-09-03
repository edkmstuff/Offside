package com.offsidegame.offside.events;

import com.offsidegame.offside.models.GameInfo;

/**
 * Created by KFIR on 11/17/2016.
 */

public class PrivateGameGeneratedEvent {
    private String privateGameId;

    public String getPrivateGameId() {
        return privateGameId;
    }

    public void setPrivateGameId(String privateGameId) {
        this.privateGameId = privateGameId;
    }

    public PrivateGameGeneratedEvent(String privateGameId) {

        this.privateGameId = privateGameId;
    }
}
