package com.offsidegame.offside.helpers;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.util.ArrayMap;


import com.offsidegame.offside.R;
import com.offsidegame.offside.activities.JoinGameActivity;
import com.offsidegame.offside.activities.LoginActivity;
import java.net.Inet4Address;
import java.util.Random;
import java.util.UUID;

//import com.google.firebase.messaging.FirebaseMessagingService;
//import com.google.firebase.messaging.RemoteMessage;


/**
 * Created by user on 12/26/2016.
 */

//public class OffsideFirebaseMessagingService extends FirebaseMessagingService {
//
//    @Override
//    public void onMessageReceived(RemoteMessage remoteMessage) {
//        super.onMessageReceived(remoteMessage);
//
//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
//
//        String notificationBody="";
//        String notificationTitle= "";
//        String gameCode ="";
//        if (remoteMessage.getData().size() > 0) {
//            ArrayMap<String, String> data = (ArrayMap<String, String>) remoteMessage.getData();
//            notificationTitle = data.get("title").toString();
//            notificationBody = data.get("body").toString();
//            gameCode = remoteMessage.getData().get("gameCode").toString();
//
//            Intent intent = new Intent(this, LoginActivity.class);
//            intent.putExtra(getString(R.string.game_code_key),gameCode);
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//            PendingIntent pendingIntent= PendingIntent.getActivity(this,0,intent,PendingIntent.FLAG_ONE_SHOT);
//            //PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, Intent.FILL_IN_ACTION);
//
//            notificationBuilder.setContentTitle(notificationTitle);
//            notificationBuilder.setContentText(notificationBody);
//            notificationBuilder.setAutoCancel(true);
//            notificationBuilder.setSmallIcon(R.mipmap.ic_offside_logo);
//
//            notificationBuilder.setContentIntent(pendingIntent);
//
//            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//            Random r = new Random();
//            int notificationId = r.nextInt(100000 - 1) + 1;
//            notificationManager.notify(notificationId, notificationBuilder.build());
//        }
//
//    }
//}
