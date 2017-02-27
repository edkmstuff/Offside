package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Chat;

/**
 * Created by KFIR on 12/8/2016.
 */
public class ChatEvent {
    private Chat chat;
    public ChatEvent(Chat chat) {
        this.chat = chat;
    }

    public Chat getChat() {
        return chat;
    }


}
