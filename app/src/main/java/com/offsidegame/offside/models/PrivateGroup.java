package com.offsidegame.offside.models;

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
    private PrivateGroupPlayer [] privateGroupPlayers;

    @com.google.gson.annotations.SerializedName("LP")
    private Date lastPlayed;

    public int getPlayersCount(){
        if(privateGroupPlayers == null)
            return 0;
        return privateGroupPlayers.length;

    }

    public int getActivePlayersCount(){
        if(privateGroupPlayers == null)
            return 0;
        int activePlayersCount=0;
        for(PrivateGroupPlayer player : privateGroupPlayers){
            if(player.getActive())
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

    public PrivateGroupPlayer[] getPrivateGroupPlayers() {
        return this.privateGroupPlayers;
    }

    public void setPrivateGroupPlayers(PrivateGroupPlayer[] privateGroupPlayers) {
        this.privateGroupPlayers = privateGroupPlayers;
    }
}
