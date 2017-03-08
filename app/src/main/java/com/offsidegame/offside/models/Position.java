package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Position {

    @com.google.gson.annotations.SerializedName("GP")
    private int generalPosition ;

    @com.google.gson.annotations.SerializedName("GTP")
    private int generalTotalPlayers ;

    @com.google.gson.annotations.SerializedName( "PGP")
    private int privateGamePosition ;

    @com.google.gson.annotations.SerializedName( "PGTP")
    private int privateGameTotalPlayers ;


    public int getGeneralPosition() {
        return generalPosition;
    }

    public void setGeneralPosition(int generalPosition) {
        this.generalPosition = generalPosition;
    }

    public int getGeneralTotalPlayers() {
        return generalTotalPlayers;
    }

    public void setGeneralTotalPlayers(int generalTotalPlayers) {
        this.generalTotalPlayers = generalTotalPlayers;
    }

    public int getPrivateGamePosition() {
        return privateGamePosition;
    }

    public void setPrivateGamePosition(int privateGamePosition) {
        this.privateGamePosition = privateGamePosition;
    }

    public int getPrivateGameTotalPlayers() {
        return privateGameTotalPlayers;
    }

    public void setPrivateGameTotalPlayers(int privateGameTotalPlayers) {
        this.privateGameTotalPlayers = privateGameTotalPlayers;
    }
}
