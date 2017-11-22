package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PrivateGroup;

/**
 * Created by user on 9/13/2017.
 */

public class PrivateGroupEvent {
    private PrivateGroup privateGroup;

    public PrivateGroupEvent(PrivateGroup privateGroup) {

        this.privateGroup = privateGroup;

    }

    public PrivateGroup getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(PrivateGroup privateGroup) {
        this.privateGroup = privateGroup;
    }


}
