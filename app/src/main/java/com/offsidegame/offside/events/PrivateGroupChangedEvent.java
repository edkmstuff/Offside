package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PrivateGroup;

/**
 * Created by user on 9/19/2017.
 */

public class PrivateGroupChangedEvent {



    public PrivateGroup getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(PrivateGroup privateGroup) {
        this.privateGroup = privateGroup;
    }

    public PrivateGroupChangedEvent(PrivateGroup privateGroup) {

        this.privateGroup = privateGroup;
    }

    private PrivateGroup privateGroup;
}
