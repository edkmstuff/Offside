package com.offsidegame.offside.models;

import android.os.CountDownTimer;

import java.util.Date;

/**
 * Created by KFIR on 12/7/2016.
 */
public class ChatMessage {

    @com.google.gson.annotations.SerializedName("I")
    private String id;

    @com.google.gson.annotations.SerializedName("MT")
    private String messageText;

    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("ST")
    private Date sentTime;

    @com.google.gson.annotations.SerializedName("II")
    private boolean isIncoming;

    @com.google.gson.annotations.SerializedName("MTY")
    private String messageType;

    @com.google.gson.annotations.SerializedName("TLTA")
    private int timeLeftToAnswer;

    @com.google.gson.annotations.SerializedName("SBUN")
    private String sentByUserName;



//Todo: remove when remove dummy
//    public ChatMessage(String messageText, boolean isIncoming){
//        this.messageText=messageText + " - " + isIncoming;
//        this.imageUrl = "http://offside.somee.com/images/defaultImage.jpg";
//        this.isIncoming = isIncoming;
//
//    }

    public String getImageUrl() {
        return imageUrl;
    }


    public String getMessageText() {
        return messageText;
    }


    public boolean isIncoming() {
        return isIncoming;
    }


    public Date getSentTime() {
        return sentTime;
    }

    public String getMessageType() {
        return messageType;
    }


    public int getTimeLeftToAnswer() {
        return timeLeftToAnswer;
    }



    public String getId() {
        return id;
    }

    public String getSentByUserName() {
        return sentByUserName;
    }

    public void setTimeLeftToAnswer(int timeLeftToAnswer) {
        this.timeLeftToAnswer = timeLeftToAnswer;
    }




    public void startCountdownTimer(){
        if(messageType.equals(OffsideApplication.getMessageTypeAskedQuestion()) || messageType.equals(OffsideApplication.getMessageTypeProcessedQuestion())) {
            new CountDownTimer(timeLeftToAnswer, 100) {
                @Override
                public void onTick(long millisUntilFinished) {
                    timeLeftToAnswer = (int) millisUntilFinished;

                }

                @Override
                public void onFinish() {
                    //user did not answer this question, we select random answer
                    timeLeftToAnswer= 0;
                }
            }.start();

        }
    }





}
