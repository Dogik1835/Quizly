/*
 * GameResultTest.java
 * Unit testy výsledku hry: výpočet percentuálnej úspešnosti
 * a validácia nekonzistentných údajov.
 */
package com.quizly.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GameResultTest {

    private static final LocalDateTime NOW = LocalDateTime.of(2026, 10, 1, 12, 0);

    /**
     * Overí výpočet percentuálnej úspešnosti.
     */
    @Test
    void percentageIsComputedFromScore() {
        GameResult result = new GameResult("Anna", "Veda", 3, 4, 2, 3, NOW);
        assertEquals(75.0, result.getPercentage(), 0.001);
    }

    /**
     * Overí, že nezmyselné hodnoty (skóre nad maximum, záporné, prázdne meno) vyhodia výnimku.
     */
    @Test
    void invalidValuesAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new GameResult("Anna", "Veda", 5, 4, 2, 3, NOW));
        assertThrows(IllegalArgumentException.class, () -> new GameResult("Anna", "Veda", -1, 4, 2, 3, NOW));
        assertThrows(IllegalArgumentException.class, () -> new GameResult(" ", "Veda", 1, 4, 1, 3, NOW));
        assertThrows(IllegalArgumentException.class, () -> new GameResult("Anna", "Veda", 1, 4, 4, 3, NOW));
        assertThrows(IllegalArgumentException.class, () -> new GameResult("Anna", "Veda", 0, 0, 0, 0, NOW));
    }
}
