package com.offsidegame.offside.helpers;

import android.os.AsyncTask;


import com.offsidegame.offside.events.CompletedHttpRequestEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static java.util.stream.Collectors.toList;

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

        if(url==null) {
            responseCode =0;
            return null;
        }

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

    public static Map<String, List<String>> splitQuery(URL url) throws UnsupportedEncodingException {
        final Map<String, List<String>> query_pairs = new LinkedHashMap<String, List<String>>();
        if(url.getQuery()==null)
            return null;
        final String[] pairs = url.getQuery().split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            if (!query_pairs.containsKey(key)) {
                query_pairs.put(key, new LinkedList<String>());
            }
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            query_pairs.get(key).add(value);
        }
        return query_pairs;
    }



}




