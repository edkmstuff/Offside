package com.offsidegame.offside.models;

/**
 * Created by user on 12/13/2016.
 */

public class User {
    @com.google.gson.annotations.SerializedName("I")
    private String id;
    @com.google.gson.annotations.SerializedName("N")
    private String name;
    @com.google.gson.annotations.SerializedName("E")
    private String email;
    @com.google.gson.annotations.SerializedName("PPU")
    private String profilePictureUri;
    @com.google.gson.annotations.SerializedName("PF")
    private GameFeature[] premiumFeatures;
    @com.google.gson.annotations.SerializedName("OC")
    private int offsideCoins;
    @com.google.gson.annotations.SerializedName("UC")
    private String userColor;


    public User(){}

    public User(String id,String name, String email, String profilePictureUri, String userColor){
        this.id=id;
        this.name=name;
        this.email= email;
        this.profilePictureUri=profilePictureUri;
        this.premiumFeatures = null;
        this.offsideCoins = 0;
        this.userColor =userColor;
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

    public GameFeature[] getPremiumFeatures() {
        return premiumFeatures;
    }

    public void setPremiumFeatures(GameFeature[] premiumFeatures) {
        this.premiumFeatures = premiumFeatures;
    }

    public int getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(int offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public String getUserColor() {
        return userColor;
    }

    public void setUserColor(String userColor) {
        this.userColor = userColor;
    }
}
