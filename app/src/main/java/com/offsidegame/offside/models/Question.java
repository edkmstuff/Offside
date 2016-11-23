package com.offsidegame.offside.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by USER on 11/21/2016.
 */

public class Question implements Serializable {

    @com.google.gson.annotations.SerializedName("Id")
    private String id;
    @com.google.gson.annotations.SerializedName("QuestionText")
    private String questionText;
    @com.google.gson.annotations.SerializedName("GameId")
    private String gameId;
    @com.google.gson.annotations.SerializedName("Answers")
    private Answer[] answers ;

    public Question() {

    }

    public Question(String questionText,Answer[] answers, String gameId) {
        this.questionText = questionText;
        this.answers = answers;
        this.gameId = gameId;

    }

    public String getId() {
        return id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String getGameId() {
        return gameId;
    }

    public Answer[] getAnswers() {
        return answers;
    }
}