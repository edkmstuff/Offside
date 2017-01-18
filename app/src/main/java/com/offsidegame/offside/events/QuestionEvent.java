package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Question;

/**
 * Created by USER on 11/21/2016.
 */
public class QuestionEvent {
    private Question question;
    private Question[] batchedQuestions;
    private String questionState;
    private boolean isBatch = false;




    public class QuestionStates {
        public static final String NEW_QUESTION = "NEW_QUESTION";
        public static final String PROCESSED_QUESTION = "PROCESSED_QUESTION";
        public static final String CLOSED_QUESTION = "CLOSED_QUESTION";
    }

    public QuestionEvent(Question[] batchedQuestions, String questionState) {
        this.batchedQuestions = batchedQuestions;
        this.question = null;
        this.questionState = questionState;
        isBatch = true;
    }

    public QuestionEvent(Question question, String questionState) {
        this.question = question;
        this.batchedQuestions = null;
        this.questionState = questionState;
        isBatch = false;
    }

    public Question getQuestion() {
        return question;
    }

    public String getQuestionState() {
        return questionState;
    }

    public Question[] getBatchedQuestions() {
        return batchedQuestions;
    }

    public void setBatchedQuestions(Question[] batchedQuestions) {
        this.batchedQuestions = batchedQuestions;
    }

    public boolean isBatch() {
        return isBatch;
    }

    public void setBatch(boolean batch) {
        isBatch = batch;
    }
}

