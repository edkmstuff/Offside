package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by user on 8/1/2017.
 */

public class PlayerGame {

    @com.google.gson.annotations.SerializedName("GN")
    private String groupName;

    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;

    @com.google.gson.annotations.SerializedName("GST")
    private Date gameStartTime;

    @com.google.gson.annotations.SerializedName("P")
    private int position;

    @com.google.gson.annotations.SerializedName("TP")
    private Integer totalPlayers;

    @com.google.gson.annotations.SerializedName("CAC")
    private Integer correctAnswersCount;

    @com.google.gson.annotations.SerializedName("TQA")
    private Integer totalQuestionsAsked;

    @com.google.gson.annotations.SerializedName("OC")
    private Integer offsideCoins;

    @com.google.gson.annotations.SerializedName("W")
    private ArrayList<Winner> winners;



    @com.google.gson.annotations.SerializedName("TAQ")
    private Integer totalAnsweredQuestions;

    @com.google.gson.annotations.SerializedName("DU")
    private Date dateUpdated;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Date getGameStartTime() {
        return gameStartTime;
    }

    public void setGameStartTime(Date gameStartTime) {
        this.gameStartTime = gameStartTime;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(Integer totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public Integer getCorrectAnswersCount() {
        return correctAnswersCount;
    }

    public void setCorrectAnswersCount(Integer correctAnswersCount) {
        this.correctAnswersCount = correctAnswersCount;
    }

    public Integer getTotalQuestionsAsked() {
        return totalQuestionsAsked;
    }

    public void setTotalQuestionsAsked(Integer totalQuestionsAsked) {
        this.totalQuestionsAsked = totalQuestionsAsked;
    }

    public Integer getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(Integer offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public ArrayList<Winner> getWinners() {
        return winners;
    }

    public void setWinners(ArrayList<Winner> winners) {
        this.winners = winners;
    }
}
