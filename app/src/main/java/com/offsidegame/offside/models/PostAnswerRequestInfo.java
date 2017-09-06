package com.offsidegame.offside.models;

/**
 * Created by USER on 11/22/2016.
 */

public class PostAnswerRequestInfo {

    @com.google.gson.annotations.SerializedName("PI")
    private String playerId;
    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;
    @com.google.gson.annotations.SerializedName("QI")
    private String questionId;
    @com.google.gson.annotations.SerializedName("AI")
    private String answerId;
    @com.google.gson.annotations.SerializedName("IS")
    private boolean isSkipped;
    @com.google.gson.annotations.SerializedName("BS")
    private int betSize;

    public PostAnswerRequestInfo(String playerId, String gameId, String questionId, String answerId, boolean isSkipped, int betSize) {
        this.playerId = playerId;
        this.gameId = gameId;

        this.questionId = questionId;
        this.answerId = answerId;
        this.isSkipped = isSkipped;
        this.betSize = betSize;
        this.isAnswerAccepted = false;
    }

    private boolean isAnswerAccepted;


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }


    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }

    public int getBetSize() {
        return betSize;
    }

    public void setBetSize(int betSize) {
        this.betSize = betSize;
    }

    public boolean isAnswerAccepted() {
        return isAnswerAccepted;
    }

    public void setAnswerAccepted(boolean answerAccepted) {
        isAnswerAccepted = answerAccepted;
    }
}



