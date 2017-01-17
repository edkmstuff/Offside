package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/14/2016.
 */

public class PlayerScore {



    @com.google.gson.annotations.SerializedName("GT")
    private String gameTitle;



    @com.google.gson.annotations.SerializedName("S")
    private Integer score;

    @com.google.gson.annotations.SerializedName("P")
    private Integer position;

    @com.google.gson.annotations.SerializedName("TP")
    private Integer totalPlayers;



    @com.google.gson.annotations.SerializedName("TOQ")
    private Integer totalOpenQuestions;

    @com.google.gson.annotations.SerializedName("TQ")
    private Integer totalQuestions;

    @com.google.gson.annotations.SerializedName("CQT")
    private String currentQuestionText;

    @com.google.gson.annotations.SerializedName("CQAT")
    private String currentQuestionAnswerText;

    @com.google.gson.annotations.SerializedName("CQEIM")
    private int currentQuestionExpirationInMilliseconds;

    @com.google.gson.annotations.SerializedName("TLTCGEIM")
    private int timeLeftToCurrentGameEventInMilliseconds;


    @com.google.gson.annotations.SerializedName("CGE")
    private String currentGameEvent;


    public String getGameTitle() {
        return gameTitle;
    }

    public void setGameTitle(String gameTitle) {
        this.gameTitle = gameTitle;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

    public Integer getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(Integer totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public Integer getTotalOpenQuestions() {
        return totalOpenQuestions;
    }

    public void setTotalOpenQuestions(Integer totalOpenQuestions) {
        this.totalOpenQuestions = totalOpenQuestions;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getCurrentQuestionText() {
        return currentQuestionText;
    }

    public void setCurrentQuestionText(String currentQuestionText) {
        this.currentQuestionText = currentQuestionText;
    }

    public String getCurrentQuestionAnswerText() {
        return currentQuestionAnswerText;
    }

    public void setCurrentQuestionAnswerText(String currentQuestionAnswerText) {
        this.currentQuestionAnswerText = currentQuestionAnswerText;
    }


    public int getCurrentQuestionExpirationInMilliseconds() {
        return currentQuestionExpirationInMilliseconds;
    }

    public void setCurrentQuestionExpirationInMilliseconds(int currentQuestionExpirationInMilliseconds) {
        this.currentQuestionExpirationInMilliseconds = currentQuestionExpirationInMilliseconds;
    }

    public int getTimeLeftToCurrentGameEventInMilliseconds() {
        return timeLeftToCurrentGameEventInMilliseconds;
    }

    public void setTimeLeftToCurrentGameEventInMilliseconds(int timeLeftToCurrentGameEventInMilliseconds) {
        this.timeLeftToCurrentGameEventInMilliseconds = timeLeftToCurrentGameEventInMilliseconds;
    }

    public String getCurrentGameEvent() {
        return currentGameEvent;
    }

    public void setCurrentGameEvent(String currentGameEvent) {
        this.currentGameEvent = currentGameEvent;
    }
}



