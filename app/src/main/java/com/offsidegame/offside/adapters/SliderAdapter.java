package com.offsidegame.offside.adapters;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.offsidegame.offside.R;

import org.acra.ACRA;

/**
 * Created by user on 2/20/2018.
 */

public class SliderAdapter extends PagerAdapter {

    Context context;
    LayoutInflater layoutInflater;

    public int[] walkthroughImages;
    public String[] walkthroughHeadings;
    public String[] walkthroughDescriptions;


    public SliderAdapter(Context context){
        this.context = context;
        initArrays();
    }

    public void initArrays(){

        walkthroughImages = new int[] {
            R.drawable.groups,
                    R.drawable.profile,
                    R.drawable.play,
                    R.drawable.ic_shop
//            R.drawable.wt_groups,
//            R.drawable.wt_profile,
//            R.drawable.wt_shop,
//            R.drawable.wt_in_game
        };

        walkthroughHeadings = new String[]{
                context.getString(R.string.lbl_walkthrough_groups_title).toString(),
                context.getString(R.string.lbl_walkthrguoh_profile_title).toString(),
                context.getString(R.string.lbl_walkthrough_play_title).toString(),
                context.getString(R.string.lbl_walkthrough_shop_title).toString()
        };

        walkthroughDescriptions = new String[]{
                context.getString(R.string.lbl_walkthrough_groups_desc).toString(),
                context.getString(R.string.lbl_walkthrough_profile_desc).toString(),
                context.getString(R.string.lbl_walkthrough_play_desc).toString(),
                context.getString(R.string.lbl_walkthrough_shop_desc).toString()
        };


    }


    @Override
    public int getCount() {
        return walkthroughHeadings.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == (RelativeLayout) object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        try
        {
            layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.walkthrough_item, container,false);

            ImageView imageView = view.findViewById(R.id.wti_image_image_view);
            TextView headingTextView = view.findViewById(R.id.wti_heading_text_view);
            TextView descriptionTextView = view.findViewById(R.id.wti_description_text_view);
//            ImageView imageView = container.getChildAt(0).findViewById(R.id.wti_image_image_view);
//            TextView headingTextView = container.getChildAt(0).findViewById(R.id.wti_heading_text_view);
//            TextView descriptionTextView = container.getChildAt(0).findViewById(R.id.wti_description_text_view);

            imageView.setImageResource(walkthroughImages[position]);
            headingTextView.setText(walkthroughHeadings[position]);
            descriptionTextView.setText(walkthroughDescriptions[position]);

            container.addView(view);

            return view;

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }


    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
