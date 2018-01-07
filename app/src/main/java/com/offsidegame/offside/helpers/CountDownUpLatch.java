package com.offsidegame.offside.helpers;

/**
 * Created by KFIR on 1/3/2018.
 */

import java.util.concurrent.CountDownLatch;

public class CountDownUpLatch {
    //from: http://www.java2s.com/Code/Java/Threads/CountUpDownLatch.htm


    CountDownLatch latch;
    int count;

    public CountDownUpLatch() {
        latch = new CountDownLatch(1);
        this.count = count;
    }

    public void await() throws InterruptedException {

        if (count == 0) {
            return;
        }

        latch.await();
    }

    public void countDown() {

        count--;

        if (count == 0) {
            latch.countDown();
        }
    }

    public long getCount() {
        return count;
    }

    public void countUp() {

        if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
        }

        count++;
    }

    public void setCount(int count) {

        if (count == 0) {
            if (latch.getCount() != 0) {
                latch.countDown();
            }
        } else if (latch.getCount() == 0) {
            latch = new CountDownLatch(1);
        }

        this.count = count;
    }


}
