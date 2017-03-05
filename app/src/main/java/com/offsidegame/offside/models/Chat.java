package com.offsidegame.offside.models;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by KFIR on 12/7/2016.
 */


public class Chat {
    @com.google.gson.annotations.SerializedName("CM")
    private ChatMessage[] chatMessages;
    @com.google.gson.annotations.SerializedName("P")
    private Player player;


    public ChatMessage[] getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ChatMessage[] chatMessages) {
        this.chatMessages = chatMessages;
    }

    public void addMessage(ChatMessage chatMessage){
        if (chatMessages == null)
            return;

        List<ChatMessage> list = new ArrayList(Arrays.asList( chatMessages));
        list.add(chatMessage);
        chatMessages = list.toArray(chatMessages);

    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
