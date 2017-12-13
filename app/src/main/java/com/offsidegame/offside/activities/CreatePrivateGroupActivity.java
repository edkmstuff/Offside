package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.events.ConnectionEvent;
import com.offsidegame.offside.events.LoadingEvent;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.events.NetworkingServiceBoundEvent;
import com.offsidegame.offside.events.PrivateGroupCreatedEvent;
import com.offsidegame.offside.helpers.AnimationHelper;
import com.offsidegame.offside.models.OffsideApplication;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class CreatePrivateGroupActivity extends AppCompatActivity {
    private final String activityName = "LobbyActivity";
    private final Context context = this;
    private LinearLayout createPrivateGroupRoot;
    private EditText privateGroupNameEditText;
    private TextView savePrivateGroupButtonTextView;
    private String playerDisplayName;
    private String playerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_private_group);

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            playerId = firebaseUser.getUid();

            if (OffsideApplication.getUserProfileInfo() == null)
                playerDisplayName = firebaseUser.getDisplayName();
            else
                playerDisplayName = OffsideApplication.getUserProfileInfo().getPlayerName();

            getIds();
            setEvents();


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }


    }

    private void getIds() {

        savePrivateGroupButtonTextView = findViewById(R.id.cpg_save_private_group_button_text_view);
        createPrivateGroupRoot = findViewById(R.id.cpg_create_private_group_root);
        privateGroupNameEditText = findViewById(R.id.cpg_private_group_name_edit_text);
    }

    private void setEvents() {

        privateGroupNameEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.toString().trim().length() == 0) {
                    savePrivateGroupButtonTextView.setEnabled(false);
                } else {
                    savePrivateGroupButtonTextView.setEnabled(true);
                }

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                if (s.toString().trim().length() == 0) {
                    savePrivateGroupButtonTextView.setEnabled(false);
                } else {
                    savePrivateGroupButtonTextView.setEnabled(true);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    savePrivateGroupButtonTextView.setEnabled(false);
                } else {
                    savePrivateGroupButtonTextView.setEnabled(true);
                }

            }
        });


        savePrivateGroupButtonTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int duration = 500;
                int delay = (int) (duration * 0.2);
                AnimationHelper.animateButtonClick(view, duration);

                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        String groupName = privateGroupNameEditText.getText().toString();
                        groupName = groupName.length() > 20 ? groupName.substring(0, 20) : groupName;

                        String groupType = getResources().getString(R.string.key_private_group_name);
                        //if (OffsideApplication.isBoundToNetworkingService)
                            OffsideApplication.networkingService.requestCreatePrivateGroup(playerId, groupName, groupType);
                        //else
                          //  throw new RuntimeException(activityName + " - generatePrivateGameCodeButtonTextView - onClick - Error: SignalRIsNotBound");

                        createPrivateGroupRoot.setVisibility(View.GONE);
                        String message = String.format("Creating %s...", groupName);
                        EventBus.getDefault().post(new LoadingEvent(true, message));

                    }
                }, delay);


            }
        });


    }


    @Override
    public void onResume() {
        try {
            super.onResume();
            savePrivateGroupButtonTextView.setEnabled(false);
            EventBus.getDefault().post(new NetworkingServiceBoundEvent(context));


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Override
    public void onStart() {
        try {
            super.onStart();
            EventBus.getDefault().register(context);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Override
    public void onStop() {
        try {

            EventBus.getDefault().unregister(context);
            super.onStop();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPrivateGroupCreated(PrivateGroupCreatedEvent privateGroupCreatedEvent) {
        try {

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    Intent intent = new Intent(context, LobbyActivity.class);
                    intent.putExtra("showGroups", true);
                    startActivity(intent);

                }
            }, 500);


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onNetworkingServiceBinding(NetworkingServiceBoundEvent networkingServiceBoundEvent) {
        try {
            if (OffsideApplication.networkingService == null)
                return;

            Context eventContext = networkingServiceBoundEvent.getContext();
            if (eventContext == context || eventContext == getApplicationContext()) {

//                if (OffsideApplication.isPlayerQuitGame()) {
//                    return;
//                }
                //if (OffsideApplication.isBoundToNetworkingService) {
                    EventBus.getDefault().post(new LoadingEvent(false, null));
                //} else
                  //  throw new RuntimeException(activityName + " - onNetworkingServiceBinding - Error: SignalRIsNotBound");
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

    @Override
    public void onBackPressed() {
        try
        {
            OffsideApplication.setIsBackFromCreatePrivateGroup(true);
            finish();

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

        }

    }
}
