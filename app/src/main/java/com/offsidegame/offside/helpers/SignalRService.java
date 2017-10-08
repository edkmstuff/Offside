package com.offsidegame.offside.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.offsidegame.offside.R;

import com.offsidegame.offside.activities.LobbyActivity;
import com.offsidegame.offside.events.AvailableGameEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.FriendInviteReceivedEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.PlayerModelEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.PrivateGroupChangedEvent;
import com.offsidegame.offside.events.PrivateGroupCreatedEvent;
import com.offsidegame.offside.events.PrivateGroupEvent;
import com.offsidegame.offside.models.LeagueRecord;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.PostAnswerRequestInfo;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.QuestionsEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.Position;
import com.offsidegame.offside.models.PrivateGroup;

import com.offsidegame.offside.models.PrivateGroupsInfo;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Scoreboard;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.events.SignalRErrorEvent;
import com.offsidegame.offside.models.User;
import com.offsidegame.offside.models.UserProfileInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.util.Date;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.StateChangedCallback;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import microsoft.aspnet.signalr.client.transport.ClientTransport;
import microsoft.aspnet.signalr.client.transport.ServerSentEventsTransport;


/**
 * Created by KFIR on 11/15/2016.
 */

public class SignalRService extends Service {
    public HubConnection hubConnection;
    private HubProxy hub;
    private final IBinder binder = new LocalBinder(); // Binder given to clients
    private Date startReconnecting = null;
    public final String ip = new String("10.0.2.2:18313");

    //public final String ip = new String("10.0.2.2:18313");
    //public final String ip = new String("192.168.1.140:18313");
    //public final String ip = new String("10.0.0.17:18313");


    //public final String ip = new String("offside.somee.com");
    //public final String ip = new String("sidekicknode.azurewebsites.net");

    public Boolean stoppedIntentionally = false;
    private int mId = -1;

    //request response flags

    private boolean chatMessagesReceived = false;
    private boolean chatMessageReceived = false;
    private boolean scoreboardReceived = false;
    private boolean playerDataReceived = false; //no request from client, only push from server
    private boolean positionReceived = false; //no request from client, only push from server
    private boolean answerAccepted = false;
    private boolean privateGroupCreated = false;
    private boolean privateGroupReceived = false;
    private boolean privateGroupChangedReceived = false; //no request from client, only push from server
    private boolean availableGamesReceived = false;
    private boolean leagueRecordsReceived = false;
    private boolean privateGameCreated = false;
    private boolean playerJoinedPrivateGame = false;
    private boolean loggedInUserReceived = false;
    private boolean userProfileInfoReceived = false;
    private boolean playerAssetsReceived = false;
    private boolean availableGameReceived = false;
    private boolean friendInviteReceived = false;
    private boolean playerImageSaved = false;
    private boolean privateGroupsReceived = false;


    //<editor-fold desc="constructors">
    public SignalRService() {
    }

    //</editor-fold>

    //<editor-fold desc="startup">
    @Override
    public void onCreate() {
        super.onCreate();
        //handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR(false);
        return result;
    }

    @Override
    public void onDestroy() {
        if (OffsideApplication.signalRService != null)
            return;
        hubConnection.stop();
        super.onDestroy();

    }

    private void deleteSignalRServiceReferenceFromApplication() {
        if (OffsideApplication.signalRService != null && OffsideApplication.signalRService.hubConnection != null) {
            OffsideApplication.signalRService.hubConnection.stop();
            OffsideApplication.signalRService.stoppedIntentionally = true;

        }

        OffsideApplication.signalRService = null;
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        if (OffsideApplication.signalRService != null) {
            deleteSignalRServiceReferenceFromApplication();
        }
        startSignalR(false);
        return binder;
    }

    private void startSignalR(Boolean notifyWhenConnected) {
        try {
            Platform.loadPlatformComponent(new AndroidPlatformComponent());
            final String serverUrl = "http://" + ip;
            hubConnection = new HubConnection(serverUrl);
            //final String SERVER_HUB = "OffsideHub";
            final String SERVER_HUB = "SidekickNodeHub";
            hub = hubConnection.createHubProxy(SERVER_HUB);
            ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());

            hubConnection.stateChanged(new StateChangedCallback() {

                @Override
                public void stateChanged(ConnectionState oldState, ConnectionState newState) {
                    try {
                        if (newState == ConnectionState.Disconnected && !stoppedIntentionally) {
                            ACRA.getErrorReporter().handleSilentException(new Throwable("SignalR disconnected"));

                            EventBus.getDefault().post(new ConnectionEvent(false, "disconnected"));
                            //try reconnection for 10 min
                            if (startReconnecting == null) {
                                startReconnecting = new Date();
                            } else
                                return; // we are already trying to reconnect
                            Date now = new Date();
                            while (startReconnecting != null && now.getTime() - startReconnecting.getTime() < 10 * 60 * 1000
                                    && hubConnection.getState() == ConnectionState.Disconnected) {

                                startSignalR(true);  //sending true to identify a reconnection event
                                now = new Date();
                                Thread.sleep(5000);

                            }
                            if (hubConnection.getState() == ConnectionState.Connected) {
                                startReconnecting = null;
                                //just for putting message that we are connected
                                EventBus.getDefault().post(new ConnectionEvent(true, "connected"));
                            }
                        }
                    } catch (Exception ex) {
                        ACRA.getErrorReporter().handleSilentException(ex);
                        EventBus.getDefault().post(new ConnectionEvent(false, ex.getMessage()));
                    }

                }
            });


            SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

            signalRFuture.get();
//            try {
//                signalRFuture.get();
//            } catch (InterruptedException | ExecutionException e) {
//                Log.e("SimpleSignalR", e.getMessage());
//                //return;
//            }

            if (notifyWhenConnected && hubConnection.getState() == ConnectionState.Connected) {
                EventBus.getDefault().post(new SignalRServiceBoundEvent(null));
                startReconnecting = null;
            }

            if (hubConnection.getState() == ConnectionState.Connected) {
                subscribeToServer();
                EventBus.getDefault().post(new ConnectionEvent(true, "connected"));
            }
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    //</editor-fold>

    //<editor-fold desc="subscribeToServer">
    public void subscribeToServer() {

        //chat

        hub.on("ChatMessagesReceived", new SubscriptionHandler1<String>() {
            @Override
            public void run(String chatJson) {
                chatMessagesReceived = true;
                final Gson gson = new GsonBuilder().create();
                Chat chat = gson.fromJson(chatJson, Chat.class);
                EventBus.getDefault().post(new ChatEvent(chat));
            }
        }, String.class);

        hub.on("ChatMessageReceived", new SubscriptionHandler1<ChatMessage>() {
            @Override
            public void run(ChatMessage chatMessage) {
                chatMessageReceived = true;
                fireNotification(chatMessage.getMessageType(), chatMessage.getMessageText());
                EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
            }
        }, ChatMessage.class);

        hub.on("ScoreboardReceived", new SubscriptionHandler1<Scoreboard>() {
            @Override
            public void run(Scoreboard scoreboard) {
                scoreboardReceived = true;
                EventBus.getDefault().post(new ScoreboardEvent(scoreboard));
            }
        }, Scoreboard.class);

        hub.on("PlayerDataReceived", new SubscriptionHandler1<String>() {
            @Override
            public void run(String playerJson) {
                playerDataReceived = true;
                final Gson gson = new GsonBuilder().create();
                PlayerModel playerModel = gson.fromJson(playerJson, PlayerModel.class);

                EventBus.getDefault().post(new PlayerModelEvent(playerModel));
            }

        }, String.class);

        hub.on("PositionReceived", new SubscriptionHandler1<Position>() {
            @Override
            public void run(Position position) {
                positionReceived = true;
                EventBus.getDefault().post(new PositionEvent(position));
            }
        }, Position.class);

        hub.on("AnswerAccepted", new SubscriptionHandler1<PostAnswerRequestInfo>() {
            @Override
            public void run(PostAnswerRequestInfo postAnswerRequestInfo) {
                answerAccepted = true;
                EventBus.getDefault().post(postAnswerRequestInfo);
            }
        }, PostAnswerRequestInfo.class);

        //groups

        hub.on("PrivateGroupsReceived", new SubscriptionHandler1<PrivateGroupsInfo>() {
            @Override
            public void run(PrivateGroupsInfo privateGroupsInfo) {
                privateGroupsReceived = true;
                EventBus.getDefault().post(privateGroupsInfo);
            }
        }, PrivateGroupsInfo.class);

        hub.on("PrivateGroupCreated", new SubscriptionHandler1<PrivateGroup>() {
            @Override
            public void run(PrivateGroup privateGroup) {
                privateGroupCreated = true;
                EventBus.getDefault().post(new PrivateGroupCreatedEvent(privateGroup));
            }
        }, PrivateGroup.class);

        hub.on("PrivateGroupReceived", new SubscriptionHandler1<PrivateGroup>() {
            @Override
            public void run(PrivateGroup privateGroup) {
                privateGroupReceived = true;
                EventBus.getDefault().post(new PrivateGroupEvent(privateGroup));
            }
        }, PrivateGroup.class);

        hub.on("PrivateGroupChangedReceived", new SubscriptionHandler1<PrivateGroup>() {
            @Override
            public void run(PrivateGroup privateGroup) {
                privateGroupChangedReceived = true;
                EventBus.getDefault().post(new PrivateGroupChangedEvent(privateGroup));
            }
        }, PrivateGroup.class);

        hub.on("AvailableGamesReceived", new SubscriptionHandler1<AvailableGame[]>() {
            @Override
            public void run(AvailableGame[] availableGames) {
                availableGamesReceived = true;
                EventBus.getDefault().post(availableGames);
            }
        }, AvailableGame[].class);

        hub.on("LeagueRecordsReceived", new SubscriptionHandler1<LeagueRecord[]>() {
            @Override
            public void run(LeagueRecord[] leagueRecords) {
                leagueRecordsReceived = true;
                EventBus.getDefault().post(leagueRecords);
            }
        }, LeagueRecord[].class);

        //privateGame

        hub.on("PrivateGameCreated", new SubscriptionHandler1<String>() {
            @Override
            public void run(String privateGameId) {
                privateGameCreated = true;
                EventBus.getDefault().post(new PrivateGameGeneratedEvent(privateGameId));
            }
        }, String.class);

        hub.on("PlayerJoinedPrivateGame", new SubscriptionHandler1<String>() {
            @Override
            public void run(String gameInfoJson) {
                playerJoinedPrivateGame = true;
                final Gson gson = new GsonBuilder().create();
                GameInfo gameInfo = gson.fromJson(gameInfoJson, GameInfo.class);
                EventBus.getDefault().post(new JoinGameEvent(gameInfo));
            }
        }, String.class);


        //user

        hub.on("LoggedInUserReceived", new SubscriptionHandler1<PlayerAssets>() {
            @Override
            public void run(PlayerAssets playerAssets) {
                loggedInUserReceived = true;
                EventBus.getDefault().post(playerAssets);
            }
        }, PlayerAssets.class);

        hub.on("PlayerImageSaved", new SubscriptionHandler1<Boolean>() {
            @Override
            public void run(Boolean playerImageSaved) {
                playerImageSaved = true;
                EventBus.getDefault().post(playerImageSaved);
            }
        }, Boolean.class);


        hub.on("UserProfileInfoReceived", new SubscriptionHandler1<UserProfileInfo>() {
            @Override
            public void run(UserProfileInfo userProfileInfo) {
                userProfileInfoReceived = true;
                EventBus.getDefault().post(userProfileInfo);
            }
        }, UserProfileInfo.class);

        hub.on("PlayerAssetsReceived", new SubscriptionHandler1<PlayerAssets>() {
            @Override
            public void run(PlayerAssets playerAssets) {
                playerAssetsReceived = true;
                EventBus.getDefault().post(playerAssets);
            }
        }, PlayerAssets.class);

        hub.on("AvailableGameReceived", new SubscriptionHandler1<AvailableGame>() {
            @Override
            public void run(AvailableGame availableGame) {
                availableGameReceived = true;
                EventBus.getDefault().post(new AvailableGameEvent(availableGame));
            }
        }, AvailableGame.class);

        hub.on("FriendInviteReceived", new SubscriptionHandler1<String>() {
            @Override
            public void run(String friendInviteCode) {
                friendInviteReceived = true;
                EventBus.getDefault().post(new FriendInviteReceivedEvent(friendInviteCode));
            }
        }, String.class);

    }

    private void fireNotification(String messageType, String message) {

        boolean isAskedQuestion = messageType.equals(OffsideApplication.getMessageTypeAskedQuestion());
        boolean isCloseQuestion = messageType.equals(OffsideApplication.getMessageTypeClosedQuestion());

        if (isAskedQuestion || isCloseQuestion) {
            MediaPlayer player;

            int soundResource = R.raw.human_whisle;
            if (isCloseQuestion) {
                final Gson gson = new GsonBuilder().create();
                Question question = gson.fromJson(message, Question.class);
                if (OffsideApplication.playerAnswers.containsKey(question.getId()) && OffsideApplication.playerAnswers.get(question.getId()).getAnswerId().equals(question.getCorrectAnswerId())) {
                    soundResource = ((int) (Math.random() * 100)) % 2 == 0 ? R.raw.bravo : R.raw.hooray;
                } else {
                    soundResource = R.raw.aww;
                }
            }

            player = MediaPlayer.create(getApplicationContext(), soundResource);
            player.start();

            if (!OffsideApplication.isChatActivityVisible()) {

                int titleResource = R.string.lbl_new_question_is_waiting_for_you;
                int textResource = R.string.lbl_click_to_answer;
                if (isCloseQuestion) {
                    titleResource = R.string.lbl_we_have_an_answer;
                    textResource = R.string.lbl_click_to_view;
                }


                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.mipmap.ic_offside_logo)
                        .setContentTitle(getString(titleResource))
                        .setDefaults(NotificationCompat.DEFAULT_ALL)
                        .setContentText(getString(textResource))
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

// Creates an explicit intent for an Activity in your app
                Intent chatIntent = new Intent(this, LobbyActivity.class);

// The stack builder object will contain an artificial back stack for the
// started Activity.
// This ensures that navigating backward from the Activity leads out of
// your application to the Home screen.
                TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
// Adds the back stack for the Intent (but not the Intent itself)
                stackBuilder.addParentStack(LobbyActivity.class);
// Adds the Intent that starts the Activity to the top of the stack
                stackBuilder.addNextIntent(chatIntent);
                PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
                mBuilder.setContentIntent(resultPendingIntent);
                NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
// mId allows you to update the notification later on.
                mNotificationManager.notify(mId, mBuilder.build());

            }


        }


    }

//    private void notifyOnNewQuestion() {
//
//        MediaPlayer player;
//        player = MediaPlayer.create(getApplicationContext(), R.raw.human_whisle);
//        player.start();
//
//
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
//                .setSmallIcon(R.mipmap.ic_offside_logo)
//                .setContentTitle(getString(R.string.lbl_new_question_is_waiting_for_you))
//                .setDefaults(NotificationCompat.DEFAULT_ALL)
//                .setContentText(getString(R.string.lbl_click_to_answer))
//                .setPriority(NotificationCompat.PRIORITY_HIGH);
//
//// Creates an explicit intent for an Activity in your app
//        Intent chatIntent = new Intent(this, ChatActivity.class);
//
//// The stack builder object will contain an artificial back stack for the
//// started Activity.
//// This ensures that navigating backward from the Activity leads out of
//// your application to the Home screen.
//        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
//// Adds the back stack for the Intent (but not the Intent itself)
//        stackBuilder.addParentStack(ChatActivity.class);
//// Adds the Intent that starts the Activity to the top of the stack
//        stackBuilder.addNextIntent(chatIntent);
//        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
//        mBuilder.setContentIntent(resultPendingIntent);
//        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//// mId allows you to update the notification later on.
//        mNotificationManager.notify(mId, mBuilder.build());
//
//    }

    //</editor-fold>

    //<editor-fold desc="methods for client activities">


//    public void joinGame(String privateGameCode, String playerId, String playerDisplayName, String playerProfilePictureUrl, boolean isPrivateGameCreator, String androidDeviceId) {
//        PlayerInfo playerInfo = new PlayerInfo(playerId, privateGameCode, playerDisplayName, playerProfilePictureUrl, isPrivateGameCreator, androidDeviceId, null);
//        if (!(hubConnection.getState() == ConnectionState.Connected))
//            return;
//
//        //hub.invoke(GameInfo.class, "JoinPrivateGame", privateGameCode, playerId, playerDisplayName, playerProfilePictureUrl, isPrivateGameCreator, androidDeviceId).done(new Action<GameInfo>() {
//        hub.invoke(GameInfo.class, "requestJoinPrivateGame", playerInfo).done(new Action<GameInfo>() {
//
//            @Override
//            public void run(GameInfo gameInfo) throws Exception {
//                //EventBus.getDefault().post(new JoinGameEvent(gameInfo));
//            }
//        }).onError(new ErrorCallback() {
//            @Override
//            public void onError(Throwable error) {
//                ACRA.getErrorReporter().handleSilentException(error);
//            }
//        });
//    }

    public void quitGame(String gameId, String playerId, String androidDeviceId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;

        hub.invoke(Boolean.class, "QuitGame", gameId, playerId, androidDeviceId).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                ACRA.getErrorReporter().handleSilentException(error);
            }
        });
    }

    public void requestAvailableGame(String gameId, String privateGameId, String playerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        availableGameReceived = false;
        hub.invoke("RequestAvailableGame", gameId, privateGameId, playerId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!availableGameReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestAvailableGame"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestAvailableGame"));
            }
        });
    }

    public void requestCreatePrivateGame(String gameId, String groupId, String playerId, String selectedLanguage) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;

        //String languageLocale = Locale.getDefault().getDisplayLanguage();
        privateGameCreated = false;
        hub.invoke("RequestCreatePrivateGame", gameId, groupId, playerId, selectedLanguage).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!privateGameCreated)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestCreatePrivateGame"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestCreatePrivateGame"));
            }
        });
    }

    public void requestPostAnswer(final String gameId, final String playerId, final String questionId, final String answerId, final boolean isSkipped, final int betSize) {

        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        answerAccepted = false;
        hub.invoke("RequestPostAnswer", gameId, playerId, questionId, answerId, isSkipped, betSize).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!answerAccepted)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestPostAnswer"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestPostAnswer"));
            }
        });

    }

    public void requestGetScoreboard(String gameId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        scoreboardReceived = false;
        hub.invoke("RequestGetScoreboard", gameId).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!scoreboardReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestGetScoreboard"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestGetScoreboard"));
            }
        });
    }

    public void requestGetChatMessages(String gameId, String privateGameId, String playerId, String androidDeviceId) {

        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;

        chatMessagesReceived = false;
        hub.invoke("requestGetChatMessages", gameId, privateGameId, playerId, androidDeviceId).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!chatMessagesReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("requestGetChatMessages"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("requestGetChatMessages"));
            }
        });

    }

    public void requestSendChatMessage(String gameId, String privateGameId, String message, String playerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        chatMessageReceived = false;
        hub.invoke("RequestSendChatMessage", gameId, privateGameId, playerId, message).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!chatMessageReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestSendChatMessage"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestSendChatMessage"));
            }
        });

    }



    public boolean requestSaveLoggedInUser(User user) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return false;

        loggedInUserReceived = false;
        hub.invoke("RequestSaveLoggedInUser", user.getId(), user.getName(), user.getEmail(), user.getProfilePictureUri()).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!loggedInUserReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestSaveLoggedInUser"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestSaveLoggedInUser"));
            }
        });

        return true;
    }

    public boolean setPowerItems(String gameId, String playerId, int powerItems, boolean isDueToRewardVideo) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return false;

        hub.invoke(Integer.class, "SetPowerItems", gameId, playerId, powerItems, isDueToRewardVideo).done(new Action<Integer>() {
            @Override
            public void run(Integer newPowerItemsValue) throws Exception {
                EventBus.getDefault().post(newPowerItemsValue);
            }

        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                ACRA.getErrorReporter().handleSilentException(error);
            }
        });

        return true;
    }



    public void requestSaveImageInDatabase(String playerId, String imageString) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        playerImageSaved = false;
        hub.invoke("RequestSaveImageInDatabase", playerId, imageString).done(new Action<Void>() {

            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!playerImageSaved)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestSaveImageInDatabase"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestSaveImageInDatabase"));
            }
        });
    }

    public void requestPrivateGroupsInfo(String playerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        privateGroupsReceived = false;
        hub.invoke("RequestPrivateGroupsInfo", playerId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!privateGroupsReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestPrivateGroupsInfo"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestPrivateGroupsInfo"));
            }
        });
    }

    public void requestAvailableGames(String playerId, String groupId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        availableGamesReceived = false;
        hub.invoke("RequestAvailableGames", playerId, groupId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!availableGamesReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestAvailableGames"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestAvailableGames"));
            }
        });

    }

    public void requestCreatePrivateGroup(String groupName, String groupType, String playerId, String selectedLanguage) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        privateGroupCreated = false;
        hub.invoke("RequestCreatePrivateGroup", groupName, groupType, playerId, selectedLanguage).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!privateGroupCreated)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestCreatePrivateGroup"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestCreatePrivateGroup"));
            }
        });

    }

    public void requestJoinPrivateGame(String gameId, String groupId, String privateGameId, String playerId, String androidDeviceId) {

        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;

        playerJoinedPrivateGame = false;
        hub.invoke("RequestJoinPrivateGame", gameId, groupId, privateGameId, playerId, androidDeviceId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!playerJoinedPrivateGame)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestJoinPrivateGame"));
                    }
                }, 15000);

            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestJoinPrivateGame"));
            }
        });


    }

    public void requestUserProfileData(String playerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        userProfileInfoReceived = false;
        hub.invoke("RequestUserProfile", playerId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!userProfileInfoReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestUserProfile"));
                    }
                }, 15000);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestUserProfile"));
            }
        });
    }

    public void requestLeagueRecords(String playerId, String groupId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        leagueRecordsReceived = false;
        hub.invoke("RequestLeagueRecords", playerId, groupId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!leagueRecordsReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestLeagueRecords"));
                    }
                }, 15000);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestLeagueRecords"));
            }
        });
    }

    public void requestPlayerAssets(String playerId) {

        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        playerAssetsReceived = false;
        hub.invoke("RequestPlayerAssets", playerId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!playerAssetsReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestPlayerAssets"));
                    }
                }, 15000);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestPlayerAssets"));
            }
        });
    }

    public void requestInviteFriend(String groupId, String gameId, String privateGameId, String inviterPlayerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        friendInviteReceived = false;
        hub.invoke("RequestInviteFriend", groupId, gameId, privateGameId, inviterPlayerId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!friendInviteReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestInviteFriend"));
                    }
                }, 15000);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestInviteFriend"));
            }
        });
    }

    public void requestPrivateGroup(String playerId, String groupId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        privateGroupReceived = false;
        hub.invoke("RequestPrivateGroup", playerId, groupId).done(new Action<Void>() {
            @Override
            public void run(Void obj) throws Exception {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!privateGroupReceived)
                            EventBus.getDefault().post(new SignalRErrorEvent("RequestPrivateGroup"));
                    }
                }, 15000);
            }
        }).onError(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                EventBus.getDefault().post(new SignalRErrorEvent("RequestPrivateGroup"));
            }
        });
    }


    //</editor-fold>

    //<editor-fold desc="support classes">

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public SignalRService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return SignalRService.this;
        }
    }

    //</editor-fold>


}

