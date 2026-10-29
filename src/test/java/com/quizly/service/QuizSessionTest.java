/*
 * QuizSessionTest.java
 * Unit testy priebehu hry: postupné odpovedanie na otázky, počítanie bodov
 * podľa obťažnosti, evidencia chýb a vytvorenie výsledku po skončení hry.
 */
package com.quizly.service;

import com.quizly.model.Difficulty;
import com.quizly.model.GameResult;
import com.quizly.model.MultipleChoiceQuestion;
import com.quizly.model.Question;
import com.quizly.model.TrueFalseQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizSessionTest {

    private Question easy;
    private Question hard;
    private QuizSession session;

    /**
     * Pred každým testom pripraví hru s ľahkou (1 b.) a ťažkou (3 b.) otázkou.
     */
    @BeforeEach
    void setUp() {
        easy = new TrueFalseQuestion(1, "Slnko je hviezda.", "Veda", Difficulty.EASY, true);
        hard = new MultipleChoiceQuestion(2, "2 + 2 = ?", "Veda", Difficulty.HARD, List.of("3", "4", "5"), 1);
        session = new QuizSession("Anna", "Veda", List.of(easy, hard));
    }

    /**
     * Overí počítanie bodov a správnych odpovedí počas hry.
     */
    @Test
    void scoringFollowsDifficulty() {
        assertEquals(4, session.getMaxScore());
        assertTrue(session.submitAnswer("p"));
        assertEquals(1, session.getScore());
        assertFalse(session.submitAnswer("1"));
        assertEquals(1, session.getScore());
        assertEquals(1, session.getCorrectCount());
        assertEquals(List.of(hard), session.getMistakes());
        assertTrue(session.isFinished());
    }

    /**
     * Overí, že otázky idú v poradí a číslovanie pre hráča začína od 1.
     */
    @Test
    void questionsAreAskedInOrder() {
        assertEquals(1, session.getCurrentQuestionNumber());
        assertEquals(easy, session.getCurrentQuestion());
        session.submitAnswer("p");
        assertEquals(2, session.getCurrentQuestionNumber());
        assertEquals(hard, session.getCurrentQuestion());
    }

    /**
     * Overí, že po skončení hry sa už nedá odpovedať a výsledok obsahuje správne údaje.
     */
    @Test
    void resultAfterFinish() {
        session.submitAnswer("p");
        session.submitAnswer("2");
        assertThrows(IllegalStateException.class, () -> session.submitAnswer("1"));

        LocalDateTime time = LocalDateTime.of(2026, 10, 1, 10, 0);
        GameResult result = session.toResult(time);
        assertEquals("Anna", result.getPlayerName());
        assertEquals("Veda", result.getCategory());
        assertEquals(4, result.getScore());
        assertEquals(4, result.getMaxScore());
        assertEquals(2, result.getCorrectAnswers());
        assertEquals(100.0, result.getPercentage(), 0.001);
        assertEquals(time, result.getPlayedAt());
    }

    /**
     * Overí, že výsledok nie je možné vytvoriť pred koncom hry.
     */
    @Test
    void resultBeforeFinishFails() {
        assertThrows(IllegalStateException.class, () -> session.toResult());
    }

    /**
     * Overí, že bez kategórie sa hra označí ako "Všetky" a že hru nemožno začať bez otázok.
     */
    @Test
    void defaultsAndValidation() {
        QuizSession mixed = new QuizSession("Ben", null, List.of(easy));
        mixed.submitAnswer("n");
        assertEquals(GameResult.ALL_CATEGORIES, mixed.toResult().getCategory());

        assertThrows(IllegalArgumentException.class, () -> new QuizSession("Ben", null, List.of()));
        assertThrows(IllegalArgumentException.class, () -> new QuizSession(" ", null, List.of(easy)));
    }
}
