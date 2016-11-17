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
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.offsidegame.offside.helpers.SignalRService;
import com.offsidegame.offside.models.LoginEvent;
import com.offsidegame.offside.models.PlayerScore;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginActivity extends AppCompatActivity {

    private final Context mContext = this;
    private SignalRService mService;
    private boolean mBound = false;

    private final ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            // We've bound to SignalRService, cast the IBinder and get SignalRService instance
            SignalRService.LocalBinder binder = (SignalRService.LocalBinder) service;
            mService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            mBound = false;
        }
    };

    EditText email;
    Button login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Intent intent = new Intent();
        intent.setClass(mContext, SignalRService.class);
        bindService(intent, mConnection, Context.BIND_AUTO_CREATE);



        email = (EditText) findViewById(R.id.email);
        login = (Button) findViewById(R.id.login_button);
        login.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mBound){
                    mService.login(email.getText().toString());
                }
            }

        });
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(mContext);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(mContext);
        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }

        super.onStop();
    }



    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onLogin(LoginEvent loginEvent) {
        String id = loginEvent.getId();
        SharedPreferences settings = getSharedPreferences(getString(R.string.preference_name), 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(getString(R.string.is_logged_in_key), true);
        editor.putString(getString(R.string.user_id_key), id);
        Date current = new Date();
        String currentAsString = new SimpleDateFormat(getString(R.string.date_format)).format(current);
        editor.putString(getString(R.string.last_login_time), currentAsString);
        editor.commit();

        Intent intent = new Intent(mContext, PlayerScoreActivity.class);
        startActivity(intent);




    }


}
