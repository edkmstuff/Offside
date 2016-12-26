package com.offsidegame.offside.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;


import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.LoginActivity;
import java.net.Inet4Address;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by user on 12/26/2016.
 */

public class OffsideFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);


        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
        String notificationTitle= String.valueOf(remoteMessage.getData().values().toArray()[1]);
        String notificationBody = String.valueOf(remoteMessage.getData().values().toArray()[0]);

        notificationBuilder.setContentTitle(notificationTitle);
        notificationBuilder.setContentText(notificationBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_offside_logo);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());




    }
}
