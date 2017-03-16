package com.offsidegame.offside.models;

import android.app.Application;
import android.content.Context;

import com.offsidegame.offside.R;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;

/**
 * Created by KFIR on 1/15/2017.
 */
@ReportsCrashes(
        //formUri = "http://192.168.1.140:8080/api/Offside/AcraCrashReport",
        //formUri = "http://10.0.0.17:8080/api/Offside/AcraCrashReport",
        formUri = "http://offside.somee.com/api/Offside/AcraCrashReport",
        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class OffsideApplication extends Application {

    private Context context;
    private static boolean isPlayerQuitGame = false;
    private static String messageTypeText = "TEXT";
    private static String messageTypeAskedQuestion = "ASKED_QUESTION";
    private static String messageTypeProcessedQuestion = "PROCESSED_QUESTION";
    private static String messageTypeClosedQuestion = "CLOSED_QUESTION";

    private static String profileImageFileName = "profileImage.jpg";
    private static String initialsProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/";
    //private static String initialsProfilePictureUrl = "http://offside.somee.com/api/Offside/GetProfilePicture/";
    private static String defaultProfilePictureUrl = "http://offside.somee.com/Images/defaultImage.jpg";

    private static int balance;
    private static int offsideCoins;


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

    public static String getProfileImageFileName() {
        return profileImageFileName;
    }

    public static String getInitialsProfilePictureUrl() {
        return initialsProfilePictureUrl;
    }

    public static String getDefaultProfilePictureUrl() {
        return defaultProfilePictureUrl;
    }

    public static int getOffsideCoins() {
        return offsideCoins;
    }

    public static void setOffsideCoins(int offsideCoins) {
        OffsideApplication.offsideCoins = offsideCoins;
    }

    public static int getBalance() {
        return balance;
    }

    public static void setBalance(int balance) {
        OffsideApplication.balance = balance;
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


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }



}