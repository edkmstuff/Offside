package com.offsidegame.offside.events;

/**
 * Created by user on 12/4/2017.
 */

public class JoinGameWithCodeEvent {
    private int privateGameCode;

    public int getPrivateGameCode() {
        return privateGameCode;
    }

    public void setPrivateGameCode(int privateGameCode) {
        this.privateGameCode = privateGameCode;
    }

    public JoinGameWithCodeEvent(int privateGameCode) {

        this.privateGameCode = privateGameCode;
    }
}
