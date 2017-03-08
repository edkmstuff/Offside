package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Position;

/**
 * Created by KFIR on 11/17/2016.
 */

public class PositionEvent {

    private Position position;

    public PositionEvent(Position position) {
        this.position = position;
    }


    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }
}
