package com.offsidegame.offside.events;

import com.offsidegame.offside.models.ChatMessage;

/**
 * Created by KFIR on 12/8/2016.
 */
public class ChatMessageEvent {

    private ChatMessage chatMessage;
     public ChatMessageEvent(ChatMessage chatMessage){
         this.chatMessage = chatMessage;
     }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public void setChatMessage(ChatMessage chatMessage) {
        this.chatMessage = chatMessage;
    }
}
