package com.offsidegame.offside.models;

import java.io.Serializable;

/**
 * Created by KFIR on 11/21/2016.
 */

public class Answer implements Serializable {

    @com.google.gson.annotations.SerializedName("I")
    private String id;
    @com.google.gson.annotations.SerializedName("AT")
    private String answerText;
    @com.google.gson.annotations.SerializedName("PUA")
    private double percentUsersAnswered;
    @com.google.gson.annotations.SerializedName("S")
    private double score;
    @com.google.gson.annotations.SerializedName("IC")
    private boolean isCorrect;


    @com.google.gson.annotations.SerializedName("PM")
    private double pointsMultiplier;



    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public double getPercentUsersAnswered() {
        return percentUsersAnswered;
    }

    public void setPercentUsersAnswered(double percentUsersAnswered) {
        this.percentUsersAnswered = percentUsersAnswered;
    }



    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isCorrect() {
        return isCorrect;
    }

    public void setCorrect(boolean correct) {
        isCorrect = correct;
    }

    public double getPointsMultiplier() {
        return pointsMultiplier;
    }

    public void setPointsMultiplier(double pointsMultiplier) {
        this.pointsMultiplier = pointsMultiplier;
    }
}
