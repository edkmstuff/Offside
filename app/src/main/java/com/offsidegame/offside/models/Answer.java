package com.offsidegame.offside.models;

import java.io.Serializable;

/**
 * Created by KFIR on 11/21/2016.
 */

public class Answer implements Serializable {

    @com.google.gson.annotations.SerializedName("Id")
    private String id;
    @com.google.gson.annotations.SerializedName("AnswerNumber")
    private String answerNumber;
    @com.google.gson.annotations.SerializedName("AnswerText")
    private String answerText;
//    @com.google.gson.annotations.SerializedName("AnswerImage")
//    private String answerImage;
    @com.google.gson.annotations.SerializedName("PercentUsersAnswered")
    private double percentUsersAnswered;
    @com.google.gson.annotations.SerializedName("Score")
    private double score;
    @com.google.gson.annotations.SerializedName("IsCorrect")
    private boolean isCorrect;
    @com.google.gson.annotations.SerializedName("IsTheAnswerOfTheUser")
    private boolean isTheAnswerOfTheUser;

    public Answer(){}
    public Answer(String id, String answerText, double percentUsersAnswered, double score, boolean isCorrect, boolean isTheAnswerOfTheUser  ){
        this.id = id;
        this.answerText = answerText;
        this.percentUsersAnswered = percentUsersAnswered;
        this.score = score;
        this.isCorrect = isCorrect;
        this.isTheAnswerOfTheUser = isTheAnswerOfTheUser;
    }


    public String getId() {
        return id;
    }

    public String getAnswerText() {
        return answerText;
    }

//    public String getAnswerImage() {
//        return answerImage;
//    }

    public double getPercentUsersAnswered() {
        return percentUsersAnswered;
    }

    public double getScore() {
        return score;
    }

    public boolean getIsCorrect() {
        return isCorrect;
    }

    public boolean getIsTheAnswerOfTheUser() {
        return isTheAnswerOfTheUser;
    }
}
