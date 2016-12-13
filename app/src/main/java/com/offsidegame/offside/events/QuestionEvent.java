package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Question;

/**
 * Created by USER on 11/21/2016.
 */
public class QuestionEvent {
    private Question question;
    private String questionState;


    public class QuestionStates {
        public static final String NEW_QUESTION = "NEW_QUESTION";
        public static final String PROCESSED_QUESTION = "PROCESSED_QUESTION";
        public static final String CLOSED_QUESTION = "CLOSED_QUESTION";
    }

    public QuestionEvent(Question question, String questionState) {
        this.question = question;
        this.questionState=questionState;
    }

    public Question getQuestion() {
        return question;
    }

    public String getQuestionState() {return questionState;}
}

