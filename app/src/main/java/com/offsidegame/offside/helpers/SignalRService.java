package com.offsidegame.offside.helpers;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ActiveGameEvent;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.IsAnswerAcceptedEvent;
import com.offsidegame.offside.events.JoinGameEvent;
import com.offsidegame.offside.events.LoginEvent;
import com.offsidegame.offside.events.QuestionsEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.GameInfo;
import com.offsidegame.offside.models.LoginInfo;
import com.offsidegame.offside.models.PlayerScore;
import com.offsidegame.offside.events.PlayerScoreEvent;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.events.QuestionEvent;
import com.offsidegame.offside.models.Scoreboard;
import com.offsidegame.offside.events.ScoreboardEvent;
import com.offsidegame.offside.models.User;

import org.greenrobot.eventbus.EventBus;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ConnectionState;
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
    private HubConnection hubConnection;
    private HubProxy hub;
    //private Handler handler; // to display Toast message
    private final IBinder binder = new LocalBinder(); // Binder given to clients
    private Date startReconnectiong = null;

    public final String ip = new String("192.168.1.140:8080");
    //public final String ip = new String("10.0.0.17:8080");
    //public final String ip = new String("offside.somee.com");


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
        hubConnection.stop();
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // Return the communication channel to the service.
        startSignalR(false);
        return binder;
    }

    private void startSignalR(Boolean notifyWhenConnected) {
        try {
            Platform.loadPlatformComponent(new AndroidPlatformComponent());
            final String serverUrl = "http://" + ip;
            hubConnection = new HubConnection(serverUrl);
            final String SERVER_HUB = "OffsideHub";
            hub = hubConnection.createHubProxy(SERVER_HUB);
            ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());

            hubConnection.stateChanged(new StateChangedCallback() {

                @Override
                public void stateChanged(ConnectionState oldState, ConnectionState newState) {
                    try {
                        if (newState == ConnectionState.Disconnected) {
                            EventBus.getDefault().post(new ConnectionEvent(false, "inner try 1"));
                            //try reconnection for 10 min
                            if (startReconnectiong == null) {
                                startReconnectiong = new Date();
                            } else
                                return; // we are already trying to reconnect
                            Date now = new Date();
                            while (startReconnectiong != null && now.getTime() - startReconnectiong.getTime() < 10 * 60 * 1000
                                    && hubConnection.getState() == ConnectionState.Disconnected) {
                                try {
                                    startSignalR(true);
                                    now = new Date();
                                    Thread.sleep(20000);

                                } catch (Exception ex) {
                                    EventBus.getDefault().post(new ConnectionEvent(true, ex.getMessage()));
                                    return;
                                }
                            }
                            if (hubConnection.getState() == ConnectionState.Connected) {
                                EventBus.getDefault().post(new ConnectionEvent(true, "inner try 2"));
                            }
                        }
                    }
                    catch(Exception e1){
                        EventBus.getDefault().post(new ConnectionEvent(false, e1.getMessage()));
                    }

                }
            });


            SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

            try {
                signalRFuture.get();
            } catch (InterruptedException | ExecutionException e) {
                Log.e("SimpleSignalR", e.getMessage());
                //return;
            }

            if (notifyWhenConnected && hubConnection.getState() == ConnectionState.Connected) {
                EventBus.getDefault().post(new SignalRServiceBoundEvent(null));
                startReconnectiong = null;
            }

            if (hubConnection.getState() == ConnectionState.Connected)
                subscribeToServer();
        }
        catch (Exception e){
            EventBus.getDefault().post(new ConnectionEvent(false,e.toString()));
        }
    }

    //</editor-fold>

    //<editor-fold desc="subscribeToServer">
    public void subscribeToServer() {
        hub.on("AskBatchedQuestions", new SubscriptionHandler1<Question[]>() {
            @Override
            public void run(Question[] batchedQuestions) {
                EventBus.getDefault().post(new QuestionEvent(batchedQuestions, QuestionEvent.QuestionStates.NEW_QUESTION));
            }
        }, Question[].class);
        hub.on("AskQuestion", new SubscriptionHandler1<Question>() {
            @Override
            public void run(Question question) {
                EventBus.getDefault().post(new QuestionEvent(question, QuestionEvent.QuestionStates.NEW_QUESTION));
            }
        }, Question.class);

        hub.on("SendProcessedQuestion", new SubscriptionHandler1<Question>() {
            @Override
            public void run(Question question) {
                EventBus.getDefault().post(new QuestionEvent(question, QuestionEvent.QuestionStates.PROCESSED_QUESTION));
            }
        }, Question.class);

        hub.on("CloseQuestion", new SubscriptionHandler1<Question>() {
            @Override
            public void run(Question question) {
                EventBus.getDefault().post(new QuestionEvent(question, QuestionEvent.QuestionStates.CLOSED_QUESTION));
            }
        }, Question.class);

        hub.on("UpdatePlayerScore", new SubscriptionHandler1<String>() {
            @Override
            public void run(String gameId) {
                SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
                String userGameId = settings.getString(getString(R.string.game_id_key), "");
                if (!userGameId.equals(gameId))
                    return;

                String userId = settings.getString(getString(R.string.user_id_key), "");
                String userName = settings.getString(getString(R.string.user_name_key), "");

                getPlayerScore(gameId, userId, userName);

            }
        }, String.class);


    }
    //</editor-fold>

    //<editor-fold desc="methods for client activities">

    public void login(String email) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        hub.invoke(LoginInfo.class, "LoginWithEmail", email).done(new Action<LoginInfo>() {

            @Override
            public void run(LoginInfo loginInfo) throws Exception {
                boolean isFacebookLogin = false;
                String password = loginInfo.getPassword();
                EventBus.getDefault().post(new LoginEvent(loginInfo.getId(), loginInfo.getName(), password, isFacebookLogin));
            }
        });
    }

    public void joinGame(String gameCode) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String userId = settings.getString(getString(R.string.user_id_key), "");
        String userName = settings.getString(getString(R.string.user_name_key), "");
        String imageUrl = settings.getString(getString(R.string.user_profile_picture_url_key), "");

        hub.invoke(GameInfo.class, "JoinGame", gameCode, userId, userName, imageUrl).done(new Action<GameInfo>() {

            @Override
            public void run(GameInfo gameInfo) throws Exception {
                EventBus.getDefault().post(new JoinGameEvent(gameInfo));
            }
        });
    }

    public void isGameActive(String gameId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        hub.invoke(Boolean.class, "IsGameActive", gameId).done(new Action<Boolean>() {

            @Override
            public void run(Boolean isGameActive) throws Exception {
                EventBus.getDefault().post(new ActiveGameEvent(isGameActive));
            }
        });
    }

    public void getPlayerScore(String gameId, String userId, String userName) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String imageUrl = settings.getString(getString(R.string.user_profile_picture_url_key), "");
        hub.invoke(PlayerScore.class, "GetPlayerScore", gameId, userId, userName, imageUrl).done(new Action<PlayerScore>() {

            @Override
            public void run(PlayerScore playerScore) throws Exception {
                EventBus.getDefault().post(new PlayerScoreEvent(playerScore));
            }
        });
    }


    public void postAnswer(String gameId, String questionId, String answerId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        hub.invoke(Boolean.class, "PostAnswer", gameId, questionId, answerId).done(new Action<Boolean>() {
            @Override
            public void run(Boolean isAnswerAccepted) throws Exception {
                EventBus.getDefault().post(new IsAnswerAcceptedEvent(isAnswerAccepted));
            }

        });

    }

    public void getScoreboard(String gameId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        hub.invoke(Scoreboard.class, "GetScoreboard", gameId).done(new Action<Scoreboard>() {

            @Override
            public void run(Scoreboard scoreboard) throws Exception {
                EventBus.getDefault().post(new ScoreboardEvent(scoreboard));
            }
        });
    }

    public void getQuestions(String gameId) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;
        hub.invoke(Question[].class, "GetQuestions", gameId).done(new Action<Question[]>() {

            @Override
            public void run(Question[] questions) throws Exception {
                EventBus.getDefault().post(new QuestionsEvent(questions));
            }
        });
    }

    public void saveUser(User user) {
        if (!(hubConnection.getState() == ConnectionState.Connected))
            return;

        hub.invoke(Boolean.class, "SaveUser", user.getId(), user.getName(), user.getEmail(), user.getProfilePictureUri(), user.getPassword(),user.getDeviceToken()).done(new Action<Boolean>() {
            @Override
            public void run(Boolean isUserSaved) throws Exception {
                EventBus.getDefault().post(new IsAnswerAcceptedEvent(isUserSaved));
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

