package com.offsidegame.offside.models;

/**
 * Created by KFIR on 6/30/2017.
 */

public class PrivateGroupCreationInfo {

    @com.google.gson.annotations.SerializedName("GN")
    private String groupName;
    @com.google.gson.annotations.SerializedName("GT")
    private String  groupType;
    @com.google.gson.annotations.SerializedName("CI")
    private String  creatorId ;
    @com.google.gson.annotations.SerializedName("LC")
    private String  languageCode;
    @com.google.gson.annotations.SerializedName("LUB")
    private String  lastUpdatedBy;
    @com.google.gson.annotations.SerializedName("COI")
    private String  connectionId ;

    public PrivateGroupCreationInfo(String groupName, String groupType, String creatorId, String languageCode) {
        this.groupName = groupName;
        this.groupType = groupType;
        this.creatorId = creatorId;
        this.languageCode = languageCode;
        this.lastUpdatedBy = creatorId;
        this.connectionId = null;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
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

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }
}
