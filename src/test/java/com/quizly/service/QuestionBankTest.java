/*
 * QuestionBankTest.java
 * Unit testy zásobníka otázok: pridávanie a mazanie, kontrola duplicitných ID,
 * filtrovanie podľa kategórie a obťažnosti, vyhľadávanie, počítanie
 * podľa kategórií a náhodný výber otázok do hry.
 */
package com.quizly.service;

import com.quizly.model.Difficulty;
import com.quizly.model.OpenQuestion;
import com.quizly.model.Question;
import com.quizly.model.TrueFalseQuestion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionBankTest {

    private QuestionBank bank;

    /**
     * Pred každým testom pripraví zásobník so 4 otázkami v dvoch kategóriách.
     */
    @BeforeEach
    void setUp() {
        bank = new QuestionBank(List.of(
                new TrueFalseQuestion(1, "Slnko je hviezda.", "Veda", Difficulty.EASY, true),
                new TrueFalseQuestion(2, "Voda vrie pri 50 °C.", "Veda", Difficulty.MEDIUM, false),
                new OpenQuestion(3, "Hlavné mesto Francúzska?", "Geografia", Difficulty.EASY, List.of("Paríž")),
                new OpenQuestion(4, "Najvyšší vrch sveta?", "Geografia", Difficulty.HARD, List.of("Mount Everest"))));
    }

    /**
     * Overí, že otázku s existujúcim ID nie je možné pridať.
     */
    @Test
    void duplicateIdIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> bank.add(new TrueFalseQuestion(1, "Iná", "Veda", Difficulty.EASY, true)));
        assertEquals(4, bank.size());
    }

    /**
     * Overí mazanie otázky a nájdenie podľa ID.
     */
    @Test
    void removeAndFind() {
        assertTrue(bank.findById(3).isPresent());
        assertTrue(bank.remove(3));
        assertFalse(bank.findById(3).isPresent());
        assertFalse(bank.remove(3));
        assertEquals(3, bank.size());
    }

    /**
     * Overí, že ďalšie voľné ID je o jedna väčšie ako najvyššie existujúce.
     */
    @Test
    void nextIdFollowsMaximum() {
        assertEquals(5, bank.nextId());
        assertEquals(1, new QuestionBank().nextId());
    }

    /**
     * Overí filtrovanie podľa kategórie (bez ohľadu na veľkosť písmen) a obťažnosti.
     */
    @Test
    void filterByCategoryAndDifficulty() {
        assertEquals(2, bank.filter("veda", null).size());
        assertEquals(2, bank.filter(null, Difficulty.EASY).size());
        List<Question> result = bank.filter("Geografia", Difficulty.HARD);
        assertEquals(1, result.size());
        assertEquals(4, result.get(0).getId());
        assertEquals(4, bank.filter(null, null).size());
    }

    /**
     * Overí vyhľadávanie v texte otázok.
     */
    @Test
    void searchInText() {
        assertEquals(1, bank.search("HVIEZDA").size());
        assertEquals(1, bank.search("mesto").size());
        assertTrue(bank.search("neexistuje").isEmpty());
    }

    /**
     * Overí zoznam kategórií a počty otázok v nich.
     */
    @Test
    void categoriesAndCounts() {
        assertEquals(Set.of("Veda", "Geografia"), bank.getCategories());
        Map<String, Long> counts = bank.countByCategory();
        assertEquals(2L, counts.get("Veda"));
        assertEquals(2L, counts.get("Geografia"));
    }

    /**
     * Overí, že náhodný výber vráti požadovaný počet rôznych otázok
     * a pri nedostatku otázok vráti všetky vyhovujúce.
     */
    @Test
    void pickRandomRespectsCountAndFilter() {
        List<Question> picked = bank.pickRandom(null, null, 3, new Random(42));
        assertEquals(3, picked.size());
        assertEquals(3, new HashSet<>(picked).size());

        List<Question> onlyScience = bank.pickRandom("Veda", null, 10, new Random(42));
        assertEquals(2, onlyScience.size());
        assertTrue(onlyScience.stream().allMatch(q -> q.getCategory().equals("Veda")));

        assertThrows(IllegalArgumentException.class, () -> bank.pickRandom(null, null, 0, new Random()));
    }
}
