package com.offsidegame.offside.models;

/**
 * Created by user on 7/16/2017.
 */

public class PrivateGroupInfo {
    @com.google.gson.annotations.SerializedName("PGI")
    private PrivateGroup[] privateGroupInfo;

    public PrivateGroup[] getPrivateGroupInfo() {
        return privateGroupInfo;
    }

    public void setPrivateGroupInfo(PrivateGroup[] privateGroupInfo) {
        this.privateGroupInfo = privateGroupInfo;
    }
}
