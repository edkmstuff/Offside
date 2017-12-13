package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.Wheel;
import com.offsidegame.offside.models.OffsideApplication;
import com.offsidegame.offside.models.PlayerModel;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Random;

public class SlotActivity extends AppCompatActivity {

    private Context context = this;

    private TextView msg;
    private ImageView img1, img2, img3;
    private Wheel wheel1, wheel2, wheel3;
    private Button startSlotButton;
    private Button goBackToChatButton;
    private boolean isStarted;
    private int frameDuration = 200;
    private int maxWaitBeforeWheelStartRolling = 500;
    private int timeToStopSlotMachine = 5000;

    public static final Random RANDOM = new Random();

    public static long randomLong(long lower, long upper) {
        long rnd = lower + (long) (RANDOM.nextDouble() * (upper - lower));
        return rnd;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slot);

        img1 = (ImageView) findViewById(R.id.img1);
        img2 = (ImageView) findViewById(R.id.img2);
        img3 = (ImageView) findViewById(R.id.img3);
        startSlotButton = (Button) findViewById(R.id.s_start_slot_button);
        goBackToChatButton = (Button) findViewById(R.id.s_go_back_to_chat_button);

        goBackToChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(context,LobbyActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        }


        );

        goBackToChatButton.setVisibility(View.GONE);

        startSlotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isStarted) {
                    stopSlot();

                } else {
                    goBackToChatButton.setVisibility(View.GONE);

                    wheel1 = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    img1.setImageResource(img);
                                }
                            });
                        }
                    }, frameDuration, randomLong(0, maxWaitBeforeWheelStartRolling));

                    wheel1.start();

                    wheel2 = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    img2.setImageResource(img);
                                }
                            });
                        }
                    }, frameDuration, randomLong(0, maxWaitBeforeWheelStartRolling));

                    wheel2.start();

                    wheel3 = new Wheel(new Wheel.WheelListener() {
                        @Override
                        public void newImage(final int img) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    img3.setImageResource(img);
                                }
                            });
                        }
                    }, frameDuration, randomLong(0, maxWaitBeforeWheelStartRolling));

                    wheel3.start();

                    startSlotButton.setVisibility(View.GONE);
                    isStarted = true;


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopSlot();
                        }
                    }, timeToStopSlotMachine);




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
        try {
            EventBus.getDefault().unregister(context);
            super.onStop();

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

    private void stopSlot() {
        wheel1.stopWheel();
        wheel2.stopWheel();
        wheel3.stopWheel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String command = null;

                if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex){
                    command = "!reload2PowerItem";
                    Toast.makeText(context, R.string.lbl_well_done_you_earned_2_power_items, Toast.LENGTH_LONG).show();
                }

                else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex || wheel1.currentIndex == wheel3.currentIndex) {
                    command = "!reload1PowerItem";
                    Toast.makeText(context, R.string.lbl_well_done_you_earned_1_power_items, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, R.string.lbl_you_didnt_earn_balls, Toast.LENGTH_LONG).show();

                startSlotButton.setText(R.string.lbl_try_again);
                startSlotButton.setVisibility(View.VISIBLE);
                goBackToChatButton.setVisibility(View.VISIBLE);
                isStarted = false;

                //if(OffsideApplication.isBoundToNetworkingService && command != null)
                //{
                    String gameId = OffsideApplication.getGameInfo().getGameId();
                    String gameCode = OffsideApplication.getGameInfo().getPrivateGameId();
                    FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
                    String playerId = player.getUid();
                    OffsideApplication.networkingService.requestSendChatMessage(playerId, gameId, gameCode, command );
                //}

            }
        }, frameDuration * 2);

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onReceivePlayer(PlayerModel player) {
        try {
            if (player == null)
                return;

            //OffsideApplication.setPlayer(player);
            OffsideApplication.getGameInfo().setPlayer(player);

        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);

        }

    }


}
