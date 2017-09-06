package com.offsidegame.offside.models;

/**
 * Created by user on 3/5/2017.
 */

public class GameFeature {
    @com.google.gson.annotations.SerializedName("GFN")
    private String name;
    @com.google.gson.annotations.SerializedName("GFC")
    private int cost;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getCost() {
        return cost;
    }

    public void setCost(int cost) {
        this.cost = cost;
    }
}
