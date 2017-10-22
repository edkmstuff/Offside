package com.offsidegame.offside.events;

/**
 * Created by user on 10/18/2017.
 */

public class PlayerImageSavedEvent {

    private boolean isPLayerImageSaved;

    public PlayerImageSavedEvent(boolean isPLayerImageSaved) {

        this.isPLayerImageSaved = isPLayerImageSaved;
    }


    public boolean isPLayerImageSaved() {
        return isPLayerImageSaved;
    }

    public void setPLayerImageSaved(boolean PLayerImageSaved) {
        isPLayerImageSaved = PLayerImageSaved;
    }


}
