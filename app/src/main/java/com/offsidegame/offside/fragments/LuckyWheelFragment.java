package com.offsidegame.offside.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.offsidegame.offside.R;
import com.offsidegame.offside.adapters.WheelImageAdapter;
import com.offsidegame.offside.events.NavigationEvent;
import com.offsidegame.offside.models.WheelImage;

import org.acra.ACRA;
import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;


public class LuckyWheelFragment extends Fragment {


    private CursorWheelLayout cursorWheelLayout;
    private List<WheelImage> wheelImages;





    public static LuckyWheelFragment newInstance() {
        LuckyWheelFragment settingsFragment = new LuckyWheelFragment();
        return settingsFragment;
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

        cursorWheelLayout = rootView.findViewById(R.id.flw_cursor_wheel_layout);


    }

    private void setEvents() {

        cursorWheelLayout.setOnMenuSelectedListener(new CursorWheelLayout.OnMenuSelectedListener() {
            @Override
            public void onItemSelected(CursorWheelLayout parent, View view, int pos) {
                Toast.makeText(getContext(), "Selected: "+ wheelImages.get(pos).rewardValue, Toast.LENGTH_LONG).show();

            }
        });


    }

    private void loadWheelContent() {
        wheelImages = new ArrayList<>();
        wheelImages.add(new WheelImage(R.drawable.ic_coins_heap,100));
        wheelImages.add(new WheelImage(R.drawable.ic_coins_heap,50));
        wheelImages.add(new WheelImage(R.drawable.ic_coins_heap,10));
        wheelImages.add(new WheelImage(R.drawable.ic_coins_heap,0));
        wheelImages.add(new WheelImage(R.mipmap.ic_soccer_ball,2));
        wheelImages.add(new WheelImage(R.mipmap.ic_soccer_ball,4));
        wheelImages.add(new WheelImage(R.mipmap.ic_soccer_ball,8));

        WheelImageAdapter wheelImageAdapter = new WheelImageAdapter(getContext(),wheelImages);
        cursorWheelLayout.setAdapter(wheelImageAdapter);

    }



    public void backToGroups(){

        EventBus.getDefault().post(new NavigationEvent(R.id.nav_action_groups));
    }

}
