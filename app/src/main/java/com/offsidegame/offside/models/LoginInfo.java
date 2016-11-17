package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginInfo {


    @com.google.gson.annotations.SerializedName("Id")
    private String id;

    @com.google.gson.annotations.SerializedName("Name")
    private String name;

    public LoginInfo() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
