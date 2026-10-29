package com.quizly;

public class Result {
    private String playerName;
    private int score;
    private int total;

    public Result(String playerName, int score, int total) {
        this.playerName = playerName;
        this.score = score;
        this.total = total;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }
}
