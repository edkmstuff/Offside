/**
 * Created by KFIR on 11/14/2016.
 */

public class PlayerScore {

    @com.google.gson.annotations.SerializedName("GameId")
    private String _gameId;

    @com.google.gson.annotations.SerializedName("GameTitle")
    private String _gameTitle;

    @com.google.gson.annotations.SerializedName("UserName")
    private String _userName;

    @com.google.gson.annotations.SerializedName("Score")
    private int _score;

    @com.google.gson.annotations.SerializedName("Position")
    private int _position;

    @com.google.gson.annotations.SerializedName("TotalPlayers")
    private int _totalPlayers;

    @com.google.gson.annotations.SerializedName("TotalOpenQuestions")
    private int _totalOpenQuestions;

    @com.google.gson.annotations.SerializedName("LeaderScore")
    private int _leaderScore;


    public PlayerScore() {

    }

    public String getGameId() {
        return _gameId;
    }

    public String getGameTitle() {
        return _gameTitle;
    }

    public String getUserName() {
        return _userName;
    }

    public int getScore() {
        return _score;
    }

    public int getPosition() {
        return _position;
    }

    public int getTotalPlayers() {
        return _totalPlayers;
    }

    public int getTotalOpenQuestions() {
        return _totalOpenQuestions;
    }

    public int getLeaderScore() {
        return _leaderScore;
    }

}



