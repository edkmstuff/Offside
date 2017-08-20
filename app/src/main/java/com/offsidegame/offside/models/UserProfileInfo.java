package com.offsidegame.offside.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by user on 8/1/2017.
 */

public class UserProfileInfo {


    @com.google.gson.annotations.SerializedName("IU")
    private String imageUrl;

    @com.google.gson.annotations.SerializedName("PN")
    private String playerName;

    @com.google.gson.annotations.SerializedName("ELN")
    private String experienceLevelName;

    @com.google.gson.annotations.SerializedName("TGP")
    private int totalGamesPlayed;

    @com.google.gson.annotations.SerializedName("TT")
    private int totalTrophies;

    @com.google.gson.annotations.SerializedName("APPG")
    private double averageProfitPerGame;

    @com.google.gson.annotations.SerializedName("R")
    private ArrayList<Reward> rewards;

    @com.google.gson.annotations.SerializedName("PG")
    private ArrayList<PlayerGame> playerGames;

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getExperienceLevelName() {
        return experienceLevelName;
    }

    public void setExperienceLevelName(String experienceLevelName) {
        this.experienceLevelName = experienceLevelName;
    }

    public int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    public void setTotalGamesPlayed(int totalGamesPlayed) {
        this.totalGamesPlayed = totalGamesPlayed;
    }

    public int getTotalTrophies() {
        return totalTrophies;
    }

    public void setTotalTrophies(int totalTrophies) {
        this.totalTrophies = totalTrophies;
    }

    public double getAverageProfitPerGame() {
        return averageProfitPerGame;
    }

    public void setAverageProfitPerGame(double averageProfitPerGame) {
        this.averageProfitPerGame = averageProfitPerGame;
    }

    public ArrayList<Reward> getRewards() {
        return rewards;
    }

    public void setRewards(ArrayList<Reward> rewards) {
        this.rewards = rewards;
    }

    public ArrayList<PlayerGame> getPlayerGames() {
        return playerGames;
    }

    public void setPlayerGames(ArrayList<PlayerGame> playerGames) {
        this.playerGames = playerGames;
    }

    public PlayerGame getMostRecentGamePlayed(){

        Collections.sort(this.playerGames,new Comparator<PlayerGame>() {
            @Override
            public int compare(PlayerGame playerGame1, PlayerGame playerGame2) {
                if (playerGame1.getGameStartTime().before(playerGame2.getGameStartTime()))
                    return -1;
                if (playerGame1.getGameStartTime().after(playerGame2.getGameStartTime()))
                    return 1;

                return 0;
            }
        });
        return this.playerGames.get(this.playerGames.size()-1);
    }
}







