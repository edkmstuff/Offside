package com.offsidegame.offside.models;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by KFIR on 11/17/2016.
 */

public class AvailableGame {


    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;

    //// TODO: 7/23/2017 get rid of this when it is not needed
    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;

    @com.google.gson.annotations.SerializedName("ST")
    private Date startTime;

    @com.google.gson.annotations.SerializedName("HT")
    private String homeTeam;

    @com.google.gson.annotations.SerializedName("HTLU")
    private String homeTeamLogoUrl;

    @com.google.gson.annotations.SerializedName("AT")
    private String awayTeam;

    @com.google.gson.annotations.SerializedName("ATLU")
    private String awayTeamLogoUrl;


    @com.google.gson.annotations.SerializedName("LN")
    private String leagueName;

    @com.google.gson.annotations.SerializedName("IA")
    private boolean isActive;

    @com.google.gson.annotations.SerializedName("PGP")
    private PrivateGroupPlayer [] privateGroupPlayers;

    @com.google.gson.annotations.SerializedName("PGI")
    private String privateGameId;

    @com.google.gson.annotations.SerializedName("GRI")
    private String groupId;


    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getStartTimeString() {
        if (startTime == null)
            return "";

        return new SimpleDateFormat("HH:mm").format(startTime);
    }

    public String getStartDateString() {
        if (startTime == null)
            return "";

        return new SimpleDateFormat("dd/MM/yy").format(startTime);
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public String getHomeTeam() {
        return homeTeam.toUpperCase();
    }

    public void setHomeTeam(String homeTeam) {
        this.homeTeam = homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam.toUpperCase();
    }

    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public PrivateGroupPlayer[] getPrivateGroupPlayers() {
        return privateGroupPlayers;
    }

    public void setPrivateGroupPlayers(PrivateGroupPlayer[] privateGroupPlayers) {
        this.privateGroupPlayers = privateGroupPlayers;
    }

//    public String getPrivateGameId() {
//        return privateGameCode;
//    }
//
//    public void setPrivateGameId(String privateGameCode) {
//        this.privateGameCode = privateGameCode;
//    }

    public String getHomeTeamLogoUrl() {
        return homeTeamLogoUrl;
    }

    public void setHomeTeamLogoUrl(String homeTeamLogoUrl) {
        this.homeTeamLogoUrl = homeTeamLogoUrl;
    }

    public String getAwayTeamLogoUrl() {
        return awayTeamLogoUrl;
    }

    public void setAwayTeamLogoUrl(String awayTeamLogoUrl) {
        this.awayTeamLogoUrl = awayTeamLogoUrl;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getPrivateGameId() {
        return privateGameId;
    }

    public void setPrivateGameId(String privateGameId) {
        this.privateGameId = privateGameId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }
}
