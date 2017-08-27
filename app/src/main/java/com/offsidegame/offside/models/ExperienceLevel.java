package com.offsidegame.offside.models;

import com.offsidegame.offside.R;

import java.util.ArrayList;

/**
 * Created by user on 8/9/2017.
 */

public class ExperienceLevel {

    private String name;
    private int minValue;
    private int maxValue;
    private int imageViewResourceId;
    private int imageViewResourceIdCurrent;

    public static ArrayList<ExperienceLevel> expLevels = new ArrayList<>();

    public static ExperienceLevel expLevel1 = new ExperienceLevel("Beginner",0,3999, R.drawable.level_baby, R.drawable.level_baby_selected);
    public static ExperienceLevel expLevel2 = new ExperienceLevel("Wanderboy",4000,7999, R.drawable.level_kid, R.drawable.level_kid_selected);
    public static ExperienceLevel expLevel3 = new ExperienceLevel("Pro",8000,11999, R.drawable.level_pro, R.drawable.level_pro_selected);
    public static ExperienceLevel expLevel4 = new ExperienceLevel("Star",12000,19999, R.drawable.level_star, R.drawable.level_star_selected);
    public static ExperienceLevel expLevel5 = new ExperienceLevel("Legend",20000,100000000, R.drawable.level_legend, R.drawable.level_legend_selected);


    public ExperienceLevel(String name, int minValue, int maxValue, int imageViewResourceId, int imageViewResourceIdCurrent) {
        this.name = name;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.imageViewResourceId = imageViewResourceId;
        this.imageViewResourceIdCurrent = imageViewResourceIdCurrent;
        expLevels.add(this);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMinValue() {
        return minValue;
    }

    public void setMinValue(int minValue) {
        this.minValue = minValue;
    }

    public int getMaxValue() {
        return maxValue;
    }

    public void setMaxValue(int maxValue) {
        this.maxValue = maxValue;
    }

    public static ExperienceLevel findByName(String name){

        for(ExperienceLevel expLevel : expLevels) {
            if(expLevel.getName().equals(name)) {
                return expLevel;
            }
        }
        return null;

    }
    public static ExperienceLevel findByIndex(int index){

        return expLevels.get(index);

    }



//    public static int getExperienceLevelImageResourceIdByExperienceLevelName(String name){
//        int resourceId=0;
//        if(name.equals(experienceLevel1Name))
//            resourceId = R.drawable.level_baby;
//        if(name.equals(experienceLevel2Name))
//            resourceId = R.drawable.level_kid;
//        if(name.equals(experienceLevel3Name))
//            resourceId = R.drawable.level_pro;
//        if(name.equals(experienceLevel4Name))
//            resourceId = R.drawable.level_star;
//        if(name.equals(experienceLevel5Name))
//            resourceId = R.drawable.level_legend;
//
//
//        return resourceId;
//
//    }

    public static int getExperienceLevelImageResourceIdByIndex(int index,int playerCurrentExpLevelImageResourceId){
        int resourceId=0;
        if(index==0)
            resourceId = R.drawable.level_baby == playerCurrentExpLevelImageResourceId ? R.drawable.level_baby_selected : R.drawable.level_baby ;
        if(index==1)
            resourceId = R.drawable.level_kid == playerCurrentExpLevelImageResourceId ? R.drawable.level_kid_selected : R.drawable.level_kid ;
        if(index==2)
            resourceId = R.drawable.level_pro == playerCurrentExpLevelImageResourceId ? R.drawable.level_pro_selected : R.drawable.level_pro ;
        if(index==3)
            resourceId = R.drawable.level_star== playerCurrentExpLevelImageResourceId ? R.drawable.level_star_selected : R.drawable.level_star ;
        if(index==4)
            resourceId = R.drawable.level_legend == playerCurrentExpLevelImageResourceId ? R.drawable.level_legend_selected : R.drawable.level_legend ;


        return resourceId;

    }

    public int getImageViewResourceId() {
        return imageViewResourceId;
    }

    public void setImageViewResourceId(int imageViewResourceId) {
        this.imageViewResourceId = imageViewResourceId;
    }

    public int getImageViewResourceIdCurrent() {
        return imageViewResourceIdCurrent;
    }

    public void setImageViewResourceIdCurrent(int imageViewResourceIdCurrent) {
        this.imageViewResourceIdCurrent = imageViewResourceIdCurrent;
    }
}
