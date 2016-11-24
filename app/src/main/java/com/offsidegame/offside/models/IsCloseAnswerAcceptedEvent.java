package com.offsidegame.offside.models;

/**
 * Created by USER on 11/24/2016.
 */

public class IsCloseAnswerAcceptedEvent {

    private Boolean isCloseAnswerAccepted;


    public IsCloseAnswerAcceptedEvent(Boolean isWaitingForAnswers) {
        this.isCloseAnswerAccepted = isWaitingForAnswers;
    }

    public Boolean getIsAnswerAccepted() {
        return isCloseAnswerAccepted;
    }

}
