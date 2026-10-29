/*
 * GameResult.java
 * Výsledok jednej odohranej hry (kvízu). Uchováva meno hráča, kategóriu,
 * získané a maximálne možné body, počet správnych odpovedí a čas hry.
 * Objekt je nemenný – po vytvorení sa výsledok už nedá zmeniť.
 */
package com.quizly.model;

import java.time.LocalDateTime;
import java.util.Objects;

public class GameResult {

    /** Označenie kategórie pre hru, v ktorej boli otázky zo všetkých kategórií. */
    public static final String ALL_CATEGORIES = "Všetky";

    private final String playerName;
    private final String category;
    private final int score;
    private final int maxScore;
    private final int correctAnswers;
    private final int totalQuestions;
    private final LocalDateTime playedAt;

    /**
     * Vytvorí výsledok hry a overí, že údaje dávajú zmysel
     * (napr. skóre nie je väčšie ako maximum, počty nie sú záporné).
     *
     * @throws IllegalArgumentException pri nekonzistentných údajoch
     */
    public GameResult(String playerName, String category, int score, int maxScore,
                      int correctAnswers, int totalQuestions, LocalDateTime playedAt) {
        if (playerName == null || playerName.isBlank()) {
            throw new IllegalArgumentException("Meno hráča nesmie byť prázdne.");
        }
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("Kategória nesmie byť prázdna.");
        }
        if (totalQuestions <= 0 || maxScore <= 0) {
            throw new IllegalArgumentException("Hra musí obsahovať aspoň jednu otázku.");
        }
        if (score < 0 || score > maxScore) {
            throw new IllegalArgumentException("Skóre musí byť v rozsahu 0 až " + maxScore + ".");
        }
        if (correctAnswers < 0 || correctAnswers > totalQuestions) {
            throw new IllegalArgumentException("Počet správnych odpovedí je mimo rozsahu.");
        }
        this.playerName = playerName.trim();
        this.category = category.trim();
        this.score = score;
        this.maxScore = maxScore;
        this.correctAnswers = correctAnswers;
        this.totalQuestions = totalQuestions;
        this.playedAt = Objects.requireNonNull(playedAt, "Čas hry nesmie byť null.");
    }

    /**
     * Vráti meno hráča.
     */
    public String getPlayerName() {
        return playerName;
    }

    /**
     * Vráti kategóriu, z ktorej boli otázky (alebo "Všetky").
     */
    public String getCategory() {
        return category;
    }

    /**
     * Vráti počet získaných bodov.
     */
    public int getScore() {
        return score;
    }

    /**
     * Vráti maximálny možný počet bodov v tejto hre.
     */
    public int getMaxScore() {
        return maxScore;
    }

    /**
     * Vráti počet správne zodpovedaných otázok.
     */
    public int getCorrectAnswers() {
        return correctAnswers;
    }

    /**
     * Vráti celkový počet otázok v hre.
     */
    public int getTotalQuestions() {
        return totalQuestions;
    }

    /**
     * Vráti dátum a čas, kedy bola hra odohraná.
     */
    public LocalDateTime getPlayedAt() {
        return playedAt;
    }

    /**
     * Vypočíta úspešnosť hráča v percentách (získané body / maximum * 100).
     */
    public double getPercentage() {
        return score * 100.0 / maxScore;
    }

    /**
     * Vráti stručný textový popis výsledku.
     */
    @Override
    public String toString() {
        return String.format("%s – %d/%d bodov (%.1f %%), kategória: %s",
                playerName, score, maxScore, getPercentage(), category);
    }
}
