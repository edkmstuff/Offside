package com.offsidegame.offside.models;

/**
 * Created by KFIR on 6/30/2017.
 */

public class PrivateGameCreationInfo {

    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;
    @com.google.gson.annotations.SerializedName("GN")
    private String  groupName;
    @com.google.gson.annotations.SerializedName("CI")
    private String  creatorId ;
    @com.google.gson.annotations.SerializedName("LC")
    private String  languageCode;
    @com.google.gson.annotations.SerializedName("COI")
    private String  connectionId ;


    public PrivateGameCreationInfo(){}
    public PrivateGameCreationInfo(String gameId, String groupName, String creatorId, String languageCode){
        this.gameId = gameId;
        this.groupName = groupName;
        this.creatorId = creatorId;
        this.languageCode = languageCode;
        this.connectionId = null;

    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
