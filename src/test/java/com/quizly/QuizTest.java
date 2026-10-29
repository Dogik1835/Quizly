// QuizTest.java
// Testy výberu kategórií a náhodných otázok.
package com.quizly;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuizTest {

    // Vytvorí kvíz so 4 otázkami v 2 kategóriách
    private Quiz createQuiz() {
        return new Quiz(List.of(
                new TrueFalseQuestion("Otázka 1", "Veda", true),
                new TrueFalseQuestion("Otázka 2", "Veda", false),
                new TrueFalseQuestion("Otázka 3", "Šport", true),
                new TrueFalseQuestion("Otázka 4", "Geografia", true)));
    }

    // Kategórie sa nesmú opakovať a majú byť zoradené
    @Test
    void categories() {
        assertEquals(List.of("Geografia", "Veda", "Šport"), createQuiz().getCategories());
    }

    // Pri výbere kategórie sa vrátia len otázky z nej
    @Test
    void randomQuestionsFromCategory() {
        List<Question> questions = createQuiz().getRandomQuestions("Veda", 5);
        assertEquals(2, questions.size());
        assertTrue(questions.stream().allMatch(q -> q.getCategory().equals("Veda")));
    }

    // Bez kategórie sa berú všetky otázky, ale najviac toľko, koľko chceme
    @Test
    void randomQuestionsLimit() {
        assertEquals(3, createQuiz().getRandomQuestions(null, 3).size());
        assertEquals(4, createQuiz().getRandomQuestions(null, 10).size());
    }
}
