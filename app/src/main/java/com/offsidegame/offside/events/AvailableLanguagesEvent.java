package com.offsidegame.offside.events;

/**
 * Created by KFIR on 11/20/2016.
 */

public class AvailableLanguagesEvent {
    private String[] availableLanquages;

    public AvailableLanguagesEvent( String[] availableLanquages){

        this.availableLanquages= availableLanquages;
    }


    public String[] getAvailableLanquages() {
        return availableLanquages;
    }
}
