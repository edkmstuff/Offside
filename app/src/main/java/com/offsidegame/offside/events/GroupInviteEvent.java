package com.offsidegame.offside.events;

/**
 * Created by user on 9/10/2017.
 */

public class GroupInviteEvent {

    private String groupId;
    private String gameId;
    private String privateGamaId;
    private String inviterPlayerId;

    public GroupInviteEvent(String groupId, String gameId, String privateGamaId, String inviterPlayerId) {
        this.groupId = groupId;
        this.gameId = gameId;
        this.privateGamaId = privateGamaId;
        this.inviterPlayerId = inviterPlayerId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPrivateGamaId() {
        return privateGamaId;
    }

    public void setPrivateGamaId(String privateGamaId) {
        this.privateGamaId = privateGamaId;
    }

    public String getInviterPlayerId() {
        return inviterPlayerId;
    }

    public void setInviterPlayerId(String inviterPlayerId) {
        this.inviterPlayerId = inviterPlayerId;
    }



}
