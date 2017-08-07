package com.offsidegame.offside.models;

import java.util.ArrayList;

/**
 * Created by user on 8/1/2017.
 */

public class UserProfileInfo {

    private Player player;
    private ArrayList<Reward> rewards;
    private PlayerInGameRecord mostRecentPlayedGame;
    private Position position;
    private ArrayList<Player> mostRecentPlayedGameWinners;


    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }


    public PlayerInGameRecord getMostRecentPlayedGame() {
        return mostRecentPlayedGame;
    }

    public void setMostRecentPlayedGame(PlayerInGameRecord mostRecentPlayedGame) {
        this.mostRecentPlayedGame = mostRecentPlayedGame;
    }

    public Position getPosition() {
        return position;
    }

    public void setPosition(Position position) {
        this.position = position;
    }

    public ArrayList<Player> getMostRecentPlayedGameWinners() {
        return mostRecentPlayedGameWinners;
    }

    public void setMostRecentPlayedGameWinners(ArrayList<Player> mostRecentPlayedGameWinners) {
        this.mostRecentPlayedGameWinners = mostRecentPlayedGameWinners;
    }
}

