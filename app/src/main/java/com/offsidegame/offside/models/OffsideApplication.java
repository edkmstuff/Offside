package com.offsidegame.offside.models;

import android.app.Application;
import android.content.Context;

/**
 * Created by KFIR on 1/15/2017.
 */

public class OffsideApplication extends Application {
    private Context context;
    private static boolean isPlayerQuitGame = false;
    private static String messageTypeText = "TEXT";
    private static String messageTypeAskedQuestion = "ASKED_QUESTION";
    private static String messageTypeProcessedQuestion = "PROCESSED_QUESTION";
    private static String messageTypeClosedQuestion = "CLOSED_QUESTION";

    public static String getMessageTypeText() {
        return messageTypeText;
    }

    public static String getMessageTypeAskedQuestion() {
        return messageTypeAskedQuestion;
    }

    public static String getMessageTypeProcessedQuestion() {
        return messageTypeProcessedQuestion;
    }

    public static String getMessageTypeClosedQuestion() {
        return messageTypeClosedQuestion;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context mContext) {
        this.context = mContext;
    }


    public static boolean isPlayerQuitGame() {
        return isPlayerQuitGame;
    }

    public static void setIsPlayerQuitGame(boolean playerQuitGame) {
        isPlayerQuitGame = playerQuitGame;
    }
}