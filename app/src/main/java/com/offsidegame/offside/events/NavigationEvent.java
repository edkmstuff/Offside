package com.offsidegame.offside.events;

import com.offsidegame.offside.R;

/**
 * Created by KFIR on 11/20/2016.
 */

public class NavigationEvent {
    private int navigationItemId;
    private String groupType;

    public NavigationEvent(int navigationItemId) {
        this.navigationItemId = navigationItemId;
    }

    public NavigationEvent(int navigationItemId, String groupType) {
        this.navigationItemId = navigationItemId;
        this.groupType = groupType;
    }


    public int getNavigationItemId() {
        return navigationItemId;
        //return getItemPositionByNavigationId();
    }


    public String getGroupType() {
        return groupType;
    }

    public void setGroupType(String groupType) {
        this.groupType = groupType;
    }
}
