package com.offsidegame.offside.helpers;


import android.view.View;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

public class AnimationHelper {

    public static void animateButtonClick(View view, int duration){

        YoYo.with(Techniques.FadeOut).duration(duration/2).playOn(view);
        YoYo.with(Techniques.FadeIn).duration(duration/2).playOn(view);
    }

}