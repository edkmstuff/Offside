package com.offsidegame.offside.models;

import java.io.Serializable;

/**
 * Created by KFIR on 11/21/2016.
 */

public class PrivateGroupPlayer implements Serializable {


    @com.google.gson.annotations.SerializedName("PI")
    private String playerId;
    @com.google.gson.annotations.SerializedName("PN")
    private String playerName;
    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl ;
    @com.google.gson.annotations.SerializedName("IA")
    private Boolean isActive ;


    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }
}
