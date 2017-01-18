package com.offsidegame.offside.events;

/**
 * Created by USER on 11/22/2016.
 */

public class QuestionAnsweredEvent {

    private String gameId;
    private String questionId;
    private String answerId;
    private boolean isRandomAnswer;


    public QuestionAnsweredEvent(String gameId, String questionId, String answerId, boolean isRandomAnswer) {
        this.gameId = gameId;
        this.questionId = questionId;
        this.answerId = answerId;
        this.isRandomAnswer = isRandomAnswer;
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

    public boolean isRandomAnswer() {
        return isRandomAnswer;
    }

    public void setRandomAnswer(boolean randomAnswer) {
        isRandomAnswer = randomAnswer;
    }
}
