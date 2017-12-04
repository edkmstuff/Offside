package com.offsidegame.offside.models;

/**
 * Created by KFIR on 6/30/2017.
 */

public class PrivateGameInfo {

    @com.google.gson.annotations.SerializedName("GI")
    private String groupId;
    @com.google.gson.annotations.SerializedName("GAI")
    private String  gameId;
    @com.google.gson.annotations.SerializedName("PGI")
    private String  privateGameId ;

    public PrivateGameInfo(String groupId, String gameId, String privateGameId) {
        this.groupId = groupId;
        this.gameId = gameId;
        this.privateGameId = privateGameId;
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

    public String getPrivateGameId() {
        return privateGameId;
    }

    public void setPrivateGameId(String privateGameId) {
        this.privateGameId = privateGameId;
    }
}
