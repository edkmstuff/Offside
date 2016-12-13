package com.offsidegame.offside.events;

import android.content.Context;

/**
 * Created by KFIR on 11/18/2016.
 */
public class SignalRServiceBoundEvent {
    private Context context;
    public SignalRServiceBoundEvent(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }
}
