// Result.java
// Výsledok jednej hry: meno hráča, počet správnych odpovedí a počet otázok.
package com.quizly;

public class Result {
    private String playerName;
    private int score;
    private int total;

    // Konštruktor - vytvorí výsledok hry
    public Result(String playerName, int score, int total) {
        this.playerName = playerName;
        this.score = score;
        this.total = total;
    }

    // Vráti meno hráča
    public String getPlayerName() {
        return playerName;
    }

    // Nastaví meno hráča
    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    // Vráti počet správnych odpovedí
    public int getScore() {
        return score;
    }

    // Nastaví počet správnych odpovedí
    public void setScore(int score) {
        this.score = score;
    }

    // Vráti počet otázok v hre
    public int getTotal() {
        return total;
    }

    // Nastaví počet otázok v hre
    public void setTotal(int total) {
        this.total = total;
    }
}
