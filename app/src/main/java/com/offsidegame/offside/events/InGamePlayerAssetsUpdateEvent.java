package com.offsidegame.offside.events;

/**
 * Created by user on 11/27/2017.
 */

public class InGamePlayerAssetsUpdateEvent {
    private String assetType;
    private int oldValue;
    private int newValue;

    public static String assetTypeCoins = "COINS";
    public static String assetTypePowerItems = "POWER_ITEMS";

    public InGamePlayerAssetsUpdateEvent(String assetType,int oldValue, int newValue){
        this.assetType = assetType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }

    public String getAssetType() {
        return assetType;
    }

    public void setAssetType(String assetType) {
        this.assetType = assetType;
    }

    public int getNewValue() {
        return newValue;
    }

    public void setNewValue(int newValue) {
        this.newValue = newValue;
    }

    public int getOldValue() {
        return oldValue;
    }

    public void setOldValue(int oldValue) {
        this.oldValue = oldValue;
    }
}
