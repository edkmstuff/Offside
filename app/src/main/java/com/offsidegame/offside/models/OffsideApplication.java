package com.offsidegame.offside.models;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;

import com.offsidegame.offside.BuildConfig;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.helpers.FontsOverride;
import com.offsidegame.offside.helpers.SignalRService;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by KFIR on 1/15/2017.
 */
@ReportsCrashes(
//         formUri = "http://192.168.1.140:8080/api/Offside/AcraCrashReport",
//         formUri = "http://10.0.2.2:8080/api/Offside/AcraCrashReport",
//         formUri = "http://10.0.0.17:8080/api/Offside/AcraCrashReport",
//         formUri = "http://10.0.0.41:8080/api/Offside/AcraCrashReport",

/****************************PRODUCTION**************************/
        formUri = "http://offside.azurewebsites.net/api/Offside/AcraCrashReport",

        httpMethod = HttpSender.Method.POST,
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text
)
public class OffsideApplication extends Application {

    private static Context context;
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

    /****************************DEVELOPMENT- LOCAL**************************/

//    private static String initialsProfilePictureUrl = "http://10.0.2.2:8080/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://10.0.2.2:8080/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
//    private static String defaultPictureUrlHazavitFeed = "http://10.0.2.2:8080/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";

    private static String initialsProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/";
    private static String defaultProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
    private static String defaultPictureUrlHazavitFeed = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";

//    private static String initialsProfilePictureUrl = "http://10.0.0.41:8080/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://10.0.0.41:8080/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
//    private static String defaultPictureUrlHazavitFeed = "http://10.0.0.41:8080/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";


//   private static String initialsProfilePictureUrl = "http://192.168.1.140:8080/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://192.168.1.140:8080/api/Offside/GetProfilePicture/default";
//    private static String defaultPictureUrlHazavitFeed = "http://192.168.1.140:8080/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";

/****************************TESTING**************************/
//    private static String initialsProfilePictureUrl = "http://offside.somee.com/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://offside.somee.com/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
//    private static String defaultPictureUrlHazavitFeed = "http://offside.somee.com/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";

    /****************************PRODUCTION**************************/
//    private static String initialsProfilePictureUrl = "http://offside.azurewebsites.net/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://offside.azurewebsites.net/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
//    private static String defaultPictureUrlHazavitFeed = "http://offside.azurewebsites.net/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";

    private static String appLogoPictureUrl = "http://www.sidekickgame.com/img/logo.png";

    private static GameInfo gameInfo;

    private static boolean isLobbyActivityVisible = false;

    private static Scoreboard scoreboard;

    private static PrivateGroupsInfo privateGroupsInfo;
    private static AvailableGame[] availableGames;

    private static PrivateGroup selectedPrivateGroup;

    private static AvailableGame selectedAvailableGame;
    private static String selectedPrivateGameId;

    private static UserProfileInfo userProfileInfo;

    private static PlayerAssets playerAssets;

    private static HashMap<String, LeagueRecord[]> leaguesRecords;

    private static boolean isBackFromNewsFeed = false;


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

    public static String getDefaultPictureUrlHazavitFeed() {
        return defaultPictureUrlHazavitFeed;
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
        return isLobbyActivityVisible;
    }

    public static void setIsLobbyActivityVisible(boolean isLobbyActivityVisible) {
        OffsideApplication.isLobbyActivityVisible = isLobbyActivityVisible;
    }

    public static String getMessageTypeWinner() {
        return messageTypeWinner;
    }

    public static Scoreboard getScoreboard() {
        return OffsideApplication.scoreboard;
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


    public static PrivateGroupsInfo getPrivateGroupsInfo() {
        return privateGroupsInfo;
    }


    public static void setPrivateGroupsInfo(PrivateGroupsInfo privateGroupsInfo) {
        OffsideApplication.privateGroupsInfo = privateGroupsInfo;
    }

    public static AvailableGame[] getAvailableGames() {
        return availableGames;
    }

    public static void setAvailableGames(AvailableGame[] availableGames) {
        OffsideApplication.availableGames = availableGames;
    }

    public static Context getContext() {
        return context;
    }

    public static PrivateGroup getSelectedPrivateGroup() {
        return selectedPrivateGroup;
    }

    public static void setSelectedPrivateGroup(PrivateGroup selectedPrivateGroup) {
        OffsideApplication.selectedPrivateGroup = selectedPrivateGroup;

    }

    public static UserProfileInfo getUserProfileInfo() {
        return userProfileInfo;
    }

    public static void setUserProfileInfo(UserProfileInfo userProfileInfo) {
        OffsideApplication.userProfileInfo = userProfileInfo;
    }

    public static HashMap<String, LeagueRecord[]> getLeaguesRecords() {
        if (leaguesRecords == null)
            leaguesRecords = new HashMap<>();

        return leaguesRecords;
    }

    public static PlayerAssets getPlayerAssets() {
        return playerAssets;
    }

    public static void setPlayerAssets(PlayerAssets playerAssets) {
        OffsideApplication.playerAssets = playerAssets;
    }

    public static AvailableGame getSelectedAvailableGame() {
        return selectedAvailableGame;
    }

    public static void setSelectedAvailableGame(AvailableGame selectedAvailableGame) {
        OffsideApplication.selectedAvailableGame = selectedAvailableGame;
    }

    public static String getSelectedPrivateGameId() {
        if (selectedPrivateGameId != null) {
            return selectedPrivateGameId;
        } else if (selectedAvailableGame != null) {
            return selectedAvailableGame.getPrivateGameId();
        }

        return null;
    }

    public static void setSelectedPrivateGameId(String selectedPrivateGameId) {
        OffsideApplication.selectedPrivateGameId = selectedPrivateGameId;
    }

    public static String getSelectedGameId() {
        return selectedAvailableGame != null ? selectedAvailableGame.getGameId() : null;
    }


    public static String getPlayerId() {
        return playerAssets != null ? playerAssets.getPlayerId() : null;

    }


    public static String getSelectedPrivateGroupId() {
        if (selectedPrivateGroup != null) {
            return selectedPrivateGroup.getId();
        } else if (selectedAvailableGame != null) {
            return selectedAvailableGame.getGroupId();
        }

        return null;
    }

    public static String getAndroidDeviceId() {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isBackFromNewsFeed() {
        return isBackFromNewsFeed;
    }

    public static void setIsBackFromNewsFeed(boolean isBackFromNewsFeed) {
        OffsideApplication.isBackFromNewsFeed = isBackFromNewsFeed;
    }


    public void onCreate() {

        try {
            super.onCreate();
            context = getApplicationContext();
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

            //override fonts
            FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/OpenSansHebrew-Regular.ttf");
//            FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/font_open_sans_hebrew_regular.ttf");
//            FontsOverride.setDefaultFont(this, "SERIF", "fonts/font_open_sans_hebrew_regular.ttf");
//            FontsOverride.setDefaultFont(this, "SANS_SERIF", "fonts/font_open_sans_hebrew_regular.ttf");
//
//            TypefaceUtil.overrideFont(getApplicationContext(), "SERIF", "fonts/font_open_sans_hebrew_regular.ttf");


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


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(PlayerModel updatedPlayer) {
        try {
            if (updatedPlayer == null)
                return;

            playerAnswers = updatedPlayer.getPlayerAnswers();
            gameInfo.setPlayer(updatedPlayer);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public static void cleanUserPreferences() {
        try {
            SharedPreferences settings = getContext().getSharedPreferences(context.getString(R.string.preference_name), 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(context.getString(R.string.game_id_key), null);
            editor.putString(context.getString(R.string.private_group_id_key), null);
            editor.putString(context.getString(R.string.private_game_id_key), null);

            editor.commit();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public static void setUserPreferences(String groupId, String gameId, String privateGameId ) {
        try {
            SharedPreferences settings = getContext().getSharedPreferences(context.getString(R.string.preference_name), 0);
            SharedPreferences.Editor editor = settings.edit();

            editor.putString(context.getString(R.string.game_id_key), gameId);
            editor.putString(context.getString(R.string.private_group_id_key), groupId);
            editor.putString(context.getString(R.string.private_game_id_key), privateGameId);

            editor.commit();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


    public static String getVersion() {
        return version == null ? "0.0" : version;
    }


}