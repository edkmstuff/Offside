package com.offsidegame.offside.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.offsidegame.offside.BuildConfig;
import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.LobbyActivity;
import com.offsidegame.offside.activities.LoginActivity;
import com.offsidegame.offside.events.AvailableGameEvent;
import com.offsidegame.offside.events.ChatEvent;
import com.offsidegame.offside.events.ChatMessageEvent;
import com.offsidegame.offside.events.CompletedHttpRequestEvent;
import com.offsidegame.offside.events.FriendInviteReceivedEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NetworkingErrorEvent;
import com.offsidegame.offside.events.NotificationBubbleEvent;
import com.offsidegame.offside.events.PlayerImageSavedEvent;
import com.offsidegame.offside.events.PlayerJoinPrivateGroupEvent;
import com.offsidegame.offside.events.PlayerModelEvent;
import com.offsidegame.offside.events.PlayerQuitFromPrivateGameEvent;
import com.offsidegame.offside.events.PlayerRewardedReceivedEvent;
import com.offsidegame.offside.events.PositionEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.PrivateGroupChangedEvent;
import com.offsidegame.offside.events.PrivateGroupCreatedEvent;
import com.offsidegame.offside.events.PrivateGroupDeletedEvent;
import com.offsidegame.offside.events.PrivateGroupEvent;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.models.AvailableGame;
import com.offsidegame.offside.models.Chat;
import com.offsidegame.offside.models.ChatMessage;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.KeyValue;
import com.offsidegame.offside.models.LeagueRecord;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerAssets;
import com.offsidegame.offside.models.PlayerModel;
import com.offsidegame.offside.models.Position;
import com.offsidegame.offside.models.PostAnswerRequestInfo;
import com.offsidegame.offside.models.PrivateGameInfo;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupsInfo;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.Scoreboard;
import com.offsidegame.offside.models.UserProfileInfo;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import com.rabbitmq.client.Recoverable;
import com.rabbitmq.client.RecoverableConnection;
import com.rabbitmq.client.RecoveryListener;
import com.rabbitmq.client.ShutdownListener;
import com.rabbitmq.client.ShutdownSignalException;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;


/**
 * Created by KFIR on 11/15/2016.
 */

public class NetworkingService extends Service {

    private RecoverableConnection connection;
    private Channel channel;
    private String listenerQueueName;

    private String password = "kfir";
    private String userName = "kfir";

    /****************************DEVELOPMENT**************************/
   // private String hostName = "10.0.2.2";
    //private String hostName = "192.168.1.140";
//    private String hostName = "10.0.0.17";

    /****************************PRODUCTION**************************/
    //private String hostName = "sktestvm.westeurope.cloudapp.azure.com";
    private String hostName = BuildConfig.RABBITMQ_HOSTNAME_STRING;

    private final IBinder binder = new LocalBinder();
    private String CLIENT_REQUESTS_EXCHANGE_NAME = "FROM_CLIENTS";
    private int sendToServerErrorDelay = 15000;
    private int mId = -1;
    //private Map<Channel, String> currentExchanges = new HashMap<>();


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
    private boolean privateGroupDeleted = false;
    private boolean playerJoinPrivateGroupReceived = false;
    private boolean playerRewardSaved = false;
    private boolean playerQuitPrivateGame = false;
    private boolean playerJoinedPrivateGameByCode = false;


    public NetworkingService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        try {
            startRabbitMq();
        } catch (InterruptedException ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
        return result;
    }

    @Override
    public void onDestroy() {
        stopRabbitMq();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {

        try {
            startRabbitMq();
            //ACRA.getErrorReporter().putCustomData("RabbitMqHostName", hostName);
        } catch (InterruptedException ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

        return binder;
    }

    private void startRabbitMq() throws InterruptedException {
        final CountDownLatch latch = new CountDownLatch(1);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    stopRabbitMq();
                    ConnectionFactory factory = new ConnectionFactory();

                    factory.setHost(hostName);
                    factory.setUsername(userName);
                    factory.setPassword(password);
                    factory.setAutomaticRecoveryEnabled(true);
                    factory.setRequestedHeartbeat(10);
                    factory.setConnectionTimeout(10000);
                    connection = (RecoverableConnection) factory.newConnection();
                    channel = connection.createChannel();

                    connection.addShutdownListener(new ShutdownListener() {
                        @Override
                        public void shutdownCompleted(ShutdownSignalException cause) {
                            try {
                                System.out.println("connection shutdownCompleted");
                            } catch (Exception ex) {
                                ACRA.getErrorReporter().handleSilentException(ex);
                            }
                        }
                    });

                    connection.addRecoveryListener(new RecoveryListener() {
                        @Override
                        public void handleRecovery(Recoverable recoverable) {
                            EventBus.getDefault().post(new LoadingEvent(true,"Done!"));
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }

                        @Override
                        public void handleRecoveryStarted(Recoverable recoverable) {
                            EventBus.getDefault().post(new LoadingEvent(true,"Disconnected. Working on it..."));
                        }
                    });




                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                } finally {

                    if (latch != null)
                        latch.countDown();

                }

            }
        }).start();
        latch.await();
    }

    private void stopRabbitMq() {

        if (channel != null && channel.isOpen()) {
            try {
                channel.close();
            } catch (Exception ex) {
                ACRA.getErrorReporter().handleSilentException(ex);
            }
        }
        if (connection != null && connection.isOpen()) {
            try {
                connection.close();
            } catch (Exception ex) {
                ACRA.getErrorReporter().handleSilentException(ex);
            }
        }
    }

    public void createListenerQueue(final String queueName, final CountDownLatch latch) {

        try {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (listenerQueueName != null)
                            return;
                        if (queueName == null)
                            return;
                        listenerQueueName = channel.queueDeclare(queueName, false, false, false, null).getQueue();


                    } catch (Exception ex) {
                        ACRA.getErrorReporter().handleSilentException(ex);

                    } finally {

                        //finally we release the waiting thread
                        if (latch != null)
                            latch.countDown();


                    }
                }
            }).start();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return;
        }


    }

    private String consumerTag = null;

    public void listenToExchange(final String exchangeName, final CountDownLatch latch) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {

                    if (exchangeName == null)
                        return;

                    channel.exchangeDeclare(exchangeName, "fanout");
                    channel.queueBind(listenerQueueName, exchangeName, "");

                    if (consumerTag != null)
                        return;

                    consumerTag = channel.basicConsume(listenerQueueName, true, new DefaultConsumer(channel) {
                        @Override
                        public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                            String json = new String(body, "UTF-8");
                            final Gson gson = new GsonBuilder().create();
                            KeyValue wrapper = gson.fromJson(json, KeyValue.class);
                            onServerMessage(wrapper.getKey(), wrapper.getValue());
                        }
                    });


                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                } finally {

                    //finally we release the waiting thread
                    if (latch != null)
                        latch.countDown();

                }
            }
        }).start();

    }

    public void unBindExchange(final String exchangeName, final CountDownLatch latch) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (exchangeName == null || listenerQueueName == null)
                        return;

                    channel.queueUnbind(listenerQueueName, exchangeName, "");

                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                } finally {

                    //finally we release the waiting thread
                    if (latch != null)
                        latch.countDown();

                }
            }
        }).start();

    }


    public void stopListening() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    if (consumerTag != null) {
                        channel.basicCancel(consumerTag);
                        consumerTag = null;
                    }

                    if (listenerQueueName != null) {
                        channel.queueDelete(listenerQueueName);
                        listenerQueueName = null;

                    }

                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                }
            }
        }).start();

    }


    public void onServerMessage(String message, String model) {
        try
        {
            final Gson gson = new GsonBuilder().create();

            if (message.equals("ChatMessagesReceived")) {
                Chat chat = gson.fromJson(model, Chat.class);
                chatMessagesReceived = true;
                EventBus.getDefault().post(new ChatEvent(chat));
            } else if (message.equals("ChatMessageReceived")) {
                ChatMessage chatMessage = gson.fromJson(model, ChatMessage.class);
                chatMessageReceived = true;
                fireNotification(chatMessage.getMessageType(), chatMessage.getMessageText());
                EventBus.getDefault().post(new ChatMessageEvent(chatMessage));
                EventBus.getDefault().post(new NotificationBubbleEvent(NotificationBubbleEvent.navigationItemChat));
            } else if (message.equals("ScoreboardReceived")) {
                Scoreboard scoreboard = gson.fromJson(model, Scoreboard.class);
                scoreboardReceived = true;
                EventBus.getDefault().post(new ScoreboardEvent(scoreboard));
            } else if (message.equals("PlayerDataReceived")) {
                PlayerModel playerModel = gson.fromJson(model, PlayerModel.class);
                playerDataReceived = true;
                EventBus.getDefault().post(new PlayerModelEvent(playerModel));
            } else if (message.equals("PositionReceived")) {
                Position position = gson.fromJson(model, Position.class);
                positionReceived = true;
                EventBus.getDefault().post(new PositionEvent(position));
            } else if (message.equals("AnswerAcceptedReceived")) {
                PostAnswerRequestInfo postAnswerRequestInfo = gson.fromJson(model, PostAnswerRequestInfo.class);
                answerAccepted = true;
                EventBus.getDefault().post(postAnswerRequestInfo);
            } else if (message.equals("PrivateGroupsReceived")) {
                PrivateGroupsInfo privateGroupsInfo = gson.fromJson(model, PrivateGroupsInfo.class);
                privateGroupsReceived = true;
                EventBus.getDefault().post(privateGroupsInfo);
            } else if (message.equals("PrivateGroupCreated")) {
                PrivateGroup privateGroup = gson.fromJson(model, PrivateGroup.class);
                privateGroupCreated = true;
                EventBus.getDefault().post(new PrivateGroupCreatedEvent(privateGroup));
            } else if (message.equals("PrivateGroupDeletedReceived")) {
                KeyValue numberOfDeletedGroupsKeyValue = gson.fromJson(model, KeyValue.class);
                Integer numberOfDeletedGroups = Integer.parseInt(numberOfDeletedGroupsKeyValue.getValue());
                privateGroupDeleted = true;
                EventBus.getDefault().post(new PrivateGroupDeletedEvent(numberOfDeletedGroups));
            } else if (message.equals("PlayerJoinedPrivateGroupReceived")) {
                KeyValue numberOfPlayerAddedKeyValue = gson.fromJson(model, KeyValue.class);
                Integer numberOfPlayerAdded = Integer.parseInt(numberOfPlayerAddedKeyValue.getValue());
                playerJoinPrivateGroupReceived = true;
                EventBus.getDefault().post(new PlayerJoinPrivateGroupEvent(numberOfPlayerAdded));
            } else if (message.equals("PrivateGroupReceived")) {
                PrivateGroup privateGroup = gson.fromJson(model, PrivateGroup.class);
                privateGroupReceived = true;
                EventBus.getDefault().post(new PrivateGroupEvent(privateGroup));
            } else if (message.equals("PrivateGroupChangedReceived")) {
                PrivateGroup privateGroup = gson.fromJson(model, PrivateGroup.class);
                privateGroupChangedReceived = true;
                EventBus.getDefault().post(new PrivateGroupChangedEvent(privateGroup));
            } else if (message.equals("AvailableGamesReceived")) {
                AvailableGame[] availableGames = gson.fromJson(model, AvailableGame[].class);
                availableGamesReceived = true;
                EventBus.getDefault().post(availableGames);
            } else if (message.equals("LeagueRecordsReceived")) {
                LeagueRecord[] leagueRecords = gson.fromJson(model, LeagueRecord[].class);
                leagueRecordsReceived = true;
                EventBus.getDefault().post(leagueRecords);
            } else if (message.equals("PrivateGameCreated")) {
                KeyValue privateGameIdKeyValue = gson.fromJson(model, KeyValue.class);
                String privateGameId = privateGameIdKeyValue.getValue();
                privateGameCreated = true;
                EventBus.getDefault().post(new PrivateGameGeneratedEvent(privateGameId));
            } else if (message.equals("PlayerJoinedPrivateGame")) {
                GameInfo gameInfo = gson.fromJson(model, GameInfo.class);
                playerJoinedPrivateGame = true;
                EventBus.getDefault().post(new JoinGameEvent(gameInfo));
            } else if (message.equals("LoggedInUserReceived")) {
                PlayerAssets playerAssets = gson.fromJson(model, PlayerAssets.class);
                loggedInUserReceived = true;
                EventBus.getDefault().post(playerAssets);
            } else if (message.equals("PlayerImageSaved")) {
                KeyValue isPlayerImageSavedKeyValue = gson.fromJson(model, KeyValue.class);
                Boolean isPlayerImageSaved = Boolean.parseBoolean(isPlayerImageSavedKeyValue.getValue());
                playerImageSaved = true;
                EventBus.getDefault().post(new PlayerImageSavedEvent(isPlayerImageSaved));
            } else if (message.equals("UserProfileInfoReceived")) {
                UserProfileInfo userProfileInfo = gson.fromJson(model, UserProfileInfo.class);
                userProfileInfoReceived = true;
                EventBus.getDefault().post(userProfileInfo);
            } else if (message.equals("PlayerAssetsReceived")) {
                PlayerAssets playerAssets = gson.fromJson(model, PlayerAssets.class);
                playerAssetsReceived = true;
                EventBus.getDefault().post(playerAssets);
            } else if (message.equals("AvailableGameReceived")) {
                AvailableGame availableGame = gson.fromJson(model, AvailableGame.class);
                availableGameReceived = true;
                EventBus.getDefault().post(new AvailableGameEvent(availableGame));
            } else if (message.equals("FriendInviteReceived")) {
                KeyValue friendInviteCodeKeyValue = gson.fromJson(model, KeyValue.class);
                String friendInviteCode = friendInviteCodeKeyValue.getValue();
                friendInviteReceived = true;
                EventBus.getDefault().post(new FriendInviteReceivedEvent(friendInviteCode));
            } else if (message.equals("PlayerRewardedReceived")) {
                KeyValue rewardValueKeyValue = gson.fromJson(model, KeyValue.class);
                Integer rewardValue = Integer.parseInt(rewardValueKeyValue.getValue());
                playerRewardSaved = true;
                EventBus.getDefault().post(new PlayerRewardedReceivedEvent(rewardValue));
            } else if (message.equals("PlayerQuitPrivateGameReceived")) {
                KeyValue playerWasRemovedFromPrivateGameKeyValue = gson.fromJson(model, KeyValue.class);
                boolean playerWasRemovedFromPrivateGame = Boolean.parseBoolean(playerWasRemovedFromPrivateGameKeyValue.getValue());
                playerQuitPrivateGame = true;
                EventBus.getDefault().post(new PlayerQuitFromPrivateGameEvent(playerWasRemovedFromPrivateGame));
            } else if (message.equals("PrivateGameInfoByCodeReceived")) {
                PrivateGameInfo privateGameInfo = gson.fromJson(model, PrivateGameInfo.class);
                playerJoinedPrivateGameByCode = true;
                EventBus.getDefault().post(privateGameInfo);
            }


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

        }



    }


    private void fireNotification(String messageType, String message) {

        try
        {

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

                if (!OffsideApplication.isLobbyActivityVisible()) {

                    int titleResource = R.string.lbl_new_question_is_waiting_for_you;
                    int textResource = R.string.lbl_click_to_answer;
                    if (isCloseQuestion) {
                        titleResource = R.string.lbl_we_have_an_answer;
                        textResource = R.string.lbl_click_to_view;
                    }

                    Bitmap largeNotificationIcon = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo_10);

                    NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                            .setSmallIcon(R.mipmap.app_logo)
                            .setLargeIcon(largeNotificationIcon)
                            .setContentTitle(getString(titleResource))
                            .setDefaults(NotificationCompat.DEFAULT_ALL)
                            .setContentText(getString(textResource))
                            .setPriority(NotificationCompat.PRIORITY_HIGH)
                            .setOnlyAlertOnce(true)
                            .setColor(Color.BLUE);


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

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

        }



    }

    private void sendToServer(final String json, final String method) {


        new Thread(new Runnable() {
            @Override
            public void run() {

                try {

                    if (connection == null || !connection.isOpen() || channel == null || !channel.isOpen())
                        return;
                    channel.exchangeDeclare(CLIENT_REQUESTS_EXCHANGE_NAME, "fanout");
                    //channel.queueDeclare(CLIENT_REQUESTS_EXCHANGE_NAME, false, false, false, null);
                    channel.basicPublish(CLIENT_REQUESTS_EXCHANGE_NAME, "", null, json.getBytes("UTF-8"));
                    if (method != null) {
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (!availableGameReceived)
                                    EventBus.getDefault().post(new NetworkingErrorEvent("Request"));
                            }
                        }, sendToServerErrorDelay);
                    }


                } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                }

            }
        }).start();


    }

    public void requestQuitFromPrivateGame(String playerId, String gameId, String privateGameId, String androidDeviceId) {

        Map<String, String> params = new HashMap<>();
        params.put("method", "RequestQuitPrivateGame");
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        params.put("androidDeviceId", androidDeviceId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, null);
    }

    public void requestAvailableGame(String playerId, String gameId, String privateGameId) {
        availableGameReceived = false;
        String method = "RequestAvailableGame";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestCreatePrivateGame(String playerId, String gameId, String groupId, String selectedLanguage) {
        privateGameCreated = false;
        String method = "RequestCreatePrivateGame";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("groupId", groupId);
        params.put("selectedLanguage", selectedLanguage);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestPostAnswer(final String playerId, final String gameId, final String privateGameId, final String questionId, final String answerId, final boolean isSkipped, final int betSize) {
        answerAccepted = false;
        String method = "RequestPostAnswer";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        params.put("questionId", questionId);
        params.put("answerId", answerId);
        params.put("isSkipped", Boolean.toString(isSkipped));
        params.put("betSize", Integer.toString(betSize));
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

//    public void requestGetScoreboard(String playerId, String gameId) {
//        scoreboardReceived = false;
//        String method = "RequestGetScoreboard";
//        Map<String, String> params = new HashMap<>();
//        params.put("method", method);
//        params.put("playerId", playerId);
//        params.put("gameId", gameId);
//        Gson gson = new GsonBuilder().create();
//        String json = gson.toJson(params);
//        sendToServer(json, method);
//    }

    public void requestGetChatMessages(String playerId, String gameId, String privateGameId, String androidDeviceId) {
        chatMessagesReceived = false;
        String method = "RequestGetChatMessages";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        params.put("androidDeviceId", androidDeviceId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestSendChatMessage(String playerId, String gameId, String privateGameId, String message) {
        chatMessageReceived = false;
        String method = "RequestSendChatMessage";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        params.put("message", message);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestSaveLoggedInUser(String playerId, String name, String email, String imageUrl, String playerColor) {
        loggedInUserReceived = false;
        String method = "RequestSaveLoggedInUser";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("name", name);
        params.put("email", email);
        params.put("imageUrl", imageUrl);
        params.put("playerColor", playerColor);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);

    }

    public void setPowerItems(String playerId, String gameId, int powerItems, boolean isDueToRewardVideo) {

        //TODO: NOT IN USE - do we need it?

        //loggedInUserReceived = false;
        String method = "RequestSetPowerItems";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("powerItems", Integer.toString(powerItems));
        params.put("isDueToRewardVideo", Boolean.toString(isDueToRewardVideo));
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestSaveImageInDatabase(String playerId, String imageString) {
        playerImageSaved = false;
        String method = "RequestSaveImageInDatabase";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("imageString", imageString);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestPrivateGroupsInfo(String playerId) {
        privateGroupsReceived = false;
        String method = "RequestPrivateGroupsInfo";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestAvailableGames(String playerId, String groupId) {
        availableGamesReceived = false;
        String method = "RequestAvailableGames";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupId", groupId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestCreatePrivateGroup(String playerId, String groupName, String groupType) {
        privateGroupCreated = false;
        String method = "RequestCreatePrivateGroup";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupName", groupName);
        params.put("groupType", groupType);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestJoinPrivateGame(String playerId, String gameId, String groupId, String privateGameId, String androidDeviceId) {
        playerJoinedPrivateGame = false;
        String method = "RequestJoinPrivateGame";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("gameId", gameId);
        params.put("groupId", groupId);
        params.put("privateGameId", privateGameId);
        params.put("androidDeviceId", androidDeviceId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestUserProfileData(String playerId) {
        userProfileInfoReceived = false;
        String method = "RequestUserProfile";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestLeagueRecords(String playerId, String groupId) {
        leagueRecordsReceived = false;
        String method = "RequestLeagueRecords";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupId", groupId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestPlayerAssets(String playerId) {
        playerAssetsReceived = false;
        String method = "RequestPlayerAssets";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestInviteFriend(String inviterPlayerId, String groupId, String gameId, String privateGameId) {
        friendInviteReceived = false;
        String method = "RequestInviteFriend";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("inviterPlayerId", inviterPlayerId);
        params.put("groupId", groupId);
        params.put("gameId", gameId);
        params.put("privateGameId", privateGameId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestPrivateGroup(String playerId, String groupId) {
        privateGroupReceived = false;
        String method = "RequestPrivateGroup";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupId", groupId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestDeletePrivateGroup(String playerId, String groupId) {
        privateGroupDeleted = false;
        String method = "RequestDeletePrivateGroup";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupId", groupId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestJoinPrivateGroup(String playerId, String groupId) {
        playerJoinPrivateGroupReceived = false;
        String method = "RequestJoinPrivateGroup";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("groupId", groupId);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void requestToRewardPlayer(String playerId, String rewardType, String rewardReason, int quantity) {

        playerRewardSaved = false;
        String method = "RequestToRewardPlayer";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("rewardType", rewardType);
        params.put("rewardReason", rewardReason);
        params.put("quantity", Integer.toString(quantity));
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }

    public void RequestPrivateGameInfoByCode(String playerId, String privateGameCode) {
        playerJoinedPrivateGameByCode = false;
        String method = "RequestPrivateGameInfoByCode";
        Map<String, String> params = new HashMap<>();
        params.put("method", method);
        params.put("playerId", playerId);
        params.put("code", privateGameCode);
        Gson gson = new GsonBuilder().create();
        String json = gson.toJson(params);
        sendToServer(json, method);
    }


    //</editor-fold>

    //<editor-fold desc="support classes">

    /**
     * Class used for the client Binder.  Because we know this service always
     * runs in the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public NetworkingService getService() {
            // Return this instance of SignalRService so clients can call public methods
            return NetworkingService.this;
        }
    }

    //</editor-fold>


}

