package com.offsidegame.offside.models;

import android.os.CountDownTimer;

import java.io.Serializable;

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
    private Answer[] answers;
    @com.google.gson.annotations.SerializedName("IA")
    private boolean isActive;
    @com.google.gson.annotations.SerializedName("TTAQ")
    private int timeToAnswerQuestion;
    @com.google.gson.annotations.SerializedName("TTQTP")
    private int timeToQuestionToPop;
    @com.google.gson.annotations.SerializedName("HPV")
    private boolean hasPointsValue;
    @com.google.gson.annotations.SerializedName("IWFA")
    private boolean isWaitingForAnswers;
    @com.google.gson.annotations.SerializedName("QTP")
    private String questionType;
    @com.google.gson.annotations.SerializedName("CAI")
    private String correctAnswerId;
    @com.google.gson.annotations.SerializedName("GP")
    private String gamePhase;





    public String getQuestionText() {
        if(questionText==null)
        return "";
        String[] questionTextArray = questionText.split("--S--");
        return questionTextArray[0];
    }

    public String getQuestionStatText() {
        if(questionText==null)
            return "";
        String[] questionTextArray = questionText.split("--S--");
        if (questionTextArray.length == 2) {
            return questionTextArray[1];

        }

        return "";

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

    public int getTimeToAnswerQuestion() {
        return timeToAnswerQuestion;
    }

    public void setTimeToAnswerQuestion(int timeToAnswerQuestion) {
        this.timeToAnswerQuestion = timeToAnswerQuestion;
    }

    public int getTimeToQuestionToPop() {
        return timeToQuestionToPop;
    }

    public void setTimeToQuestionToPop(int timeToQuestionToPop) {
        this.timeToQuestionToPop = timeToQuestionToPop;
    }


    public boolean isHasPointsValue() {
        return hasPointsValue;
    }

    public void setHasPointsValue(boolean hasPointsValue) {
        this.hasPointsValue = hasPointsValue;
    }

    public boolean isWaitingForAnswers() {
        return isWaitingForAnswers;
    }

    public void setWaitingForAnswers(boolean waitingForAnswers) {
        isWaitingForAnswers = waitingForAnswers;
    }

    public String getQuestionType() {
        return questionType;
    }

    public String getCorrectAnswerId() {
        return correctAnswerId;
    }

    public boolean isDebate() {
        return questionType.equals(OffsideApplication.getQuestionTypeDebate());

    }

    public String getGamePhase() {
        return gamePhase;
    }

    public void setGamePhase(String gamePhase) {
        this.gamePhase = gamePhase;
    }
}
