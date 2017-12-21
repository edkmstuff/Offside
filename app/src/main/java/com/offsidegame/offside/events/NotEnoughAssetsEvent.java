package com.offsidegame.offside.events;

/**
 * Created by user on 11/16/2017.
 */

public class NotEnoughAssetsEvent {
    private int availableValue;
    private int requiredValue;
    private String assetName;
    private boolean includeLuckyWheel;

    public NotEnoughAssetsEvent(int availableValue, int requiredValue, String assetName, boolean includeLuckyWheel) {
        this.availableValue = availableValue;
        this.requiredValue = requiredValue;
        this.assetName= assetName;
        this.includeLuckyWheel = includeLuckyWheel;
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


    public boolean isIncludeLuckyWheel() {
        return includeLuckyWheel;
    }
}
