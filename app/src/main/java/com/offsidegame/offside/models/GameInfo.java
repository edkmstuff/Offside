package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class GameInfo {

    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;

    @com.google.gson.annotations.SerializedName("PGC")
    private String privateGameCode;

    @com.google.gson.annotations.SerializedName("PGT")
    private String privateGameTitle;

    @com.google.gson.annotations.SerializedName("HT")
    private String homeTeam;

    @com.google.gson.annotations.SerializedName("AT")
    private String awayTeam;

    @com.google.gson.annotations.SerializedName("OC")
    private int offsideCoins;

    @com.google.gson.annotations.SerializedName("B")
    private int balance;

    @com.google.gson.annotations.SerializedName("TP")
    private int totalPlayers;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPrivateGameCode() {
        return privateGameCode;
    }

    public void setPrivateGameCode(String privateGameCode) {
        this.privateGameCode = privateGameCode;
    }

    public String getPrivateGameTitle() {
        return privateGameTitle;
    }

    public void setPrivateGameTitle(String privateGameTitle) {
        this.privateGameTitle = privateGameTitle;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public int getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(int offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }
}
