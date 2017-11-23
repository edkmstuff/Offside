package com.offsidegame.offside.models;

import org.acra.ACRA;

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
                if (privateGroup.getLastDateUpdated().before(other.getLastDateUpdated()))
                    return 1;
                if (privateGroup.getLastDateUpdated().after(other.getLastDateUpdated()))
                    return -1;

                return 0;
            }
        });

        return privateGroups;
    }

    public void setPrivateGroups(ArrayList<PrivateGroup> privateGroups) {
        this.privateGroups = privateGroups;
    }

    public PrivateGroup findPrivateGroupById(String privateGroupId) {
        for (PrivateGroup privateGroup:privateGroups) {
            if(privateGroup.getId().equals(privateGroupId))
                return privateGroup;
        }
        return null;


    }

    public void replace(PrivateGroup newPrivateGroup) {
        try {
            for (PrivateGroup privateGroup:privateGroups) {
                if(privateGroup.getId().equals(newPrivateGroup.getId())){
                    //privateGroups.remove(privateGroup);
                    //privateGroups.add(newPrivateGroup);
                    privateGroup.setPrivateGroupPlayers(newPrivateGroup.getPrivateGroupPlayers());
                    if(OffsideApplication.getSelectedPrivateGroup().getId() == newPrivateGroup.getId())
                        OffsideApplication.setSelectedPrivateGroup(privateGroup);

                }

            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }
}
