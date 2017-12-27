package com.offsidegame.offside.models;


import java.util.Date;

/**
 * Created by user on 12/27/2017.
 */

public class PlayerActivity {

    @com.google.gson.annotations.SerializedName("QT")
    private String questionText;

    @com.google.gson.annotations.SerializedName("A")
    private Answer answer;

    @com.google.gson.annotations.SerializedName("AI")
    private AnswerIdentifier answerIdentifier;

    @com.google.gson.annotations.SerializedName("QST")
    private Date questionStartTime;

    public Date getQuestionStartTime() {
        return questionStartTime;
    }

    public void setQuestionStartTime(Date questionStartTime) {
        this.questionStartTime = questionStartTime;
    }


    public PlayerActivity(String questionText, Answer answer, AnswerIdentifier answerIdentifier) {
        this.questionText = questionText;
        this.answerIdentifier = answerIdentifier;
        this.answer = answer;
    }



    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public AnswerIdentifier getAnswerIdentifier() {
        return answerIdentifier;
    }

    public void setAnswerIdentifier(AnswerIdentifier answerIdentifier) {
        this.answerIdentifier = answerIdentifier;
    }

    public Answer getAnswer() {
        return answer;
    }

    public void setAnswer(Answer answer) {
        this.answer = answer;
    }


}
