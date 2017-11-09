package com.offsidegame.offside.events;

/**
 * Created by user on 9/10/2017.
 */

public class GroupInviteEvent {

    private String groupId;
    private String groupName;
    private String gameId;
    private String privateGamaId;
    private String inviterPlayerId;

    public GroupInviteEvent(String groupId, String groupName, String gameId,  String privateGamaId, String inviterPlayerId) {
        this.groupId = groupId;
        this.groupName = groupName;
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


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
