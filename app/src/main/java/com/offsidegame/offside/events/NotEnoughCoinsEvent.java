package com.offsidegame.offside.events;

/**
 * Created by user on 11/16/2017.
 */

public class NotEnoughCoinsEvent {
    private int availableCoins;
    private int requiredCoins;


    public NotEnoughCoinsEvent(int availableCoins, int requiredCoins) {
        this.availableCoins = availableCoins;
        this.requiredCoins = requiredCoins;
    }

    public boolean iseligble(){
        return availableCoins>requiredCoins;

    }
}
