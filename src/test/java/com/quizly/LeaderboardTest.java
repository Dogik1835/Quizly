// LeaderboardTest.java
// Testy rebríčka: zoradenie najlepších výsledkov a výsledky jedného hráča.
package com.quizly;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LeaderboardTest {

    // Vytvorí rebríček s 3 výsledkami
    private Leaderboard createLeaderboard() {
        List<Result> results = new ArrayList<>();
        results.add(new Result("Anna", 3, 5));
        results.add(new Result("Ben", 5, 5));
        results.add(new Result("Anna", 4, 5));
        return new Leaderboard(results);
    }

    // Najlepšie výsledky sú zoradené od najvyššieho skóre
    @Test
    void top() {
        List<Result> top = createLeaderboard().getTop(2);
        assertEquals(2, top.size());
        assertEquals("Ben", top.get(0).getPlayerName());
        assertEquals(4, top.get(1).getScore());
    }

    // Výsledky hráča sa hľadajú bez ohľadu na veľké/malé písmená
    @Test
    void resultsOfPlayer() {
        Leaderboard leaderboard = createLeaderboard();
        assertEquals(2, leaderboard.getResultsOf("anna").size());
        assertEquals(0, leaderboard.getResultsOf("Cyril").size());
    }

    // Pridanie výsledku zväčší rebríček
    @Test
    void addResult() {
        Leaderboard leaderboard = createLeaderboard();
        leaderboard.addResult(new Result("Cyril", 1, 5));
        assertEquals(4, leaderboard.getResults().size());
    }
}
