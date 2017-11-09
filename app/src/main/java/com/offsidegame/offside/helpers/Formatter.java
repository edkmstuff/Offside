package com.offsidegame.offside.helpers;

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
}
