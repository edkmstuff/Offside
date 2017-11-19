package com.offsidegame.offside.events;

import android.content.Context;

/**
 * Created by KFIR on 11/18/2016.
 */
public class NetworkingServiceBoundEvent {
    private Context context;
    public NetworkingServiceBoundEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
