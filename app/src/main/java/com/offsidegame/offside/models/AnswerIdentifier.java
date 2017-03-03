package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class AnswerIdentifier {


    @com.google.gson.annotations.SerializedName("AI")
    private String answerId;

    @com.google.gson.annotations.SerializedName("IRS")
    private boolean isRandomlySelected;

    public AnswerIdentifier(String answerId, boolean isRandomlySelected){
        this.answerId=answerId;
        this.isRandomlySelected=isRandomlySelected;


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
}
