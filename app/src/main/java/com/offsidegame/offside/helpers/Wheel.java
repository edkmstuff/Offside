package com.offsidegame.offside.helpers;

import com.offsidegame.offside.R;

/**
 * Created by KFIR on 4/15/2017.
 */

public class Wheel extends Thread {

    public interface WheelListener {
        void newImage(int img);
    }

    private static int[] imgs = {
            R.drawable.slot_adidas,
            R.drawable.slot_gazprom,
            R.drawable.slot_heineken,
            R.drawable.slot_lays,
            R.drawable.slot_mastercard,
            R.drawable.slot_nissan,
            R.drawable.slot_unicredit,
            R.drawable.slot_xperia
    };

    public int currentIndex;
    private WheelListener wheelListener;
    private long frameDuration;
    private long startIn;
    private boolean isStarted;

    public Wheel(WheelListener wheelListener, long frameDuration, long startIn) {
        this.wheelListener = wheelListener;
        this.frameDuration = frameDuration;
        this.startIn = startIn;
        currentIndex = 0;
        isStarted = true;
    }

    public void nextImg() {
        currentIndex++;
        if (currentIndex == imgs.length)
            currentIndex = 0;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(startIn);
        } catch (InterruptedException e) {

        }

        while (isStarted){
            try {
                Thread.sleep(frameDuration);
            } catch (InterruptedException e) {

            }

            nextImg();

            if (wheelListener != null){
                wheelListener.newImage(imgs[currentIndex]);
            }
        }
    }

    public void stopWheel(){
        isStarted = false;
    }
}
