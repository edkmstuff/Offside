package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginEvent {
    private String id;

    public LoginEvent(String id) {
        this.id = id;
    }


    public String getId() {
        return id;
    }
}
