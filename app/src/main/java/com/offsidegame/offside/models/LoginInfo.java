package com.offsidegame.offside.models;

/**
 * Created by KFIR on 11/17/2016.
 */

public class LoginInfo {


    @com.google.gson.annotations.SerializedName("I")
    private String id; //email

    @com.google.gson.annotations.SerializedName("N")
    private String name;

    @com.google.gson.annotations.SerializedName("P")
    private String password;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
