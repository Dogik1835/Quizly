// FileManagerTest.java
// Testy ukladania a načítania zo súborov. Súbory sa vytvárajú v dočasnom priečinku.
package com.quizly;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FileManagerTest {

    @TempDir
    Path tempDir;

    // Uložené výsledky sa po načítaní zhodujú s pôvodnými
    @Test
    void saveAndLoadResults() {
        String file = tempDir.resolve("results.csv").toString();
        FileManager.saveResults(file, List.of(new Result("Anna", 3, 5), new Result("Ben", 5, 5)));

        List<Result> loaded = FileManager.loadResults(file);
        assertEquals(2, loaded.size());
        assertEquals("Anna", loaded.get(0).getPlayerName());
        assertEquals(3, loaded.get(0).getScore());
        assertEquals(5, loaded.get(1).getTotal());
    }

    // Ak súbor s výsledkami neexistuje, vráti sa prázdny zoznam
    @Test
    void missingResultsFile() {
        assertTrue(FileManager.loadResults(tempDir.resolve("nic.csv").toString()).isEmpty());
    }

    // Načítajú sa oba typy otázok a chybný riadok sa preskočí
    @Test
    void loadQuestions() throws IOException {
        Path file = tempDir.resolve("questions.csv");
        Files.write(file, List.of(
                "CHOICE;Geografia;Hlavné mesto SR?;Košice,Bratislava;2",
                "TF;Veda;Slnko je hviezda.;true",
                "CHOICE;Zlý riadok"));

        List<Question> questions = FileManager.loadQuestions(file.toString());
        assertEquals(2, questions.size());
        assertTrue(questions.get(0) instanceof ChoiceQuestion);
        assertTrue(questions.get(1) instanceof TrueFalseQuestion);
        assertTrue(questions.get(0).isCorrect("2"));
    }
}
