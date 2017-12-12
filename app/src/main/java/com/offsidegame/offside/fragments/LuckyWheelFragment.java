package com.offsidegame.offside.fragments;

import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.bluehomestudio.luckywheel.LuckyWheel;
import com.bluehomestudio.luckywheel.OnLuckyWheelReachTheTarget;
import com.bluehomestudio.luckywheel.WheelItem;

import com.offsidegame.offside.R;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.helpers.Formatter;
import com.offsidegame.offside.models.MyWheelItem;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class LuckyWheelFragment extends Fragment {
    private LuckyWheel luckyWheel;
    private List<MyWheelItem> myWheelItems;
    private Button rollWheelButton;
    private int randomNumber;





    public static LuckyWheelFragment newInstance() {
        LuckyWheelFragment luckyWheelFragment = new LuckyWheelFragment();
        return luckyWheelFragment;
    }

    public LuckyWheelFragment(){}

    @Override
    public void onResume(){
        try
        {

            super.onResume();
        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return ;
        }


    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        try
        {
            // Inflate the layout for this fragment
            View rootView = inflater.inflate(R.layout.fragment_lucky_wheel, container, false);

            randomNumber=0;
            
            getIds(rootView);
            
            setEvents();

            loadWheelContent();

            return rootView;


        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }

    }

    private void getIds(View rootView) {

        //cursorWheelLayout = rootView.findViewById(R.id.flw_cursor_wheel_layout);
        luckyWheel = rootView.findViewById(R.id.flw_bluehome_studio_lucky_wheel);
        rollWheelButton = rootView.findViewById(R.id.flw_roll_wheel_button);


    }

    private void setEvents() {


        rollWheelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                randomNumber =getRandomNumber();
                luckyWheel.rotateWheelTo(randomNumber);

            }
        });

        luckyWheel.setLuckyWheelReachTheTarget(new OnLuckyWheelReachTheTarget() {

            @Override
            public void onReachTarget() {
                int rewardValue;
                MyWheelItem selectedWheelItem = myWheelItems.get(randomNumber-1);
                if(selectedWheelItem==null){
                    Toast.makeText(getContext(),String.format("Problem %d",randomNumber),Toast.LENGTH_LONG).show();
                }

                rewardValue = selectedWheelItem.rewardValue;

                Toast.makeText(getContext(),String.format("you earned %d",rewardValue),Toast.LENGTH_LONG).show();

            }
        });


    }

    private void loadWheelContent() {

        myWheelItems = new ArrayList<>();
        String wheelSliceBackgroundColorEven = Formatter.colorNumberToHexaValue(getContext(),R.color.wheelBackgroundColorEven);
        String wheelSliceBackgroundColorOdd = Formatter.colorNumberToHexaValue(getContext(),R.color.wheelBackgroundColorOdd);

        myWheelItems.add(new MyWheelItem(R.mipmap.ic_coin,10,Color.parseColor(wheelSliceBackgroundColorEven), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coin)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_soccer_ball,1,Color.parseColor(wheelSliceBackgroundColorOdd), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_soccer_ball)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_coin,50,Color.parseColor(wheelSliceBackgroundColorEven), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coin)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_soccer_ball,2,Color.parseColor(wheelSliceBackgroundColorOdd), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_soccer_ball)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_coin,100,Color.parseColor(wheelSliceBackgroundColorEven), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coin)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_soccer_ball,4,Color.parseColor(wheelSliceBackgroundColorOdd), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_soccer_ball)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_coin,200,Color.parseColor(wheelSliceBackgroundColorEven), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_coin)));
        myWheelItems.add(new MyWheelItem(R.mipmap.ic_soccer_ball,8,Color.parseColor(wheelSliceBackgroundColorOdd), BitmapFactory.decodeResource(getResources(), R.mipmap.ic_soccer_ball)));

        List<WheelItem> wheelItems = new ArrayList<>();

        for(MyWheelItem myWheelItem : myWheelItems){
            wheelItems.add(myWheelItem.wheelItem);
        }

        luckyWheel.addWheelItems(wheelItems);

    }


    public int getRandomNumber(){

        int randomNumber = 1;
        Random r = new Random();
        int Low = 1;
        int High = myWheelItems.size();
        randomNumber = r.nextInt(High-Low) + Low;

        return randomNumber;

    }



    public void backToGroups(){

        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
    }

}
