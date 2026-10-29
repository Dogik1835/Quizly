/*
 * ResultCsvRepositoryTest.java
 * Unit testy ukladania histórie výsledkov do CSV súboru: uloženie
 * a opätovné načítanie výsledkov a preskočenie riadku s neplatným dátumom.
 */
package com.quizly.persistence;

import com.quizly.model.GameResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ResultCsvRepositoryTest {

    @TempDir
    Path tempDir;

    /**
     * Overí, že výsledky sa po uložení a načítaní zhodujú s pôvodnými.
     */
    @Test
    void saveAndLoad() throws DataAccessException {
        ResultCsvRepository repository = new ResultCsvRepository(tempDir.resolve("results.csv"));
        LocalDateTime time = LocalDateTime.of(2026, 10, 29, 18, 30, 15);
        repository.saveAll(List.of(
                new GameResult("Anna; Nováková", "Veda", 7, 10, 4, 5, time),
                new GameResult("Ben", GameResult.ALL_CATEGORIES, 3, 6, 2, 3, time.plusDays(1))));

        List<GameResult> loaded = repository.loadAll();

        assertEquals(2, loaded.size());
        GameResult first = loaded.get(0);
        assertEquals("Anna; Nováková", first.getPlayerName());
        assertEquals("Veda", first.getCategory());
        assertEquals(7, first.getScore());
        assertEquals(10, first.getMaxScore());
        assertEquals(4, first.getCorrectAnswers());
        assertEquals(5, first.getTotalQuestions());
        assertEquals(time, first.getPlayedAt());
        assertEquals(GameResult.ALL_CATEGORIES, loaded.get(1).getCategory());
    }

    /**
     * Overí, že riadok s neplatným dátumom sa preskočí a zapíše sa upozornenie.
     */
    @Test
    void invalidDateIsSkipped() throws IOException, DataAccessException {
        Path file = tempDir.resolve("results.csv");
        Files.writeString(file, String.join("\n",
                "hrac;kategoria;body;max_body;spravne;pocet_otazok;datum",
                "Anna;Veda;5;10;3;5;včera",
                "Ben;Veda;5;10;3;5;2026-10-29T10:00:00"), StandardCharsets.UTF_8);

        ResultCsvRepository repository = new ResultCsvRepository(file);
        assertEquals(1, repository.loadAll().size());
        assertEquals(1, repository.getWarnings().size());
    }
}
