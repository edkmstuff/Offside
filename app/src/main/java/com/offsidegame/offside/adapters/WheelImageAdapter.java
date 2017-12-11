package com.offsidegame.offside.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.offsidegame.offside.R;
import com.offsidegame.offside.models.WheelImage;

import java.util.List;

import github.hellocsl.cursorwheel.CursorWheelLayout;

/**
 * Created by user on 12/11/2017.
 */

public class WheelImageAdapter extends CursorWheelLayout.CycleWheelAdapter {

    private Context context;
    private List<WheelImage> wheelImages;
    private LayoutInflater inflater;

    public WheelImageAdapter(Context context, List<WheelImage> wheelImages) {
        this.context = context;
        this.wheelImages = wheelImages;
        this.inflater = LayoutInflater.from(context);
    }


    @Override
    public int getCount() {
        return wheelImages.size();
    }


    @Override
    public View getView(View parent, int position) {
        WheelImage wheelImage = getItem(position);
        View root = inflater.inflate(R.layout.wheel_image_item,null, false);
        TextView rewardValueTextView = root.findViewById(R.id.wii_reward_value_text_view);
        rewardValueTextView.setText(String.format("%d",wheelImage.rewardValue));
        ImageView imageView = root.findViewById(R.id.wii_reward_image_image_view);
        imageView.setImageResource(wheelImage.imageResourceId);


        return root;
    }

    @Override
    public WheelImage getItem(int position) {
        return wheelImages.get(position);
    }
}
