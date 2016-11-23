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
import com.offsidegame.offside.models.ActiveGameEvent;
import com.offsidegame.offside.models.GameCreationEvent;
import com.offsidegame.offside.models.IsAnswerAcceptedEvent;
import com.offsidegame.offside.models.JoinGameEvent;
import com.offsidegame.offside.models.LoginEvent;
import com.offsidegame.offside.models.LoginInfo;
import com.offsidegame.offside.models.PlayerScore;
import com.offsidegame.offside.models.PlayerScoreEvent;
import com.offsidegame.offside.models.Question;
import com.offsidegame.offside.models.QuestionEvent;

import org.greenrobot.eventbus.EventBus;

import java.net.InetAddress;
import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
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
    private Handler handler; // to display Toast message
    private final IBinder binder = new LocalBinder(); // Binder given to clients


    //<editor-fold desc="constructors">
    public SignalRService() {
    }

    //</editor-fold>

    //<editor-fold desc="startup">
    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int result = super.onStartCommand(intent, flags, startId);
        startSignalR();
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
        startSignalR();
        return binder;
    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        String ip;

        try {
            ip = InetAddress.getLocalHost().toString();
        } catch (Exception ex) {
            ip = "192.168.1.140";
            //ip = "10.0.0.41";
        }
        String serverUrl = "http://" + ip + ":8080/";
        hubConnection = new HubConnection(serverUrl);
        String SERVER_HUB = "OffsideHub";
        hub = hubConnection.createHubProxy(SERVER_HUB);
        ClientTransport clientTransport = new ServerSentEventsTransport(hubConnection.getLogger());
        SignalRFuture<Void> signalRFuture = hubConnection.start(clientTransport);

        try {
            signalRFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            Log.e("SimpleSignalR", e.toString());
            return;
        }

        subscribeToServer();
    }

    //</editor-fold>

    //<editor-fold desc="Admin methods">
    public void login(String email) {

        hub.invoke(LoginInfo.class, "Login", email).done(new Action<LoginInfo>() {

            @Override
            public void run(LoginInfo loginInfo) throws Exception {
                EventBus.getDefault().post(new LoginEvent(loginInfo.getId(), loginInfo.getName()));
            }
        });
    }

    public void joinGame(String gameCode) {
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        String userId = settings.getString(getString(R.string.user_id_key), "");
        String userName = settings.getString(getString(R.string.user_name_key), "");

        hub.invoke(String.class, "JoinGame", gameCode, userId, userName).done(new Action<String>() {

            @Override
            public void run(String gameId) throws Exception {
                EventBus.getDefault().post(new JoinGameEvent(gameId));
            }
        });
    }

    public void isGameActive(String gameId) {

        hub.invoke(Boolean.class, "IsGameActive", gameId).done(new Action<Boolean>() {

            @Override
            public void run(Boolean isGameActive) throws Exception {
                EventBus.getDefault().post(new ActiveGameEvent(isGameActive));
            }
        });
    }

    public void adminAskQuestion(Question question) {
        hub.invoke(Question.class, "AdminAskQuestion", question);
    }

    public void adminCreateGame(String gameCode, String homeTeam, String visitorTeam) {
        hub.invoke(String.class, "AdminCreateGame", gameCode, homeTeam, visitorTeam).done(new Action<String>() {

            @Override
            public void run(String gameCode) throws Exception {
                EventBus.getDefault().post(new GameCreationEvent(gameCode));
            }
        });
    }


    //</editor-fold>

    //<editor-fold desc="methods for client activities">

    public void getPlayerScore(String gameId, String userId, String userName) {

        hub.invoke(PlayerScore.class, "GetPlayerScore", gameId, userId, userName).done(new Action<PlayerScore>() {

            @Override
            public void run(PlayerScore playerScore) throws Exception {
                EventBus.getDefault().post(new PlayerScoreEvent(playerScore));
            }
        });
    }


    public void subscribeToServer() {
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
    }

    public void postAnswer(String gameId,String questionId,String answerId) {

        hub.invoke(Boolean.class, "PostAnswer", gameId,questionId,answerId).done(new Action<Boolean>(){
            @Override
            public void run(Boolean isAnswerAccepted) throws Exception {
                EventBus.getDefault().post(new IsAnswerAcceptedEvent(isAnswerAccepted));
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

