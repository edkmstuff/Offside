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
    private Integer score;

    @com.google.gson.annotations.SerializedName("Position")
    private Integer position;

    @com.google.gson.annotations.SerializedName("TotalPlayers")
    private Integer totalPlayers;

    @com.google.gson.annotations.SerializedName("LeaderScore")
    private Integer leaderScore;

    @com.google.gson.annotations.SerializedName("TotalOpenQuestions")
    private Integer totalOpenQuestions;




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

    public Integer getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public Integer getTotalOpenQuestions() {
        return totalOpenQuestions;
    }

    public void setTotalOpenQuestions(int totalOpenQuestions) {
        this.totalOpenQuestions = totalOpenQuestions;
    }

    public Integer getLeaderScore() {
        return leaderScore;
    }

    public void setLeaderScore(int leaderScore) {
        this.leaderScore = leaderScore;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}



