package com.offsidegame.offside.models;

/**
 * Created by user on 7/16/2017.
 */

public class PrivateGroupInfo {
    @com.google.gson.annotations.SerializedName("PGI")
    private PrivateGroup[] privateGroupsInfo;



    public PrivateGroup[] getPrivateGroupsInfo() {
        return privateGroupsInfo;
    }

    public void setPrivateGroupsInfo(PrivateGroup[] privateGroupsInfo) {
        this.privateGroupsInfo = privateGroupsInfo;
    }
}
