// QuestionTest.java
// Testy, či otázky správne vyhodnocujú odpovede.
package com.quizly;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QuestionTest {

    // Otázka s možnosťami: správne je len číslo správnej možnosti
    @Test
    void choiceQuestion() {
        ChoiceQuestion q = new ChoiceQuestion("Hlavné mesto SR?", "Geografia",
                List.of("Košice", "Bratislava", "Žilina"), 2);
        assertTrue(q.isCorrect("2"));
        assertTrue(q.isCorrect(" 2 "));
        assertFalse(q.isCorrect("1"));
        assertFalse(q.isCorrect("Bratislava"));
        assertEquals("Bratislava", q.getCorrectAnswer());
    }

    // Otázka áno/nie: "a" pri pravdivom tvrdení, "n" pri nepravdivom
    @Test
    void trueFalseQuestion() {
        TrueFalseQuestion pravda = new TrueFalseQuestion("Slnko je hviezda.", "Veda", true);
        assertTrue(pravda.isCorrect("a"));
        assertTrue(pravda.isCorrect("A"));
        assertFalse(pravda.isCorrect("n"));
        assertFalse(pravda.isCorrect("neviem"));

        TrueFalseQuestion nepravda = new TrueFalseQuestion("Mesiac je hviezda.", "Veda", false);
        assertTrue(nepravda.isCorrect("n"));
        assertEquals("nie", nepravda.getCorrectAnswer());
    }
}
