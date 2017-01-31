package com.offsidegame.offside.models;

import android.app.Application;
import android.content.Context;

/**
 * Created by KFIR on 1/15/2017.
 */

public class OffsideApplication extends Application
{
    private  Context context;
    private static boolean isPlayerQuitGame= false;

    public  Context getContext() {
        return context;
    }

    public void setContext(Context mContext) {
        this.context = mContext;
    }


    public static boolean isPlayerQuitGame() {
        return isPlayerQuitGame;
    }

    public  static  void setIsPlayerQuitGame(boolean playerQuitGame) {
        isPlayerQuitGame = playerQuitGame;
    }
}