package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class GameInfo {

    @com.google.gson.annotations.SerializedName("GAI")
    private String gameId;

    @com.google.gson.annotations.SerializedName("PRGI")
    private String privateGameId;

    @com.google.gson.annotations.SerializedName("PGT")
    private String privateGameTitle;

    @com.google.gson.annotations.SerializedName("HT")
    private String homeTeam;

    @com.google.gson.annotations.SerializedName("AT")
    private String awayTeam;

    @com.google.gson.annotations.SerializedName("MBS")
    private int minBetSize;

    @com.google.gson.annotations.SerializedName("T")
    private int trophies;

    @com.google.gson.annotations.SerializedName("MARVWPG")
    private int maxAllowedRewardVideosWatchPerGame;

    @com.google.gson.annotations.SerializedName("P")
    private Player player;




    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getPrivateGameId() {
        return privateGameId;
    }

    public String getPrivateGameTitle() {
        return privateGameTitle;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getMinBetSize() {
        return minBetSize;
    }

    public int getTrophies() {
        return trophies;
    }

    public void setTrophies(int trophies) {
        this.trophies = trophies;
    }

    public int getMaxAllowedRewardVideosWatchPerGame() {
        return maxAllowedRewardVideosWatchPerGame;
    }

    public void setMaxAllowedRewardVideosWatchPerGame(int maxAllowedRewardVideosWatchPerGame) {
        this.maxAllowedRewardVideosWatchPerGame = maxAllowedRewardVideosWatchPerGame;
    }

    public void setPrivateGameId(String privateGameId) {
        this.privateGameId = privateGameId;
    }

    public void setPrivateGameTitle(String privateGameTitle) {
        this.privateGameTitle = privateGameTitle;
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public void setMinBetSize(int minBetSize) {
        this.minBetSize = minBetSize;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}

