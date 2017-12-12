package com.offsidegame.offside.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.MyWheelItem;

import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;

/**
 * Created by user on 12/11/2017.
 */

public class WheelImageAdapter extends CursorWheelLayout.CycleWheelAdapter {

    private Context context;
    private List<MyWheelItem> myWheelItems;
    private LayoutInflater inflater;

    public WheelImageAdapter(Context context, List<MyWheelItem> myWheelItems) {
        this.context = context;
        this.myWheelItems = myWheelItems;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return myWheelItems.size();
    }


    @Override
    public View getView(View parent, int position) {
        MyWheelItem myWheelItem = getItem(position);
        View root = inflater.inflate(R.layout.wheel_image_item,null, false);
        TextView rewardValueTextView = root.findViewById(R.id.wii_reward_value_text_view);
        rewardValueTextView.setText(String.format("%d", myWheelItem.rewardValue));
        ImageView imageView = root.findViewById(R.id.wii_reward_image_image_view);
        imageView.setImageResource(myWheelItem.imageResourceId);


        return root;
    }

    @Override
    public MyWheelItem getItem(int position) {
        return myWheelItems.get(position);
    }
}
