package com.offsidegame.offside.events;

/**
 * Created by USER on 11/22/2016.
 */

public class IsAnswerAcceptedEvent {

    private Boolean isAnswerAccepted;


    public IsAnswerAcceptedEvent(Boolean isWaitingForAnswers) {
        this.isAnswerAccepted = isWaitingForAnswers;
    }

    public Boolean getIsAnswerAccepted() {
        return isAnswerAccepted;
    }
}



