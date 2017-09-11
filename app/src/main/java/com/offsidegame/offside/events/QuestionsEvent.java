package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Question;

/**
 * Created by KFIR on 12/8/2016.
 */
public class QuestionsEvent {
    private Question[] questions;

    public QuestionsEvent(Question[] questions) {

        this.questions = questions;
    }

    public Question[] getQuestions() {
        return questions;
    }


}
