package com.offsidegame.offside.models;

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



