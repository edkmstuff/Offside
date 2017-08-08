package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

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
        if (privateGroups == null)
            privateGroups = new ArrayList<>();


        Collections.sort(privateGroups, new Comparator<PrivateGroup>() {
            @Override
            public int compare(PrivateGroup privateGroup, PrivateGroup other) {
                return other.orderDate().compareTo(privateGroup.orderDate());
            }
        });

        return privateGroups;
    }

    public void setPrivateGroups(ArrayList<PrivateGroup> privateGroups) {
        this.privateGroups = privateGroups;
    }
}
