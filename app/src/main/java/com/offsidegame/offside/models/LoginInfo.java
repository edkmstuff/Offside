package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginInfo {


    @com.google.gson.annotations.SerializedName("Id")
    private String id; //email

    @com.google.gson.annotations.SerializedName("Name")
    private String name;

    @com.google.gson.annotations.SerializedName("Password")
    private String password;

    public LoginInfo() {}

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
