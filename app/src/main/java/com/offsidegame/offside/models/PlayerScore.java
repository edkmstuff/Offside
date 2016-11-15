package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/14/2016.
 */

public class PlayerScore {

    @com.google.gson.annotations.SerializedName("GameId")
    private String gameId;

    @com.google.gson.annotations.SerializedName("GameTitle")
    private String gameTitle;

    @com.google.gson.annotations.SerializedName("UserName")
    private String userName;

    @com.google.gson.annotations.SerializedName("Score")
    private int score;

    @com.google.gson.annotations.SerializedName("Position")
    private int position;

    @com.google.gson.annotations.SerializedName("TotalPlayers")
    private int totalPlayers;

    @com.google.gson.annotations.SerializedName("TotalOpenQuestions")
    private int totalOpenQuestions;

    @com.google.gson.annotations.SerializedName("LeaderScore")
    private int leaderScore;


    public PlayerScore() {

    }


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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getTotalOpenQuestions() {
        return totalOpenQuestions;
    }

    public void setTotalOpenQuestions(int totalOpenQuestions) {
        this.totalOpenQuestions = totalOpenQuestions;
    }

    public int getLeaderScore() {
        return leaderScore;
    }

    public void setLeaderScore(int leaderScore) {
        this.leaderScore = leaderScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}



