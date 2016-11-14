package com.offsidegame.offside;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

import microsoft.aspnet.signalr.client.ErrorCallback;
import microsoft.aspnet.signalr.client.Platform;
import microsoft.aspnet.signalr.client.SignalRFuture;
import microsoft.aspnet.signalr.client.http.android.AndroidPlatformComponent;
import microsoft.aspnet.signalr.client.hubs.HubConnection;
import microsoft.aspnet.signalr.client.hubs.HubProxy;

public class PlayerScoreActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_score);
        Platform.loadPlatformComponent( new AndroidPlatformComponent() );
        HubConnection connection = new HubConnection("http://offside.somee.com");
        final HubProxy proxy = connection.createHubProxy("OffsideHub");
        connection.error(new ErrorCallback() {

            @Override
            public void onError(Throwable error) {
                System.err.println("There was an error communicating with the server.");
                System.err.println("Error detail: " + error.toString());

                error.printStackTrace(System.err);
            }
        });

//        connection.connected(new Runnable() {
//
//            @Override
//            public void run() {
//                Toast toast = Toast.makeText(getApplicationContext(), "connected", Toast.LENGTH_LONG);
//            }
//        });

        proxy.subscribe(new Object() {
            @SuppressWarnings("unused")
            public void SendNotification(String notification) {
                System.err.println("OFFSIDE NOTIFICATION IS: " + notification);
            }


        });

        SignalRFuture<Void> awaitConnection = connection.start();
        try {
            awaitConnection.get();
        } catch (InterruptedException e) {
            System.err.println("Error detail: " + e.getMessage().toString());
        } catch (ExecutionException e) {
            System.err.println("Error detail: " + e.getMessage().toString());
        }
    }


}
