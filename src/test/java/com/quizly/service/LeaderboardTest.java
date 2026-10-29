/*
 * LeaderboardTest.java
 * Unit testy rebríčka: poradie najlepších výsledkov, filtrovanie podľa
 * kategórie, história a najlepší výsledok hráča a štatistické výpočty.
 */
package com.quizly.service;

import com.quizly.model.GameResult;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LeaderboardTest {

    private Leaderboard leaderboard;
    private GameResult annaScience;
    private GameResult benScience;
    private GameResult annaHistory;
    private GameResult cyrilHistory;

    /**
     * Vytvorí čas hry v zadaný deň októbra 2026 (na testovanie poradia podľa dátumu).
     */
    private static LocalDateTime day(int day) {
        return LocalDateTime.of(2026, 10, day, 12, 0);
    }

    /**
     * Pred každým testom pripraví rebríček so 4 výsledkami.
     */
    @BeforeEach
    void setUp() {
        annaScience = new GameResult("Anna", "Veda", 8, 10, 4, 5, day(1));      // 80 %
        benScience = new GameResult("Ben", "Veda", 10, 10, 5, 5, day(2));       // 100 %
        annaHistory = new GameResult("Anna", "História", 4, 10, 2, 5, day(3));  // 40 %
        cyrilHistory = new GameResult("Cyril", "História", 8, 10, 4, 5, day(4)); // 80 %, neskôr ako Anna
        leaderboard = new Leaderboard(List.of(annaScience, benScience, annaHistory, cyrilHistory));
    }

    /**
     * Overí zoradenie podľa úspešnosti a pri zhode podľa skoršieho dátumu.
     */
    @Test
    void topIsSortedByPercentageThenDate() {
        assertEquals(List.of(benScience, annaScience, cyrilHistory, annaHistory), leaderboard.top(10));
        assertEquals(List.of(benScience, annaScience), leaderboard.top(2));
    }

    /**
     * Overí rebríček pre jednu kategóriu.
     */
    @Test
    void topByCategory() {
        assertEquals(List.of(cyrilHistory, annaHistory), leaderboard.topByCategory("história", 10));
        assertTrue(leaderboard.topByCategory("Šport", 10).isEmpty());
    }

    /**
     * Overí históriu hráča (od najnovšej hry) a jeho najlepší výsledok.
     */
    @Test
    void historyAndBestOfPlayer() {
        assertEquals(List.of(annaHistory, annaScience), leaderboard.historyOf("anna"));
        assertEquals(annaScience, leaderboard.bestOf("Anna").orElseThrow());
        assertTrue(leaderboard.bestOf("Nikto").isEmpty());
    }

    /**
     * Overí štatistiky: priemernú úspešnosť, priemer hráčov a počet hier v kategóriách.
     */
    @Test
    void statistics() {
        assertEquals(75.0, leaderboard.averagePercentage().orElseThrow(), 0.001);

        Map<String, Double> byPlayer = leaderboard.averagePercentageByPlayer();
        assertEquals(60.0, byPlayer.get("Anna"), 0.001);
        assertEquals(100.0, byPlayer.get("Ben"), 0.001);

        assertEquals(Map.of("Veda", 2L, "História", 2L), leaderboard.gamesPerCategory());
        assertTrue(new Leaderboard().averagePercentage().isEmpty());
    }
}
