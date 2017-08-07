package com.offsidegame.offside.models;

import java.util.Date;

/**
 * Created by user on 8/1/2017.
 */

public class PlayerInGameRecord {

    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;

    @com.google.gson.annotations.SerializedName("PG")
    private PrivateGroup privateGroup;

    @com.google.gson.annotations.SerializedName("OC")
    private Integer offsideCoins;

    @com.google.gson.annotations.SerializedName("I")
    private Integer investment;

    @com.google.gson.annotations.SerializedName("P")
    private Position position;

    @com.google.gson.annotations.SerializedName("TP")
    private Integer totalPlayers;

    @com.google.gson.annotations.SerializedName("TQ")
    private Integer totalQuestions;

    @com.google.gson.annotations.SerializedName("TAQ")
    private Integer totalAnsweredQuestions;

    @com.google.gson.annotations.SerializedName("TQ")
    private Date dateUpdated;



    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }


    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(Integer totalPlayers) {
        this.totalPlayers = totalPlayers;
    }



    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }


    public Integer getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(Integer offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public Date getDateUpdated() {
        return dateUpdated;
    }

    public void setDateUpdated(Date dateUpdated) {
        this.dateUpdated = dateUpdated;
    }

    public Integer getInvestment() {
        return investment;
    }

    public void setInvestment(Integer investment) {
        this.investment = investment;
    }

    public PrivateGroup getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(PrivateGroup privateGroup) {
        this.privateGroup = privateGroup;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public Integer getTotalAnsweredQuestions() {
        return totalAnsweredQuestions;
    }

    public void setTotalAnsweredQuestions(Integer totalAnsweredQuestions) {
        this.totalAnsweredQuestions = totalAnsweredQuestions;
    }
}
