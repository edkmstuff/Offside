package com.offsidegame.offside.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
                Intent intent= new Intent(context,ChatActivity.class);
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

    private void stopSlot() {
        wheel1.stopWheel();
        wheel2.stopWheel();
        wheel3.stopWheel();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String command = null;

                if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex){
                    command = "!reload1000";
                    Toast.makeText(context, R.string.lbl_well_done_you_earned_1000_coins, Toast.LENGTH_LONG).show();
                }

                else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex || wheel1.currentIndex == wheel3.currentIndex) {
                    command = "!reload500";
                    Toast.makeText(context, R.string.lbl_well_done_you_earned_500_coins, Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(context, R.string.lbl_you_didnt_earn_coins, Toast.LENGTH_LONG).show();

                startSlotButton.setText(R.string.lbl_try_again);
                startSlotButton.setVisibility(View.VISIBLE);
                goBackToChatButton.setVisibility(View.VISIBLE);
                isStarted = false;

                if(OffsideApplication.isBoundToSignalRService && command != null)
                {
                    String gameId = OffsideApplication.getGameInfo().getGameId();
                    String gameCode = OffsideApplication.getGameInfo().getPrivateGameCode();
                    FirebaseUser player = FirebaseAuth.getInstance().getCurrentUser();
                    String playerId = player.getUid();
                    OffsideApplication.signalRService.sendChatMessage(gameId, gameCode, command, playerId);
                }

            }
        }, frameDuration * 2);



    }
}
