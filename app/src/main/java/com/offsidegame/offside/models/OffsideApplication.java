package com.offsidegame.offside.models;

import android.app.Application;
import android.content.Context;

/**
 * Created by KFIR on 1/15/2017.
 */

public class OffsideApplication extends Application
{
    private  Context context;

    public  Context getContext() {
        return context;
    }

    public void setContext(Context mContext) {
        this.context = mContext;
    }



}