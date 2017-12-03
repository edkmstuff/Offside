package com.offsidegame.offside.helpers;

import android.content.Context;

import java.text.DecimalFormat;

/**
 * Created by user on 11/6/2017.
 */

public class Formatter {

    public static String intCommaSeparator = "#,###,###.#";


    public static String formatNumber(Integer value, String format){

        DecimalFormat formatter = new DecimalFormat(format);
        String formattedValue="";
        if(value==0)
            formattedValue = "0";
        else if(value<10000)
            formattedValue = formatter.format(value);
        else if(value<1000000){
            formattedValue = formatter.format((float)value/1000) + "K";
        }
        else if(value<1000000000){
            formattedValue = formatter.format((float)value/1000000) + "M";

        }
        return formattedValue;
    }

    public static String colorNumberToHexaValue(Context context,int colorId){
        return colorId==0 ? "#000000" : String.format("#%06X", (0xFFFFFF & context.getResources().getColor(colorId)));
    }

}
