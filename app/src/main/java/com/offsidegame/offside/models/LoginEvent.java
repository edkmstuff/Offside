package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginEvent {
    private String id;
    private String name;

    public LoginEvent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
