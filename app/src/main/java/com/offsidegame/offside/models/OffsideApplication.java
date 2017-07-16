package com.offsidegame.offside.models;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.IBinder;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.offsidegame.offside.BuildConfig;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.ImageHelper;
import com.offsidegame.offside.helpers.SignalRService;

import org.acra.*;
import org.acra.annotation.*;
import org.acra.sender.HttpSender;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Map;

/**
 * Created by KFIR on 1/15/2017.
 */
@ReportsCrashes(
        //formUri = "http://192.168.1.140:8080/api/Offside/AcraCrashReport",
        //formUri = "http://10.0.0.17:8080/api/Offside/AcraCrashReport",
        //formUri = "http://offside.somee.com/api/Offside/AcraCrashReport",
        formUri = "http://offside.azurewebsites.net/api/Offside/AcraCrashReport",

        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class OffsideApplication extends Application {

    private static String version = BuildConfig.VERSION_NAME;
    private static boolean isPlayerQuitGame = false;
    private static String messageTypeText = "TEXT";
    private static String messageTypeAskedQuestion = "ASKED_QUESTION";
    private static String messageTypeProcessedQuestion = "PROCESSED_QUESTION";
    private static String messageTypeClosedQuestion = "CLOSED_QUESTION";
    private static String messageTypeGetCoins = "GET_COINS";
    private static String messageTypeWinner = "WINNER";
    private static String messageTypeSocialFeed = "SOCIAL_FEED";
    private static String questionTypeDebate = "Debate";
    private static String inGamePhaseName = "GamePlay";

    private static String questionTypePrediction = "Prediction";
    private static String questionTypeFun = "Fun";

    private static String profileImageFileName = "profileImage.jpg";

    //private static String initialsProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/";
    //private static String defaultProfilePictureUrl = "http://10.0.0.17:8080/Images/defaultImage.jpg";
    //private static String defaultProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/default";

    private static String initialsProfilePictureUrl = "http://192.168.1.140:8080/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://192.168.1.140:8080/Images/defaultImage.jpg";
    private static String defaultProfilePictureUrl = "http://192.168.1.140:8080/api/Offside/GetProfilePicture/default";

//    private static String initialsProfilePictureUrl = "http://offside.somee.com/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://offside.somee.com/Images/defaultImage.jpg";
//    private static String defaultProfilePictureUrl = "http://offside.somee.com/api/Offside/GetProfilePicture/default";
//

//    private static String initialsProfilePictureUrl = "http://offside.azurewebsites.net/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://offside.azurewebsites.net/Images/defaultImage.jpg";
//    private static String defaultProfilePictureUrl = "http://offside.azurewebsites.net/api/Offside/GetProfilePicture/default";

    private static String appLogoPictureUrl = "http://www.sidekickgame.com/img/logo.png";

    private static GameInfo gameInfo;

    private static boolean isChatActivityVisible = false;

    private static Scoreboard scoreboard;




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

    public static GameInfo getGameInfo() {
        return gameInfo;
    }

    public static void setGameInfo(GameInfo gameInfo) {
        OffsideApplication.gameInfo = gameInfo;
    }

    public static boolean isPlayerQuitGame() {
        return isPlayerQuitGame;
    }

    public static void setIsPlayerQuitGame(boolean playerQuitGame) {
        isPlayerQuitGame = playerQuitGame;
    }

    public static String getMessageTypeGetCoins() {
        return messageTypeGetCoins;
    }

    public static String getQuestionTypeDebate() {
        return questionTypeDebate;
    }

    public static boolean isChatActivityVisible() {
        return isChatActivityVisible;
    }

    public static void setIsChatActivityVisible(boolean isChatActivityVisible) {
        OffsideApplication.isChatActivityVisible = isChatActivityVisible;
    }

    public static String getMessageTypeWinner() {
        return messageTypeWinner;
    }

    public static Scoreboard getScoreboard() {
        return scoreboard;
    }

    public static void setScoreboard(Scoreboard scoreboard) {
        OffsideApplication.scoreboard = scoreboard;
    }

    public static String getInGamePhaseName() {
        return inGamePhaseName;
    }

    public static String getQuestionTypePrediction() {
        return questionTypePrediction;
    }

    public static String getQuestionTypeFun() {
        return questionTypeFun;
    }

    public static String getMessageTypeSocialFeed() {
        return messageTypeSocialFeed;
    }

    public static String getAppLogoPictureUrl() {
        return appLogoPictureUrl;
    }


    public void onCreate() {

        try {
            super.onCreate();
            // Setup handler for uncaught exceptions.
            Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
                @Override
                public void uncaughtException(Thread thread, Throwable e) {
                    ACRA.getErrorReporter().handleSilentException(e);
                }
            });

            //signal r
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), SignalRService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
            EventBus.getDefault().register(getApplicationContext());

        } catch (Exception ex) {

            ACRA.getErrorReporter().handleSilentException(ex);
        }


    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }


    public static Map<String, AnswerIdentifier> playerAnswers;


    //signal r
    public static SignalRService signalRService;
    public static boolean isBoundToSignalRService = false;
    public final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance

            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
            EventBus.getDefault().post(new SignalRServiceBoundEvent(getApplicationContext()));

        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }


    };

    @Subscribe
    public void onReceiveScoreboard(ScoreboardEvent scoreboardEvent) {
        try {

            Scoreboard scoreboard = scoreboardEvent.getScoreboard();

            if (scoreboard == null)
                return;
            Score[]  scores = scoreboard.getScores();

            if (scores == null || scores.length == 0)
                return;

            //check if scoreboard was changed
            Scoreboard currentScoreboard = OffsideApplication.getScoreboard();
            boolean isScoreboardsEquals = false;
            if (!scoreboard.isForceUpdate()&& currentScoreboard != null && currentScoreboard.getScores() != null && currentScoreboard.getScores().length == scores.length) {
                Score [] currentScores = currentScoreboard.getScores();
                for(int i=0; i<currentScores.length;i++){
                    boolean isEqualScore = currentScores[i].getImageUrl().equals(scores[i].getImageUrl()) &&
                            currentScores[i].getPosition() ==scores[i].getPosition() &&
                            currentScores[i].getOffsideCoins()==scores[i].getOffsideCoins();

                    if(!isEqualScore)
                        break;
                    if(i==currentScores.length-1)
                        isScoreboardsEquals = true;
                }

            }
            if(isScoreboardsEquals)
                return;

            OffsideApplication.setScoreboard(scoreboard);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(Player updatedPlayer) {
        try {
            if (updatedPlayer == null)
                return;

            playerAnswers = updatedPlayer.getPlayerAnswers();
            gameInfo.setPlayer(updatedPlayer);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public static String getVersion() {
        return version;
    }


}