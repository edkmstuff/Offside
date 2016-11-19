package com.offsidegame.offside.helpers;

import android.content.Context;
import android.util.Log;

import com.offsidegame.offside.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KFIR on 11/18/2016.
 */

public class DateHelper {

    public DateHelper(){

    }

    public Date getCurrentDate(){
       Calendar calendar = Calendar.getInstance();
        return calendar.getTime();
    }

    public Date addHours(Date date, int hours){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 3);
        return calendar.getTime();
    }

    public String formatAsString(Date date, Context context){
        String dateFormat = context.getString(R.string.date_format);
        String dateAsString = new SimpleDateFormat(dateFormat).format(date);
        return dateAsString;
    }

    public Date formatAsDate(String dateAsString, Context context){
        //String loginExpirationTimeAsString = (String) settings.getString(getString(R.string.login_expiration_time), "");
        String dateFormat = context.getString(R.string.date_format);
        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date;
        try {
            date = formatter.parse(dateAsString);
        }
        catch (ParseException pe) {
            Log.e(context.getString(R.string.log_tag), pe.getMessage());
            date = null;
        }
        return date;
    }

}
