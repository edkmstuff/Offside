package com.offsidegame.offside.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.NavigationEvent;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.util.Random;


public class LuckyWheelFragment extends Fragment {

    private Button rollWheelButton;
    private ImageView wheelImageView;
    Random r;
    int degree=0, degree_old=0;

    //formula:  DS (degreesPerSector) = 360/numSectorsOnWheel  >>  FACTOR = DS/2
    //the middle of each sector. the range it covers is FACTOR*2 (e.g. 4 slices=> 45)
    private static final float FACTOR = 45f;


    public static LuckyWheelFragment newInstance() {
        LuckyWheelFragment luckyWheelFragment = new LuckyWheelFragment();
        return luckyWheelFragment;
    }

    public LuckyWheelFragment() {
    }

    @Override
    public void onResume() {
        try {

            super.onResume();
        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return;
        }


    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_lucky_wheel, container, false);

            getIds(rootView);

            setEvents();

            return rootView;


        } catch (Exception ex) {
            ACRA.getErrorReporter().handleSilentException(ex);
            return null;
        }

    }

    private void getIds(View rootView) {

        rollWheelButton = rootView.findViewById(R.id.flw_roll_wheel_button);
        wheelImageView = rootView.findViewById(R.id.flw_wheel_image_view);

    }

    private void setEvents() {


        rollWheelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                degree_old = degree%360;
                r= new Random();
                degree =r.nextInt(3600)+720;
                Log.d("SIDEKICK","degree: "+ degree);
                RotateAnimation rotate = new RotateAnimation(degree_old,degree
                        ,RotateAnimation.RELATIVE_TO_SELF,0.5f,RotateAnimation.RELATIVE_TO_SELF,0.5f);
                rotate.setDuration(3600);
                rotate.setFillAfter(true);
                rotate.setInterpolator(new DecelerateInterpolator());
                rotate.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        Toast.makeText(getContext(),"Spinning...",Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        String message = prizeEarned(360-(degree%360));
                        Toast.makeText(getContext(),message,Toast.LENGTH_LONG).show();

                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {

                    }
                });
                wheelImageView.startAnimation(rotate);



            }
        });


    }



    private String prizeEarned(int degrees){
        String text="";
        if(degrees>=(FACTOR*1) && degrees<(FACTOR*3))
            text="B";
        if(degrees>=(FACTOR*3) && degrees<(FACTOR*5))
            text="D";
        if(degrees>=(FACTOR*5) && degrees<(FACTOR*7))
           text="C";

        if((degrees>=(FACTOR*7) && degrees<(360)) ||(degrees>=0 && degrees<(FACTOR*1)))
            text="A";

        return text;

    }


}
