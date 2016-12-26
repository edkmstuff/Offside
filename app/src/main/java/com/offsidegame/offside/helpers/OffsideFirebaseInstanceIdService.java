package com.offsidegame.offside.helpers;

import android.util.Log;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

/**
 * Created by user on 12/26/2016.
 */

public class OffsideFirebaseInstanceIdService extends FirebaseInstanceIdService {

    private final static String REG_TOKEN = "REG_TOKEN";

    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        String recentToken = FirebaseInstanceId.getInstance().getToken();
        //TooDo: push the reg_token to the webserver
        Log.d(REG_TOKEN,recentToken);

    }


}
