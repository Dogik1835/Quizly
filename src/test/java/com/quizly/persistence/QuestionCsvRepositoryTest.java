/*
 * QuestionCsvRepositoryTest.java
 * Unit testy ukladania otázok do CSV súboru: uloženie a opätovné načítanie
 * všetkých typov otázok, správanie pri chýbajúcom súbore a preskočenie
 * chybných riadkov s upozornením. Používa dočasný priečinok (@TempDir).
 */
package com.quizly.persistence;

import com.quizly.model.Difficulty;
import com.quizly.model.MultipleChoiceQuestion;
import com.quizly.model.OpenQuestion;
import com.quizly.model.Question;
import com.quizly.model.TrueFalseQuestion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionCsvRepositoryTest {

    @TempDir
    Path tempDir;

    /**
     * Overí, že všetky typy otázok sa po uložení a načítaní zhodujú s pôvodnými.
     */
    @Test
    void saveAndLoadAllQuestionTypes() throws DataAccessException {
        QuestionCsvRepository repository = new QuestionCsvRepository(tempDir.resolve("sub/questions.csv"));
        List<Question> original = List.of(
                new MultipleChoiceQuestion(1, "Čo platí; vyber?", "Veda", Difficulty.HARD,
                        List.of("A \"x\"", "B", "C"), 2),
                new TrueFalseQuestion(2, "Zem je guľatá.", "Veda", Difficulty.EASY, true),
                new OpenQuestion(3, "Hlavné mesto Francúzska?", "Geografia", Difficulty.MEDIUM,
                        List.of("Paríž", "Paris")));

        repository.saveAll(original);
        List<Question> loaded = repository.loadAll();

        assertEquals(3, loaded.size());
        assertTrue(repository.getWarnings().isEmpty());

        MultipleChoiceQuestion mc = assertInstanceOf(MultipleChoiceQuestion.class, loaded.get(0));
        assertEquals("Čo platí; vyber?", mc.getText());
        assertEquals(List.of("A \"x\"", "B", "C"), mc.getOptions());
        assertEquals(2, mc.getCorrectIndex());
        assertEquals(Difficulty.HARD, mc.getDifficulty());

        TrueFalseQuestion tf = assertInstanceOf(TrueFalseQuestion.class, loaded.get(1));
        assertTrue(tf.getCorrectAnswer());

        OpenQuestion open = assertInstanceOf(OpenQuestion.class, loaded.get(2));
        assertEquals(List.of("Paríž", "Paris"), open.getAcceptedAnswers());
        assertEquals("Geografia", open.getCategory());
    }

    /**
     * Overí, že neexistujúci súbor znamená prázdny zoznam otázok (nie chybu).
     */
    @Test
    void missingFileGivesEmptyList() throws DataAccessException {
        assertTrue(new QuestionCsvRepository(tempDir.resolve("neexistuje.csv")).loadAll().isEmpty());
    }

    /**
     * Overí, že chybné riadky sa preskočia, zapíšu sa upozornenia a platné riadky sa načítajú.
     */
    @Test
    void invalidLinesAreSkippedWithWarning() throws IOException, DataAccessException {
        Path file = tempDir.resolve("questions.csv");
        Files.writeString(file, String.join("\n",
                "# komentár",
                "typ;id;kategoria;obtiaznost;text;spravna_odpoved;moznosti",
                "TF;1;Veda;EASY;Platná otázka;true;",
                "XX;2;Veda;EASY;Neznámy typ;true;",
                "MC;3;Veda;EASY;Zlý index;9;A|B",
                "TF;abc;Veda;EASY;Zlé ID;true;",
                "",
                "OPEN;5;Veda;MEDIUM;Platná otvorená;odpoveď"), StandardCharsets.UTF_8);

        QuestionCsvRepository repository = new QuestionCsvRepository(file);
        List<Question> loaded = repository.loadAll();

        assertEquals(2, loaded.size());
        assertEquals(3, repository.getWarnings().size());
        assertTrue(repository.getWarnings().get(0).startsWith("Riadok 4"));
    }

    /**
     * Overí, že pribalený súbor s predvolenými otázkami sa načíta celý a bez chýb.
     */
    @Test
    void defaultQuestionsResourceIsValid() throws IOException, DataAccessException {
        Path file = tempDir.resolve("default.csv");
        try (var in = getClass().getResourceAsStream("/default_questions.csv")) {
            Files.copy(in, file);
        }
        QuestionCsvRepository repository = new QuestionCsvRepository(file);
        List<Question> loaded = repository.loadAll();
        assertTrue(repository.getWarnings().isEmpty(), () -> repository.getWarnings().toString());
        assertTrue(loaded.size() >= 30);
    }
}
