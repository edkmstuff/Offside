package com.offsidegame.offside.events;

/**
 * Created by KFIR on 11/20/2016.
 */

public class NavigationEvent {
    private int navigationItemId;

    public NavigationEvent(int navigationItemId) {
        this.navigationItemId = navigationItemId;
    }


    public int getNavigationItemId() {
        return navigationItemId;
    }
}
