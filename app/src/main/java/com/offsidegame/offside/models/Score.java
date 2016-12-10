package com.offsidegame.offside.models;

/**
 * Created by KFIR on 12/7/2016.
 */
public class Score {
    @com.google.gson.annotations.SerializedName("Position")
    private int position;

    @com.google.gson.annotations.SerializedName("Name")
    private String name;

    @com.google.gson.annotations.SerializedName("ImageUrl")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("Points")
    private int points;

    public Score (int position, String name, String imageUrl, int points){
        this.points = position;
        this.name = name;
        this.imageUrl = imageUrl;
        this.points = points;
    }


    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }
}
