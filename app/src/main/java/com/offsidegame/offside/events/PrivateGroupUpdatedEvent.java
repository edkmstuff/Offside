package com.offsidegame.offside.events;

import com.offsidegame.offside.models.PrivateGroup;

/**
 * Created by user on 12/8/2017.
 */

public class PrivateGroupUpdatedEvent {
    private PrivateGroup privateGroup;

    public PrivateGroupUpdatedEvent(PrivateGroup privateGroup) {
        this.privateGroup = privateGroup;
    }


    public PrivateGroup getPrivateGroup() {
        return privateGroup;
    }

    public void setPrivateGroup(PrivateGroup privateGroup) {
        this.privateGroup = privateGroup;
    }
}
