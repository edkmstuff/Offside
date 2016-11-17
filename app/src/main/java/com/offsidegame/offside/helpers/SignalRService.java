package com.offsidegame.offside.helpers;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import com.offsidegame.offside.models.LoginEvent;
import com.offsidegame.offside.models.LoginInfo;
import com.offsidegame.offside.models.PlayerScore;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;
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

    public SignalRService() {
    }

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

    /**
     * method for clients (activities)
     */
    public void getPlayerScore() {

        hub.invoke(PlayerScore.class, "GetPlayerScoreJava").done(new Action<PlayerScore>() {

            @Override
            public void run(PlayerScore playerScore) throws Exception {
                EventBus.getDefault().post(playerScore);
            }
        });
    }

    public void login(String email) {

        hub.invoke(LoginInfo.class, "Login").done(new Action<LoginInfo>() {

            @Override
            public void run(LoginInfo loginInfo) throws Exception {
                EventBus.getDefault().post(new LoginEvent(loginInfo.getId()));
            }
        });
    }

    /**
     * method for clients (activities)
     */
//    public void sendMessage_To(String receiverName, String message) {
//        String SERVER_METHOD_SEND_TO = "SendChatMessage";
//        hubProxy.invoke(SERVER_METHOD_SEND_TO, receiverName, message);
//    }

    private void startSignalR() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());

        String serverUrl = "http://offsidedev.somee.com/";
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

        getPlayerScore();
    }



//        String CLIENT_METHOD_BROADAST_MESSAGE = "broadcastMessage";
//        hubProxy.on("UpdatePlayerScore",
//                new SubscriptionHandler1<PlayerScore>() {
//                    @Override
//                    public void run(final PlayerScore msg) {
//                        final String finalMsg = msg.UserName + " says " + msg.Message;
//                        // display Toast message
//                        handler.post(new Runnable() {
//                            @Override
//                            public void run() {
//                                Toast.makeText(getApplicationContext(), finalMsg, Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                }
//                , CustomMessage.class);


    }

