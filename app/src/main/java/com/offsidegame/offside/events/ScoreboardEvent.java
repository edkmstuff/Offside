package com.offsidegame.offside.events;

import com.offsidegame.offside.models.Scoreboard;

/**
 * Created by KFIR on 12/8/2016.
 */
public class ScoreboardEvent {
    private Scoreboard scoreboard;
    public ScoreboardEvent(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }


}
