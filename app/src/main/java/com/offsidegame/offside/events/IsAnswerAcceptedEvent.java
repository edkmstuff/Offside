package com.offsidegame.offside.events;

/**
 * Created by USER on 11/22/2016.
 */

public class IsAnswerAcceptedEvent {

    private Boolean isAnswerAccepted;


    private String gameId, playerId, questionId, answerId;
    //private boolean isRandomlySelected;
    private boolean isSkipped;
    private int betSize;


    public IsAnswerAcceptedEvent(Boolean isWaitingForAnswers, String gameId, String playerId, String questionId, String answerId, boolean isSkipped, int betSize) {
        this.isAnswerAccepted = isWaitingForAnswers;
        this.gameId = gameId;
        this.playerId = playerId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.isSkipped = isSkipped;
        this.betSize = betSize;

    }

    public Boolean getIsAnswerAccepted() {
        return isAnswerAccepted;
    }

    public String getGameId() {
        return gameId;
    }


    public String getPlayerId() {
        return playerId;
    }


    public String getQuestionId() {
        return questionId;
    }

    public String getAnswerId() {
        return answerId;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public int getBetSize() {
        return betSize;
    }
}



