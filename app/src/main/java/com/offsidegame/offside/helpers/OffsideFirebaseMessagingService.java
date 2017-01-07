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
        Object[] notificationContentArray = remoteMessage.getData().values().toArray();
        String notificationBody="";
        String notificationTitle= "";
        if (remoteMessage.getData().size() > 0) {
        //if(notificationContentArray.length>0) {
            //todo: replace with foreach
            notificationBody = String.valueOf(notificationContentArray[0]);
            notificationTitle = String.valueOf(notificationContentArray[1]);
        }

        else {
            notificationBody= remoteMessage.getNotification().getBody().toString();
            notificationTitle= "Manual Notification";
        }
        notificationBuilder.setContentTitle(notificationTitle);
        notificationBuilder.setContentText(notificationBody);
        notificationBuilder.setAutoCancel(true);
        notificationBuilder.setSmallIcon(R.mipmap.ic_offside_logo);
        notificationBuilder.setContentIntent(pendingIntent);
        NotificationManager notificationManager =  (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0,notificationBuilder.build());

    }
}
