package com.offsidegame.offside.models;

import java.util.Date;

/**
 * Created by KFIR on 12/7/2016.
 */
public class ChatMessage {
    @com.google.gson.annotations.SerializedName("MT")
    private String messageText;

    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("ST")
    private Date sentTime;

    @com.google.gson.annotations.SerializedName("II")
    private boolean isIncoming;


    public ChatMessage(String messageText, boolean isIncoming){
        this.messageText=messageText + " - " + isIncoming;
        this.imageUrl = "http://offside.somee.com/images/defaultImage.jpg";
        this.isIncoming = isIncoming;

    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }


    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }


    public boolean isIncoming() {
        return isIncoming;
    }

    public void setIncoming(boolean incoming) {
        isIncoming = incoming;
    }

    public Date getSentTime() {
        return sentTime;
    }

    public void setSentTime(Date sentTime) {
        this.sentTime = sentTime;
    }
}