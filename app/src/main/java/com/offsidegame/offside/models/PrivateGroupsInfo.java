package com.offsidegame.offside.models;

import java.util.ArrayList;

/**
 * Created by user on 7/16/2017.
 */

public class PrivateGroupsInfo {
    @com.google.gson.annotations.SerializedName("PG")
    private ArrayList<PrivateGroup> privateGroups;

    @com.google.gson.annotations.SerializedName("PA")
    private PlayerAssets playerAssets;




    public PlayerAssets getPlayerAssets() {
        return playerAssets;
    }

    public void setPlayerAssets(PlayerAssets playerAssets) {
        this.playerAssets = playerAssets;
    }

    public ArrayList<PrivateGroup> getPrivateGroups() {
        return privateGroups;
    }

    public void setPrivateGroups(ArrayList<PrivateGroup> privateGroups) {
        this.privateGroups = privateGroups;
    }
}
