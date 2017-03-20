package com.offsidegame.offside.models;

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
}
