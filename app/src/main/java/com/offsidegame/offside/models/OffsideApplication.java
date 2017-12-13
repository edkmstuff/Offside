package com.offsidegame.offside.models;

import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.util.Log;

import com.offsidegame.offside.BuildConfig;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.NetworkingServiceBoundEvent;
import com.offsidegame.offside.helpers.FontsOverride;
import com.offsidegame.offside.helpers.NetworkingService;

import org.acra.ACRA;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by KFIR on 1/15/2017.
 */

@ReportsCrashes(
//         formUri = "http://192.168.1.140:8080/api/Offside/AcraCrashReport",
//         formUri = "http://10.0.2.2:8080/api/Offside/AcraCrashReport",
//         formUri = "http://10.0.0.17:8080/api/Offside/AcraCrashReport",

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

//    private static String initialsProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/";
//    private static String defaultProfilePictureUrl = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK";
//    private static String defaultPictureUrlHazavitFeed = "http://10.0.0.17:8080/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT";


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

    private static String initialsProfilePictureUrl = String.format("http://%s/api/Offside/GetProfilePicture/",BuildConfig.GAME_SERVER_HOSTNAME_STRING);
    private static String defaultProfilePictureUrl = String.format("http://%s/api/Offside/GetProfilePicture/DEFAULT_SIDEKICK",BuildConfig.GAME_SERVER_HOSTNAME_STRING);
    private static String defaultPictureUrlHazavitFeed = String.format("http://%s/api/Offside/GetProfilePicture/DEFAULT_FEED_HAZAVIT",BuildConfig.GAME_SERVER_HOSTNAME_STRING);

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

    public static HashMap<String, String> availableLanguages = new HashMap<>();

    private static boolean isBackFromNewsFeed = false;

    private static boolean isBackFromCreatePrivateGroup = false;

    private static HashMap<String, LeagueRecord[]> leaguesRecords;

    private static int minRequiredBalance = 200;


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

    public static boolean isLobbyActivityVisible() {
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
        CountDownLatch latch = new CountDownLatch(1);
        String oldPlayerId = OffsideApplication.getPlayerId();
        OffsideApplication.playerAssets = playerAssets;
        String newPlayerId = OffsideApplication.getPlayerId();
        OffsideApplication.networkingService.listenToExchange(newPlayerId, latch);
        try {
            latch.await();
        } catch (Exception ex) {

        }

    }

    public static AvailableGame getSelectedAvailableGame() {
        return selectedAvailableGame;
    }

    public static void setSelectedAvailableGame(AvailableGame selectedAvailableGame) {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            String oldGameId = OffsideApplication.getSelectedGameId();
            OffsideApplication.networkingService.unBindExchange(oldGameId,latch);
            latch.await();

            latch = new CountDownLatch(1);
            OffsideApplication.selectedAvailableGame = selectedAvailableGame;
            String newGameId = OffsideApplication.getSelectedGameId();
            OffsideApplication.networkingService.listenToExchange(newGameId, latch);
            latch.await();

        } catch (Exception ex) {

        }

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
        try {

            CountDownLatch latch = new CountDownLatch(1);

            String oldPrivateGameId = OffsideApplication.getSelectedPrivateGameId();
            String oldRoutingKey = OffsideApplication.getPlayerId()+'-'+ oldPrivateGameId;
            OffsideApplication.networkingService.unBindExchange(oldRoutingKey, latch);
            OffsideApplication.networkingService.unBindExchange(oldPrivateGameId, latch);
            latch.await();

            latch = new CountDownLatch(1);
            OffsideApplication.selectedPrivateGameId = selectedPrivateGameId;
            String newPrivateGameId = OffsideApplication.getSelectedPrivateGameId();
            String routingKey = OffsideApplication.getPlayerId()+'-'+ newPrivateGameId;
            OffsideApplication.networkingService.listenToExchange(routingKey, latch);
            OffsideApplication.networkingService.listenToExchange(newPrivateGameId, latch);
            latch.await();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

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

    public static boolean isIsBackFromCreatePrivateGroup() {
        return isBackFromCreatePrivateGroup;
    }

    public static void setIsBackFromCreatePrivateGroup(boolean isBackFromCreatePrivateGroup) {
        OffsideApplication.isBackFromCreatePrivateGroup = isBackFromCreatePrivateGroup;
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

            availableLanguages.put("en", "English");
            availableLanguages.put("he", "עברית");


            //signal r
            Intent intent = new Intent();
            intent.setClass(getApplicationContext(), NetworkingService.class);
            bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);
            EventBus.getDefault().register(getApplicationContext());

            //override fonts
            FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/OpenSansHebrew-Regular.ttf");

            //ACRA.getErrorReporter().putCustomData("initialsProfilePictureUrl", initialsProfilePictureUrl);

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
    public static NetworkingService networkingService;
    //public static boolean isBoundToNetworkingService = false;
    public final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className, IBinder service) {
            // We've bound to NetworkingService, cast the IBinder and get NetworkingService instance

            NetworkingService.LocalBinder binder = (NetworkingService.LocalBinder) service;
            networkingService = binder.getService();
            //isBoundToNetworkingService = true;
            EventBus.getDefault().post(new NetworkingServiceBoundEvent(getApplicationContext()));

        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            //isBoundToNetworkingService = false;
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


            OffsideApplication.setSelectedPrivateGroup(null);
            OffsideApplication.setSelectedPrivateGameId(null);
            OffsideApplication.setSelectedAvailableGame(null);
            OffsideApplication.setSelectedPrivateGameId(null);
            OffsideApplication.setPlayerAssets(null);
            OffsideApplication.setGameInfo(null);
            OffsideApplication.setPrivateGroupsInfo(null);
            OffsideApplication.setUserProfileInfo(null);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    public static void setUserPreferences(String groupId, String gameId, String privateGameId) {
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


    public static int getMinRequiredBalance() {
        return minRequiredBalance;
    }
}