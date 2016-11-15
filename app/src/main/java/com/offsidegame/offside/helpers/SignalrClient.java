package com.offsidegame.offside.helpers;

import android.util.Log;

import com.offsidegame.offside.models.PlayerScore;

import org.greenrobot.eventbus.EventBus;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.Action;
import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

/**
 * Created by KFIR on 11/15/2016.
 */

public class SignalrClient {

    public static final SignalrClient instance = new SignalrClient();

    private HubProxy hub = null;
    private final String url = "http://offside.somee.com";
    private final String hubName = "OffsideHub";
    private final String logTag = "signalr";
    private boolean isConnected = false;








    public SignalrClient(){
        start();
    }

    private void start() {
        Platform.loadPlatformComponent(new AndroidPlatformComponent());
        HubConnection connection = new HubConnection(url);
        hub = connection.createHubProxy(hubName);
        connection.error(new ErrorCallback() {
            @Override
            public void onError(Throwable error) {
                Log.e(logTag, "There was an error communicating with the server.");
                Log.e(logTag, "Error detail: " + error.toString());
                System.err.println();
                error.printStackTrace(System.err);
            }
        });

        connection.connected(new Runnable() {
            @Override
            public void run() {
                isConnected = true;
            }
        });

        hub.subscribe(new Object() {
            @SuppressWarnings("unused")
            public void SendNotification(String notification) {
                System.err.println("OFFSIDE NOTIFICATION IS: " + notification);
            }


        });

        SignalRFuture<Void> awaitConnection = connection.start();
        try {
            awaitConnection.get();
        } catch (InterruptedException e) {
            Log.e(logTag, "Error detail: " + e.getMessage().toString());
            System.err.println();
            isConnected = false;
        } catch (ExecutionException e) {
            Log.e(logTag, "Error detail: " + e.getMessage().toString());
            isConnected = false;
        }
    }

    public void getPlayerScore(){

        hub.invoke(PlayerScore.class, "GetPlayerScoreJava").done(new Action<PlayerScore>() {

            @Override
            public void run(PlayerScore playerScore) throws Exception {
                EventBus.getDefault().post(playerScore);
            }
        });
    }

}

