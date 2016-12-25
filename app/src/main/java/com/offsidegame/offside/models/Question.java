package com.offsidegame.offside.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by USER on 11/21/2016.
 */

public class Question implements Serializable {

    @com.google.gson.annotations.SerializedName("I")
    private String id;
    @com.google.gson.annotations.SerializedName("QT")
    private String questionText;
    @com.google.gson.annotations.SerializedName("GI")
    private String gameId;
    @com.google.gson.annotations.SerializedName("A")
    private Answer[] answers ;
    @com.google.gson.annotations.SerializedName("IA")
    private boolean isActive ;


    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public Answer[] getAnswers() {
        return answers;
    }

    public void setAnswers(Answer[] answers) {
        this.answers = answers;
    }

    public String getGameId() {
        return gameId;
    }

    public void setGameId(String gameId) {
        this.gameId = gameId;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }
}
