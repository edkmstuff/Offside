package com.offsidegame.offside.models;

/**
 * Created by user on 7/16/2017.
 */

public class PrivateGroupsInfo {
    @com.google.gson.annotations.SerializedName("PG")
    private PrivateGroup[] privateGroups;

    @com.google.gson.annotations.SerializedName("PA")
    private PlayerAssets playerAssets;


    public PrivateGroup[] getPrivateGroups() {
        return privateGroups;
    }

    public void setPrivateGroups(PrivateGroup[] privateGroups) {
        this.privateGroups = privateGroups;
    }

    public PlayerAssets getPlayerAssets() {
        return playerAssets;
    }

    public void setPlayerAssets(PlayerAssets playerAssets) {
        this.playerAssets = playerAssets;
    }
}
