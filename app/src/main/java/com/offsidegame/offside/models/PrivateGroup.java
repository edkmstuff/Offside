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
    @com.google.gson.annotations.SerializedName("PI")
    private PlayerInfo [] playersInfo;

    @com.google.gson.annotations.SerializedName("LP")
    private Date lastPlayed;

    public int getPlayersCount(){
        if(playersInfo == null)
            return 0;
        return playersInfo.length;

    }

    public int getActivePlayersCount(){
        if(playersInfo == null)
            return 0;
        int activePlayersCount=0;
        for(PlayerInfo player : playersInfo){
            if(player.getPrivateGameCode() !=null && player.getPrivateGameCode().length()>0 )
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

    public PlayerInfo[] getPlayersInfo() {
        return playersInfo;
    }

    public void setPlayersInfo(PlayerInfo[] playersInfo) {
        this.playersInfo = playersInfo;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }


}
