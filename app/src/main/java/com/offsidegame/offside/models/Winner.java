package com.offsidegame.offside.models;

/**
 * Created by user on 8/9/2017.
 */

public class Winner {

    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("OC")
    private Integer offsideCoins;

    @com.google.gson.annotations.SerializedName("PN")
    private String playerName;

    @com.google.gson.annotations.SerializedName("P")
    private Integer position;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getOffsideCoins() {
        return offsideCoins;
    }

    public void setOffsideCoins(Integer offsideCoins) {
        this.offsideCoins = offsideCoins;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public Integer getPosition() {
        return position;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }
}
