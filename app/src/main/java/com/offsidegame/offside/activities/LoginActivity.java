package com.offsidegame.offside.activities;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.DateHelper;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.Serializable;
import java.util.Date;

//import static com.offsidegame.offside.R.string.fbProfile_key;

public class LoginActivity extends AppCompatActivity implements Serializable{

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;
    EditText email;
    Button login;

    private Toolbar toolbar;

    private final ServiceConnection signalRServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            signalRService = binder.getService();
            isBoundToSignalRService = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName className) {
            isBoundToSignalRService = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(getApplication());

        toolbar = (Toolbar) findViewById((R.id.app_bar));
        setSupportActionBar(toolbar);

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        boolean isLoggedIn = settings.getBoolean(getString(R.string.is_logged_in_key), false);

        String loginExpirationTimeAsString = (String) settings.getString(getString(R.string.login_expiration_time_key), "");

        DateHelper dateHelper = new DateHelper();
        Date loginExpirationTime = dateHelper.formatAsDate(loginExpirationTimeAsString, context);
        if (loginExpirationTime == null)
            loginExpirationTime = dateHelper.getCurrentDate();

        Date current = dateHelper.getCurrentDate();
        if (isLoggedIn /*|| current.after(loginExpirationTime)*/ ) {
            Intent intent = new Intent(context, JoinGameActivity.class);
            startActivity(intent);
            return;
        }

        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

        email = (EditText) findViewById(R.id.email);
        login = (Button) findViewById(R.id.email_login_button);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (isBoundToSignalRService){
                    signalRService.login(email.getText().toString());
                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(context);
        // Unbind from the service
        if (isBoundToSignalRService) {
            unbindService(signalRServiceConnection);
            isBoundToSignalRService = false;
        }

        super.onStop();
    }





    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {
        String id = loginEvent.getId();
        String name = loginEvent.getName();

        /*Profile profile = loginEvent.getFbProfile();
        FacebookLoginInfo fbLoginInfo = new FacebookLoginInfo(profile.getFirstName(),profile.getLastName(),profile.getName(),profile.getId(),profile.getLinkUri().toString());
        Gson gson = new Gson();
        String fbProfileJson = gson.toJson(fbLoginInfo);*/

        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(getString(R.string.is_logged_in_key), true);
        editor.putString(getString(R.string.user_id_key), id);
        editor.putString(getString(R.string.user_name_key), name);
//        editor.putString(getString(fbProfile_key), fbProfileJson);

        DateHelper dateHelper = new DateHelper();
        Date current = dateHelper.getCurrentDate();
        Date expirationTime = dateHelper.addHours(current, 3);

        String expirationTimeAsString = dateHelper.formatAsString(expirationTime, context);
        editor.putString(getString(R.string.login_expiration_time_key), expirationTimeAsString);
        editor.commit();

        Intent intent = new Intent(context, JoinGameActivity.class);

        startActivity(intent);

    }


}
