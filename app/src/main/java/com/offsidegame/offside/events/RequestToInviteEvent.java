package com.offsidegame.offside.events;

/**
 * Created by user on 1/28/2018.
 */

public class RequestToInviteEvent {
    private String gameId;
    private String privateGameId;
    private String groupId;

    public RequestToInviteEvent(String gameId, String privateGameId, String groupId) {
        this.gameId = gameId;
        this.privateGameId = privateGameId;
        this.groupId = groupId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPrivateGameId() {
        return privateGameId;
    }

    public void setPrivateGameId(String privateGameId) {
        this.privateGameId = privateGameId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
