package com.offsidegame.offside.activities;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.helpers.Wheel;

import java.util.Random;

public class SlotActivity extends AppCompatActivity {

    private Context context = this;

    private TextView msg;
    private ImageView img1, img2, img3;
    private Wheel wheel1, wheel2, wheel3;
    private Button btn;
    private boolean isStarted;

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
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
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
                    }, 500, randomLong(0, 5000));

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
                    }, 500, randomLong(0, 5000));

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
                    }, 500, randomLong(0, 5000));

                    wheel3.start();

                    btn.setVisibility(View.GONE);
                    isStarted = true;


                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            stopSlot();
                        }
                    }, 20000);




                }
            }
        });


    }

    private void stopSlot() {
        wheel1.stopWheel();
        wheel2.stopWheel();
        wheel3.stopWheel();

        if (wheel1.currentIndex == wheel2.currentIndex && wheel2.currentIndex == wheel3.currentIndex)
            Toast.makeText(context, "כל הכבוד! הרווחת 1000 מטבעות", Toast.LENGTH_LONG).show();
        else if (wheel1.currentIndex == wheel2.currentIndex || wheel2.currentIndex == wheel3.currentIndex || wheel1.currentIndex == wheel3.currentIndex)
            Toast.makeText(context, "כל הכבוד! הרווחת 500 מטבעות", Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context, "לא זכית במטבעות הפעם... לא נורא, תוכל לנסות שוב בעוד 5 דקות", Toast.LENGTH_LONG).show();

        btn.setText("Start");
        btn.setVisibility(View.VISIBLE);
        isStarted = false;
    }
}
