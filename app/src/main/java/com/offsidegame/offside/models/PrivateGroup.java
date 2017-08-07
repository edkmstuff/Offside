package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 7/12/2017.
 */

public class PrivateGroup {

    @com.google.gson.annotations.SerializedName("I")
    private String id;

    @com.google.gson.annotations.SerializedName("N")
    private String name;

    @com.google.gson.annotations.SerializedName("PGP")
    private ArrayList<PrivateGroupPlayer> privateGroupPlayers;

    @com.google.gson.annotations.SerializedName("GT")
    private String groupType;

    @com.google.gson.annotations.SerializedName("LU")
    private Date lastUpdated;

    @com.google.gson.annotations.SerializedName("LUB")
    private String lastUpdatedBy;


    @com.google.gson.annotations.SerializedName("LP")
    private Date lastPlayed;



    public int getPlayersCount(){
        if(privateGroupPlayers == null)
            return 0;
        return privateGroupPlayers.size();

    }

    public int getActivePlayersCount(){
        if(privateGroupPlayers == null)
            return 0;
        int activePlayersCount=0;
        for(PrivateGroupPlayer player : privateGroupPlayers){
            if(player.getActive().booleanValue())
                activePlayersCount++;
        }
        return activePlayersCount;

    }

    public boolean isActive(){
        return getActivePlayersCount()>0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }



    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }

    public ArrayList<PrivateGroupPlayer> getPrivateGroupPlayers() {
        return privateGroupPlayers;
    }

    public void setPrivateGroupPlayers(ArrayList<PrivateGroupPlayer> privateGroupPlayers) {
        this.privateGroupPlayers = privateGroupPlayers;
    }

    public Date getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(Date lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Date orderDate() {
        if (lastPlayed == null && lastUpdated == null)
            return new Date(0);
        else if (lastPlayed == null)
            return lastUpdated;
        else if (lastUpdated == null)
            return lastPlayed;
        else
            return lastPlayed.after(lastUpdated) ? lastPlayed : lastUpdated;
    }


}
