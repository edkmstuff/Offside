package com.offsidegame.offside.events;

import com.offsidegame.offside.models.GameInfo;

/**
 * Created by KFIR on 11/17/2016.
 */

public class PrivateGameGeneratedEvent {
    private String privateGameCode;

    public PrivateGameGeneratedEvent(String privateGameCode) {
        this.privateGameCode = privateGameCode;
    }


    public String getPrivateGameCode() {
        return privateGameCode;
    }

    public void setPrivateGameCode(String privateGameCode) {
        this.privateGameCode = privateGameCode;
    }
}
