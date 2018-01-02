package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 12/27/2017.
 */

public class ScoreDetailedInfo {

    @com.google.gson.annotations.SerializedName("PAC")
    private ArrayList<PlayerActivity> playerActivities;

    @com.google.gson.annotations.SerializedName("TQ")
    private int totalQuestions;

    @com.google.gson.annotations.SerializedName("TAQ")
    private int totalAnsweredQuestions;

    @com.google.gson.annotations.SerializedName("TCA")
    private int totalCorrectAnswers;

    public ScoreDetailedInfo(ArrayList<PlayerActivity> playerActivities, int totalQuestions, int totalAnsweredQuestions, int totalCorrectAnswers) {
        this.playerActivities = playerActivities;
        this.totalQuestions = totalQuestions;
        this.totalAnsweredQuestions = totalAnsweredQuestions;
        this.totalCorrectAnswers = totalCorrectAnswers;

    }

    public ArrayList<PlayerActivity> getPlayerActivities() {

        if (playerActivities == null)
            playerActivities = new ArrayList();


        Collections.sort(playerActivities, new Comparator<PlayerActivity>() {
            @Override
            public int compare(PlayerActivity playerActivity, PlayerActivity other) {
                if (playerActivity.getQuestionStartTime().before(other.getQuestionStartTime()))
                    return 1;
                if (playerActivity.getQuestionStartTime().after(other.getQuestionStartTime()))
                    return -1;

                return 0;
            }
        });


        return playerActivities;
    }

    public void setPlayerActivities(ArrayList<PlayerActivity> playerActivities) {
        this.playerActivities = playerActivities;
    }

    public int getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(int totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public int getTotalAnsweredQuestions() {
        return totalAnsweredQuestions;
    }

    public void setTotalAnsweredQuestions(int totalAnsweredQuestions) {
        this.totalAnsweredQuestions = totalAnsweredQuestions;
    }

    public int getTotalCorrectAnswers() {
        return totalCorrectAnswers;
    }

    public void setTotalCorrectAnswers(int totalCorrectAnswers) {
        this.totalCorrectAnswers = totalCorrectAnswers;
    }

//    public double getTotalPotentialEarnings() {
//
//        double potentialEarnings = 0;
//        for (PlayerActivity playerActivity : playerActivities) {
//            Answer answer = playerActivity.getAnswer();
//            double pointsMultiplier = answer != null ? answer.getPointsMultiplier() : 0;
//
//            AnswerIdentifier answerIdentifier = playerActivity.getAnswerIdentifier();
//            int betSize = answerIdentifier != null ? answerIdentifier.getBetSize() : 0;
//
//            potentialEarnings =  potentialEarnings+(pointsMultiplier * betSize);
//        }
//
//        return potentialEarnings;
//
//    }


}
