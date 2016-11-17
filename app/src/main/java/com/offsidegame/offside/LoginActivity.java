package com.offsidegame.offside;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.LoginEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private final Context context = this;
    private SignalRService signalRService;
    private boolean isBoundToSignalRService = false;

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

    EditText email;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = new Intent();
        intent.setClass(context, SignalRService.class);
        bindService(intent, signalRServiceConnection, Context.BIND_AUTO_CREATE);

        email = (EditText) findViewById(R.id.email);
        login = (Button) findViewById(R.id.login_button);
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
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(getString(R.string.is_logged_in_key), true);
        editor.putString(getString(R.string.user_id_key), id);
        editor.putString(getString(R.string.user_name_key), name);
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR_OF_DAY, 3);
        Date expirationTime = cal.getTime();
        String currentAsString = new SimpleDateFormat(getString(R.string.date_format)).format(expirationTime);
        editor.putString(getString(R.string.login_expiration_time), currentAsString);
        editor.commit();

        Intent intent = new Intent(context, JoinGameActivity.class);
        startActivity(intent);




    }


}
