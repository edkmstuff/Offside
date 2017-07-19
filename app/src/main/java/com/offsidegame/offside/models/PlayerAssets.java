package com.offsidegame.offside.models;

/**
 * Created by user on 7/16/2017.
 */

public class PlayerAssets {

    @com.google.gson.annotations.SerializedName("B")
    private int balance;

    @com.google.gson.annotations.SerializedName("PI")
    private int powerItems;

    @com.google.gson.annotations.SerializedName("IU")
    private String ImageUrl;


    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getPowerItems() {
        return powerItems;
    }

    public void setPowerItems(int powerItems) {
        this.powerItems = powerItems;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
    }
}
