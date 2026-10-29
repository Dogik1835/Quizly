/*
 * QuestionTest.java
 * Unit testy doménového modelu otázok: vyhodnotenie odpovedí pre všetky
 * typy otázok (výber z možností, pravda/nepravda, otvorená), validácia
 * vstupných údajov, bodovanie podľa obťažnosti a parsovanie obťažnosti.
 */
package com.quizly.model;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    /**
     * Vytvorí testovaciu otázku s výberom z možností (správna je "Bratislava", index 1).
     */
    private MultipleChoiceQuestion capitalQuestion() {
        return new MultipleChoiceQuestion(1, "Hlavné mesto SR?", "Geografia", Difficulty.EASY,
                List.of("Košice", "Bratislava", "Žilina"), 1);
    }

    /**
     * Overí, že výber z možností akceptuje správnu odpoveď zadanú číslom aj písmenom.
     */
    @Test
    void multipleChoiceAcceptsNumberAndLetter() {
        MultipleChoiceQuestion q = capitalQuestion();
        assertTrue(q.isCorrect("2"));
        assertTrue(q.isCorrect(" b "));
        assertTrue(q.isCorrect("B"));
        assertFalse(q.isCorrect("1"));
        assertFalse(q.isCorrect("c"));
    }

    /**
     * Overí rozpoznanie neplatného formátu odpovede pri výbere z možností.
     */
    @Test
    void multipleChoiceRejectsInvalidFormat() {
        MultipleChoiceQuestion q = capitalQuestion();
        assertFalse(q.isValidAnswerFormat("4"));
        assertFalse(q.isValidAnswerFormat("0"));
        assertFalse(q.isValidAnswerFormat("Bratislava"));
        assertFalse(q.isValidAnswerFormat(""));
        assertFalse(q.isCorrect(null));
        assertTrue(q.isValidAnswerFormat("3"));
    }

    /**
     * Overí, že správna odpoveď sa vypíše v tvare "číslo) text".
     */
    @Test
    void multipleChoiceCorrectAnswerText() {
        assertEquals("2) Bratislava", capitalQuestion().getCorrectAnswerText());
    }

    /**
     * Overí validáciu pri vytváraní otázky s možnosťami (málo možností, zlý index, prázdna možnosť).
     */
    @Test
    void multipleChoiceValidation() {
        assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceQuestion(
                1, "Q", "K", Difficulty.EASY, List.of("Jediná"), 0));
        assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceQuestion(
                1, "Q", "K", Difficulty.EASY, List.of("A", "B"), 2));
        assertThrows(IllegalArgumentException.class, () -> new MultipleChoiceQuestion(
                1, "Q", "K", Difficulty.EASY, List.of("A", " "), 0));
    }

    /**
     * Overí, že otázka pravda/nepravda rozpozná rôzne zápisy odpovede.
     */
    @Test
    void trueFalseRecognizesVariousAnswers() {
        TrueFalseQuestion q = new TrueFalseQuestion(1, "Slnko je hviezda.", "Veda", Difficulty.EASY, true);
        assertTrue(q.isCorrect("p"));
        assertTrue(q.isCorrect("Pravda"));
        assertTrue(q.isCorrect("áno"));
        assertFalse(q.isCorrect("n"));
        assertFalse(q.isCorrect("možno"));
        assertFalse(q.isValidAnswerFormat("možno"));
        assertEquals("Pravda", q.getCorrectAnswerText());
    }

    /**
     * Overí, že otvorená otázka ignoruje veľkosť písmen, diakritiku a medzery
     * a akceptuje alternatívne odpovede.
     */
    @Test
    void openQuestionNormalizesAnswer() {
        OpenQuestion q = new OpenQuestion(1, "Najvyšší vrch SR?", "Geografia", Difficulty.MEDIUM,
                List.of("Gerlachovský štít", "Gerlach"));
        assertTrue(q.isCorrect("gerlachovsky   stit"));
        assertTrue(q.isCorrect("  GERLACH "));
        assertFalse(q.isCorrect("Lomnický štít"));
        assertFalse(q.isCorrect(null));
        assertEquals("Gerlachovský štít", q.getCorrectAnswerText());
    }

    /**
     * Overí, že otvorená otázka bez správnej odpovede sa nedá vytvoriť.
     */
    @Test
    void openQuestionRequiresAnswer() {
        assertThrows(IllegalArgumentException.class,
                () -> new OpenQuestion(1, "Q", "K", Difficulty.EASY, List.of()));
    }

    /**
     * Overí spoločnú validáciu otázok (ID, text, kategória) aj pri použití setterov.
     */
    @Test
    void commonValidation() {
        assertThrows(IllegalArgumentException.class,
                () -> new TrueFalseQuestion(0, "Q", "K", Difficulty.EASY, true));
        assertThrows(IllegalArgumentException.class,
                () -> new TrueFalseQuestion(1, " ", "K", Difficulty.EASY, true));
        assertThrows(IllegalArgumentException.class,
                () -> new TrueFalseQuestion(1, "Q", "", Difficulty.EASY, true));

        TrueFalseQuestion q = new TrueFalseQuestion(1, "Q", "K", Difficulty.EASY, true);
        assertThrows(IllegalArgumentException.class, () -> q.setText(""));
        q.setCategory("  Nová  ");
        assertEquals("Nová", q.getCategory());
    }

    /**
     * Overí, že body za otázku zodpovedajú jej obťažnosti.
     */
    @Test
    void pointsDependOnDifficulty() {
        TrueFalseQuestion q = new TrueFalseQuestion(1, "Q", "K", Difficulty.EASY, true);
        assertEquals(1, q.getPoints());
        q.setDifficulty(Difficulty.HARD);
        assertEquals(3, q.getPoints());
    }

    /**
     * Overí parsovanie obťažnosti z textu aj z čísla a chybu pri neznámej hodnote.
     */
    @Test
    void difficultyParse() {
        assertEquals(Difficulty.EASY, Difficulty.parse("easy"));
        assertEquals(Difficulty.MEDIUM, Difficulty.parse("2"));
        assertEquals(Difficulty.HARD, Difficulty.parse(" HARD "));
        assertThrows(IllegalArgumentException.class, () -> Difficulty.parse("4"));
        assertThrows(IllegalArgumentException.class, () -> Difficulty.parse("extreme"));
    }
}
