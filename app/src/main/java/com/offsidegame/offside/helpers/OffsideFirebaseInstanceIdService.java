package com.offsidegame.offside.helpers;

import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;
import com.offsidegame.offside.R;

/**
 * Created by user on 12/26/2016.
 */

public class OffsideFirebaseInstanceIdService extends FirebaseInstanceIdService {


    private final static String REG_TOKEN = "REG_TOKEN";


    @Override
    public void onTokenRefresh() {
        //super.onTokenRefresh();
        String recentToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(REG_TOKEN,recentToken);

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(getString(R.string.recent_token_key), recentToken);
        editor.commit();

    }




}
