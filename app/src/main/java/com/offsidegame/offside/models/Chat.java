package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by KFIR on 12/7/2016.
 */


public class Chat {
    @com.google.gson.annotations.SerializedName("CM")
    private ArrayList<ChatMessage> chatMessages;

    @com.google.gson.annotations.SerializedName("P")
    private PlayerModel player;

    @com.google.gson.annotations.SerializedName("POS")
    private Position position;

    @com.google.gson.annotations.SerializedName("SB")
    private Scoreboard scoreboard;

    @com.google.gson.annotations.SerializedName("CQCM")
    private ChatMessage currentQuestionChatMessage;

    private HashMap<String,ChatMessage> chatMessagesDictionary;

    public Chat(Chat chat){
        chatMessages= chat.getChatMessages();
        player= chat.getPlayer();
        position = chat.getPosition();
        scoreboard = chat.getScoreboard();
        chatMessagesDictionary= new HashMap<>();
        for(ChatMessage cm : chat.getChatMessages() ){
            chatMessagesDictionary.put(cm.getId(),cm);
            cm.startCountdownTimer();
        }
        currentQuestionChatMessage = chat.getCurrentQuestionChatMessage();

    }



    public boolean addMessageIfNotAlreadyExists(ChatMessage chatMessage){

        if (chatMessages == null)
            return false;

        if(!chatMessagesDictionary.containsKey(chatMessage.getId())){
            chatMessagesDictionary.put(chatMessage.getId(),chatMessage);
            chatMessages.add(chatMessage);
            return true;
        }

        return false;


    }

    public PlayerModel getPlayer() {
        return player;
    }

    public void setPlayer(PlayerModel player) {
        this.player = player;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }


    public ArrayList<ChatMessage> getChatMessages() {
        return chatMessages;
    }

    public void setChatMessages(ArrayList<ChatMessage> chatMessages) {
        this.chatMessages = chatMessages;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setScoreboard(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }


    public ChatMessage getCurrentQuestionChatMessage() {
        return currentQuestionChatMessage;
    }

    public void setCurrentQuestionChatMessage(ChatMessage currentQuestionChatMessage) {
        this.currentQuestionChatMessage = currentQuestionChatMessage;
    }
}
