package com.offsidegame.offside.helpers;

import android.os.AsyncTask;

import com.offsidegame.offside.events.CompletedHttpRequestEvent;

import org.greenrobot.eventbus.EventBus;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by user on 10/19/2017.
 */

public class HttpHelper extends AsyncTask<Void,Void, Void> {

    private String url;
    private int responseCode;
    private boolean isUrlValid;

    public HttpHelper(String url) {
        this.url = url;
        this.responseCode = 0;
        this.isUrlValid = false;
    }


    protected Void doInBackground(Void... urls) {

        if(url==null)
            responseCode =0;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .build();

        try (Response response = client.newCall(request).execute()) {

            responseCode = response.code();
            return null;
        }
        catch (Exception e) {
            responseCode = 0;
            return null;
        }

    }

    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        isUrlValid = (responseCode==200);
        EventBus.getDefault().post(new CompletedHttpRequestEvent(isUrlValid));
    }



}




