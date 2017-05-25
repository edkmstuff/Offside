package com.offsidegame.offside.events;

/**
 * Created by USER on 11/22/2016.
 */

public class QuestionAnsweredEvent {

    private String gameId;
    private String questionId;
    private String answerId;
    //private boolean isRandomAnswer;
    private boolean isSkipped;
    private int betSize;


    public QuestionAnsweredEvent(String gameId, String questionId, String answerId, boolean isSkipped, int betSize) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.isSkipped = isSkipped;
        this.betSize= betSize;
    }


    public String getGameId() {
        return gameId;
    }

    public String getQuestionId() {
        return questionId;
    }

    public String getAnswerId() {
        return answerId;
    }



    public int getBetSize() {
        return betSize;
    }

    public void setBetSize(int betSize) {
        this.betSize = betSize;
    }

    public boolean isSkipped() {
        return isSkipped;
    }

    public void setSkipped(boolean skipped) {
        isSkipped = skipped;
    }
}
