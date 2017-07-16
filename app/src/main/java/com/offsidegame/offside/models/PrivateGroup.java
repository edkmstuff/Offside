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
    private PlayerInfo [] playerInfos;
    @com.google.gson.annotations.SerializedName("LP")
    private Date lastPlayed;

    public int getPlayersCount(){
        if(playerInfos == null)
            return 0;
        return playerInfos.length;

    }

    public int getActivePlayersCount(){
        if(playerInfos == null)
            return 0;
        int activePlayersCount=0;
        for(PlayerInfo player : playerInfos){
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

    public PlayerInfo[] getPlayerInfos() {
        return playerInfos;
    }

    public void setPlayerInfos(PlayerInfo[] playerInfos) {
        this.playerInfos = playerInfos;
    }

    public Date getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(Date lastPlayed) {
        this.lastPlayed = lastPlayed;
    }
}
