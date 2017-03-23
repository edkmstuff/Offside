package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class AnswerIdentifier {


    @com.google.gson.annotations.SerializedName("AI")
    private String answerId;

    @com.google.gson.annotations.SerializedName("IRS")
    private boolean isRandomlySelected;

    @com.google.gson.annotations.SerializedName("BS")
    private int betSize;

    @com.google.gson.annotations.SerializedName("QIA")
    private boolean questionIsActive;

    public AnswerIdentifier(String answerId, boolean isRandomlySelected, int betSize, boolean questionIsActive) {
        this.answerId = answerId;
        this.isRandomlySelected = isRandomlySelected;
        this.betSize = betSize;
        this.questionIsActive = questionIsActive;

    }


    public String getAnswerId() {
        return answerId;
    }

    public void setAnswerId(String answerId) {
        this.answerId = answerId;
    }

    public boolean isRandomlySelected() {
        return isRandomlySelected;
    }

    public void setRandomlySelected(boolean randomlySelected) {
        isRandomlySelected = randomlySelected;
    }

    public int getBetSize() {
        return betSize;
    }

    public void setBetSize(int betSize) {
        this.betSize = betSize;
    }

    public boolean getQuestionIsActive() {
        return questionIsActive;
    }

    public void setQuestionIsActive(boolean questionIsActive) {
        this.questionIsActive = questionIsActive;
    }
}
