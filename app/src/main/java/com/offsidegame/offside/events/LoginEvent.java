package com.offsidegame.offside.events;

import com.facebook.Profile;

import java.net.URI;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginEvent {
    private String id;
    private String name;
    private String password;
    private boolean isFacebookLogin;



    public LoginEvent(String id, String name, String password, boolean isFacebookLogin) {
        this.id = id;
        this.name = name;
        this.password= password;
        this.isFacebookLogin = isFacebookLogin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean getIsFacebookLogin(){ return isFacebookLogin;}

    public String getPassword() {
        return password;
    }
}
