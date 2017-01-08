package com.offsidegame.offside.events;

/**
 * Created by KFIR on 1/8/2017.
 */

public class ConnectionEvent {
    private Boolean isConnected = true;
    private String msg;

    public ConnectionEvent (boolean isConnected, String msg){
        this.isConnected = isConnected;
        this.msg = msg;
    }


    public Boolean getConnected() {
        return isConnected;
    }

    public void setConnected(Boolean connected) {
        isConnected = connected;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
