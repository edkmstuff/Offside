package com.offsidegame.offside.helpers;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by user on 11/6/2017.
 */

public class Formatter {

    public static String intCommaSeparator = "#,###,###";


    public static String formatNumber(Integer value, String format){
        DecimalFormat formatter = new DecimalFormat(format);
        return formatter.format(value);
    }

    public static String colorNumberToHexaValue(Context context,int colorId){
        return colorId==0 ? "#000000" : String.format("#%06X", (0xFFFFFF & context.getResources().getColor(colorId)));
    }

}
