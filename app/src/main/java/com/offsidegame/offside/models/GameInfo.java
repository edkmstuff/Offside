package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class GameInfo {


    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;

    @com.google.gson.annotations.SerializedName("TTAQ")
    private int timeToAnswerQuestion;

    @com.google.gson.annotations.SerializedName("TTGBTP")
    private int timeToGoBackToPlayerScore;

    @com.google.gson.annotations.SerializedName("TTQTP")
    private int timeToQuestionToPop;

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public int getTimeToAnswerQuestion() {
        return timeToAnswerQuestion;
    }

    public void setTimeToAnswerQuestion(int timeToAnswerQuestion) {
        this.timeToAnswerQuestion = timeToAnswerQuestion;
    }

    public int getTimeToGoBackToPlayerScore() {
        return timeToGoBackToPlayerScore;
    }

    public void setTimeToGoBackToPlayerScore(int timeToGoBackToPlayerScore) {
        this.timeToGoBackToPlayerScore = timeToGoBackToPlayerScore;
    }

    public int getTimeToQuestionToPop() {
        return timeToQuestionToPop;
    }

    public void setTimeToQuestionToPop(int timeToQuestionToPop) {
        this.timeToQuestionToPop = timeToQuestionToPop;
    }
}
