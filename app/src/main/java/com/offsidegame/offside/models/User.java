package com.offsidegame.offside.models;

/**
 * Created by user on 12/13/2016.
 */

public class User {
    @com.google.gson.annotations.SerializedName("Id")
    private String id;
    @com.google.gson.annotations.SerializedName("Name")
    private String name;
    @com.google.gson.annotations.SerializedName("Email")
    private String email;
    @com.google.gson.annotations.SerializedName("ProfilePictureUri")
    private String profilePictureUri;
    @com.google.gson.annotations.SerializedName("Password")
    private String password;
    @com.google.gson.annotations.SerializedName("DeviceToken")
    private String deviceToken;
    @com.google.gson.annotations.SerializedName("offsideCoins")
    private int offsideCoins;


    public User(){}

    public User(String id,String name, String email, String profilePictureUri, String password, String deviceToken){
        this.id=id;
        this.name=name;
        this.email= email;
        this.profilePictureUri=profilePictureUri;
        this.password= password;
        this.deviceToken = deviceToken;

    }

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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePictureUri() {
        return profilePictureUri;
    }

    public void setProfilePictureUri(String profilePictureUri) {
        this.profilePictureUri = profilePictureUri;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }


}
