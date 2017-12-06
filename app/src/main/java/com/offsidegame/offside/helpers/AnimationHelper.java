package com.offsidegame.offside.helpers;


import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import org.acra.ACRA;

public class AnimationHelper {

    public static void animateButtonClick(View view, int duration){

        try
        {
            YoYo.with(Techniques.FadeOut).duration(duration/2).playOn(view);
            YoYo.with(Techniques.FadeIn).duration(duration/2).playOn(view);

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);

        }

    }

}