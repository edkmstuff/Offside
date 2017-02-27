package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Chat {
    @com.google.gson.annotations.SerializedName("CM")
    private ChatMessage[] chatMessages;


    public ChatMessage[] getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ChatMessage[] chatMessages) {
        this.chatMessages = chatMessages;
    }
}
