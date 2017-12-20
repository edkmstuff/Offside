package com.offsidegame.offside.events;

/**
 * Created by user on 11/16/2017.
 */

public class NotEnoughAssetsEvent {
    private int availableValue;
    private int requiredValue;
    private String assetName;

    public NotEnoughAssetsEvent(int availableValue, int requiredValue, String assetName) {
        this.availableValue = availableValue;
        this.requiredValue = requiredValue;
        this.assetName= assetName;
    }

    public boolean hasEnoughAssets(){
        return availableValue > requiredValue;

    }

    public String getAssetName() {
        return assetName;
    }

    public void setAssetName(String assetName) {
        this.assetName = assetName;
    }
}
