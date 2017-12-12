package com.offsidegame.offside.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;

import com.bluehomestudio.luckywheel.WheelItem;
import com.offsidegame.offside.R;

/**
 * Created by user on 12/11/2017.
 */

public class MyWheelItem {

    public int imageResourceId;
    public int rewardValue;
    public WheelItem wheelItem;


    public MyWheelItem(int imageResourceId, int rewardValue, int colorId,Bitmap bitmap) {
        this.imageResourceId = imageResourceId;
        this.rewardValue = rewardValue;
        this.wheelItem = new WheelItem(colorId, bitmap);


    }






}
