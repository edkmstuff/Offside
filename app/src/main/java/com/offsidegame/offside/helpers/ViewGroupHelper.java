package com.offsidegame.offside.helpers;

import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by KFIR on 12/20/2016.
 */

public class ViewGroupHelper {

    private static ArrayList<View> getAllChildren(View v) {

        if (!(v instanceof ViewGroup)) {
            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            return viewArrayList;
        }

        ArrayList<View> result = new ArrayList<View>();

        ViewGroup viewGroup = (ViewGroup) v;
        for (int i = 0; i < viewGroup.getChildCount(); i++) {

            View child = viewGroup.getChildAt(i);

            ArrayList<View> viewArrayList = new ArrayList<View>();
            viewArrayList.add(v);
            viewArrayList.addAll(getAllChildren(child));

            result.addAll(viewArrayList);
        }
        return result;
    }

    public static void ChangeRtlToLtr(View v){
        ArrayList<View> allChildren =  getAllChildren(v);
        for (View view : allChildren) {
            view.setLayoutDirection(View.LAYOUT_DIRECTION_LTR);
        }

    }
}
