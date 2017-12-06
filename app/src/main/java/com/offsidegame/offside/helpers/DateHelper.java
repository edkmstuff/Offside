package com.offsidegame.offside.helpers;

import android.content.Context;
import android.util.Log;

import com.offsidegame.offside.R;

import org.acra.ACRA;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by KFIR on 11/18/2016.
 */

public class DateHelper {

    public DateHelper(){

    }

    public Date getCurrentDate(){
        try
        {
            Calendar calendar = Calendar.getInstance();
            return calendar.getTime();

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }

    }

    public Date addHours(Date date, int hours){
        try
        {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 3);
            return calendar.getTime();

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return null;
        }

    }

    public String formatAsString(Date date, Context context){
        try
        {
            String dateFormat = context.getString(R.string.date_format);
            String dateAsString = new SimpleDateFormat(dateFormat).format(date);
            return dateAsString;

        } catch (Exception ex) {
                    ACRA.getErrorReporter().handleSilentException(ex);
                    return date.toString();
        }

    }

    public static  Date formatAsDate(String dateAsString, String dateFormat, Context context){

        SimpleDateFormat formatter = new SimpleDateFormat(dateFormat);
        Date date;
        try {
            date = formatter.parse(dateAsString);
            return date;
        }
        catch (ParseException pe) {
            //Log.e(context.getString(R.string.log_tag), pe.getMessage());
            ACRA.getErrorReporter().handleSilentException(pe);
            return null;
        }

    }

}
