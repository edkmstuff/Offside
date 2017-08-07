package com.offsidegame.offside.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.PrivateGameGeneratedEvent;
import com.offsidegame.offside.events.SignalRServiceBoundEvent;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PrivateGroup;
import com.offsidegame.offside.models.PrivateGroupCreationInfo;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class CreatePrivateGroupActivity extends AppCompatActivity {


    //activity
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private final Activity thisActivity = this;
    private LinearLayout loadingRoot;

    //create private group form

    private LinearLayout createPrivateGroupRoot;
    private EditText privateGroupNameEditText;
    private TextView savePrivateGroupButtonTextView;


    private String playerDisplayName;
    private String playerId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_private_group);

        FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
        playerDisplayName = player.getDisplayName();
        playerId = player.getUid();

        savePrivateGroupButtonTextView = (TextView) findViewById(R.id.cpg_save_private_group_button_text_view);

        loadingRoot = (LinearLayout) findViewById(R.id.shared_loading_root);
        createPrivateGroupRoot = (LinearLayout) findViewById(R.id.cpg_create_private_group_root);


        privateGroupNameEditText = (EditText) findViewById(R.id.cpg_private_group_name_edit_text);

        privateGroupNameEditText.setText(playerDisplayName.split(" ")[0] + "'s" + " friends");
//
        savePrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get language
                String selectedLanguage = "Hebrew";

                String groupName = privateGroupNameEditText.getText().toString();
                groupName = groupName.length() > 20 ? groupName.substring(0, 20) : groupName;

                String groupType= getResources().getString(R.string.key_private_group_name);

                if (OffsideApplication.isBoundToSignalRService)
                    OffsideApplication.signalRService.RequestCreatePrivateGroup(groupName, groupType, playerId, selectedLanguage);
                else
                    throw new RuntimeException(activityName + " - generatePrivateGameCodeButtonTextView - onClick - Error: SignalRIsNotBound");

                createPrivateGroupRoot.setVisibility(View.GONE);
                createPrivateGroupRoot.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();
        EventBus.getDefault().post(new SignalRServiceBoundEvent(context));


    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(context);

    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(context);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGroupCreated(PrivateGroup privateGroup) {
        try {


            OffsideApplication.getPrivateGroupsInfo().getPrivateGroups().add(privateGroup);

            Intent intent = new Intent(context,LobbyActivity.class);
            startActivity(intent);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }

    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onSignalRServiceBinding(SignalRServiceBoundEvent signalRServiceBoundEvent) {
        try {
            if (OffsideApplication.signalRService == null)
                return;

            Context eventContext = signalRServiceBoundEvent.getContext();
            if (eventContext == context || eventContext == getApplicationContext()) {

                if (OffsideApplication.isPlayerQuitGame()) {
                    loadingRoot.setVisibility(View.GONE);

                    return;
                }


                if (OffsideApplication.isBoundToSignalRService) {
                    loadingRoot.setVisibility(View.GONE);

                } else
                    throw new RuntimeException(activityName + " - onSignalRServiceBinding - Error: SignalRIsNotBound");


            }

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onConnectionEvent(ConnectionEvent connectionEvent) {
        try {
            boolean isConnected = connectionEvent.getConnected();
            if (isConnected)
                Toast.makeText(context, R.string.lbl_you_are_connected, Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(context, R.string.lbl_you_are_disconnected, Toast.LENGTH_SHORT).show();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }
}
