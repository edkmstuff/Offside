package com.offsidegame.offside.events;

import com.facebook.Profile;

import java.net.URI;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginEvent {
    private String id;
    private String name;
    private Profile fbProfile;

    public LoginEvent(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public LoginEvent(String id, String name, Profile profile) {
        this.id = id;
        this.name = name;
        this.fbProfile = profile;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Profile getFbProfile(){ return fbProfile;}
}
